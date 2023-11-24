package comumer.i200784;



import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatDetailsActivity extends AppCompatActivity {

    private static final int VIDEO_REQUEST = 2;
    TextView send, contactName, status;
    private static final int IMAGE_REQUEST = 1;
    EditText messageText;
    ImageView send_picture;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;

    private List<MessageModel> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private Uri selectedImageUri;
    private ScreenshotObserver screenshotObserver;
    Context context;
    RecyclerView recyclerView;
    public static String senderUid, receiverUid, receiverProfileUrl;
    Handler handler = new Handler();
    int intervalMilliseconds = 2000;

    public void fetchMessagesPeriodically() {
        // Clear the existing messages
        messageList.clear();
        messageAdapter = new MessageAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Execute the GetMessageTask to get messages
        new GetMessageTask(messageAdapter.getMessageList()) {
            @Override
            protected void onMessagesLoaded(List<MessageModel> messages) {
                // Update the UI or perform any action after messages are loaded
                messageAdapter.notifyDataSetChanged();
            }
        }.execute(senderUid, receiverUid);

        // Schedule the next execution after the specified interval
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchMessagesPeriodically();
            }
        }, intervalMilliseconds);
    }

    // To start the periodic updates, call this function
    private void startMessageUpdates() {
        fetchMessagesPeriodically();
    }

    // To stop the periodic updates, call this function
    private void stopMessageUpdates() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_details);

        screenshotObserver = new ScreenshotObserver(new Handler());

        // Register the ContentObserver to listen for changes in the MediaStore
        ContentResolver contentResolver = getContentResolver();
        contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                screenshotObserver
        );

        send = findViewById(R.id.send);
        status = findViewById(R.id.status);
        messageText = findViewById(R.id.messageText);
        contactName=findViewById(R.id.contactName);
        send_picture = findViewById(R.id.send_picture);
        ImageView capture  = findViewById(R.id.capture_picture);

        capture.setOnClickListener(view -> showMediaSelectionDialog(IMAGE_REQUEST));

        contactName.setText(getIntent().getStringExtra("receiverUsername"));

        messageAdapter = new MessageAdapter(this, messageList);
        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        receiverUid = getIntent().getStringExtra("receiverUid");
        receiverProfileUrl = getIntent().getStringExtra("receiverProfileUrl");
        senderUid = User.currentUser.getUid();


        new GetMessageTask(messageList) {
            @Override
            protected void onMessagesLoaded(List<MessageModel> messageList) {
                // Update the UI or perform any action after messages are loaded
                messageAdapter.notifyDataSetChanged(); // Assuming messageAdapter is already initialized
            }
        }.execute(senderUid, receiverUid);


        send.setOnClickListener(v -> {
            // Inside your ChatDetailsActivity class

            send.setOnClickListener(task -> {
                // Get the message text from the EditText
                String message = messageText.getText().toString().trim();

                if (!message.isEmpty()) {
                    // Execute the AsyncTask to send the message
                    new SendMessageTask() {
                        @Override
                        protected void onMessageSent(String message) {
                            // Handle the result, e.g., show a toast
                            Toast.makeText(ChatDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }.execute(senderUid, receiverUid, message, receiverProfileUrl, "text");

                    messageList.clear();
                    messageAdapter = new MessageAdapter(this, messageList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(messageAdapter);

                    new GetMessageTask(messageList) {
                        @Override
                        protected void onMessagesLoaded(List<MessageModel> messageList) {
                            // Update the UI or perform any action after messages are loaded
                            messageAdapter.notifyDataSetChanged(); // Assuming messageAdapter is already initialized
                        }
                    }.execute(senderUid, receiverUid);

                    // Clear the message input field
                    messageText.setText("");
                    sendNotification(message, receiverUid, senderUid, User.currentUser.getName());
                } else {
                    Toast.makeText(ChatDetailsActivity.this, "Please enter a message to send.", Toast.LENGTH_SHORT).show();
                }
            });

        });

        startMessageUpdates();






        send_picture.setOnClickListener(v -> {
            // Open the image gallery
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });

        // close screen icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_back_to_chat);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // voice call icon
        ImageView voiceCallIcon = findViewById(R.id.start_voice_call);
        voiceCallIcon.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, VoiceCallActivity.class);
            startActivity(intent);
        });

        // video call icon
        ImageView videoCallIcon = findViewById(R.id.start_video_call);
        videoCallIcon.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, VideoCallActivity.class);
            startActivity(intent);
        });

    }

    private void showMediaSelectionDialog(int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Media Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Gallery option selected
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    if (requestCode == IMAGE_REQUEST) {
                        galleryIntent.setType("image/*");
                    } else if (requestCode == VIDEO_REQUEST) {
                        galleryIntent.setType("video/*");
                    }
                    startActivityForResult(galleryIntent, requestCode);
                    break;
                case 1:
                    if (requestCode == IMAGE_REQUEST) {
                        Intent cameraIntent = new Intent(ChatDetailsActivity.this, CaptureActivity.class);
                        startActivityForResult(cameraIntent,requestCode);
                        Toast.makeText(this, "Camera option selected", Toast.LENGTH_SHORT).show();
                    } else if (requestCode == VIDEO_REQUEST) {
                        Intent videoIntent = new Intent(ChatDetailsActivity.this, Video.class);
                        startActivityForResult(videoIntent,requestCode);
                        Toast.makeText(this, "Video option selected", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                uploadImageToStorage(selectedImageUri);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == VIDEO_REQUEST) {
                Uri selectedVideoUri = data.getData();
                Toast.makeText(this, "Video selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    

    private void uploadImageToStorage(Uri imageUri) {
        // Create a unique document ID for the image
        String imageId = UUID.randomUUID().toString();

        // Reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Reference to the specific image file in Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + imageId);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Register observers to listen for the upload task
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // The image was uploaded successfully. Now, retrieve the download URL.
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                sendMessageWithImageUrl(imageUrl);
            }).addOnFailureListener(e -> {
                // Handle the failure to retrieve the download URL
                Toast.makeText(this, "Failed to retrieve download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Handle the failure to upload the image
            Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void sendMessageWithImageUrl(String imageUrl) {
        // Now that you have the image URL, you can send it as a message
        MessageModel messageModel = new MessageModel(senderUid, receiverUid, imageUrl, "img");
        messageModel.setSenderProfileUrl(receiverProfileUrl);

        new SendMessageTask() {
            @Override
            protected void onMessageSent(String message) {
                // Handle the result, e.g., show a toast
                Toast.makeText(ChatDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }.execute(senderUid, receiverUid, imageUrl, receiverProfileUrl, "img");

        sendNotification("Image",receiverUid, senderUid, User.currentUser.getName());
        Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
    }

    public static void sendNotification(String message, String receiverUid, String currentUid, String currentUsername){

        // current username, message, currentUserId, otherUserToken
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        DocumentReference userDocument = usersCollection.document(receiverUid);

        userDocument.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot document = task1.getResult();
                if (document.exists()) {
                    String fcmToken = document.getString("fcmtoken");

                    try {
                        JSONObject object = new JSONObject();

                        JSONObject notfObject = new JSONObject();
                        notfObject.put("title", currentUsername);
                        notfObject.put("body", message);

                        JSONObject dataObject = new JSONObject();
                        dataObject.put("userId", currentUid);

                        object.put("notification", notfObject);
                        object.put("data", dataObject);
                        object.put("to", fcmToken);

                        callApi(object);




                    }catch (Exception e){

                    }

                }
            }
        });
    }

    private static void callApi(JSONObject object){
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";

        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAA7LBOjaE:APA91bEgCCehqjXcFAgR8eiH9YEn1wRLn-LknVBkD14QX0cmtOg7Ju_guWbxUEd22dBLR5UfdyF88Gr61ZGp2o-s9nKuIDrI65tV9JloQfEuct2HxLpBEfwkliqjL0gfLvdxifN9jo0l")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("notification sent", object.toString());
            }
        });



    }


    public abstract class SendMessageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = Utility.ip+"/SPOT-IT/insertMsg.php";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Create JSON object with message details
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("senderUid", params[0]);
                jsonInput.put("receiverUid", params[1]);
                jsonInput.put("message", params[2]);
                String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                jsonInput.put("time", formattedTime);
                jsonInput.put("senderProfileUrl", params[3]);
                jsonInput.put("messageType", params[4]);

                // Write the JSON object to the request body
                OutputStream os = connection.getOutputStream();
                os.write(jsonInput.toString().getBytes());
                os.flush();
                os.close();


                // Read response from the server
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();



            } catch (Exception e) {
                Log.e("SendMessageTask", "Error sending message: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                parseJson(result);
            } else {
                Log.e("SendMessageTask", "Error: result is null");
            }
        }

        private String readResponse(java.io.InputStream is) throws java.io.IOException {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        private void parseJson(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("status");

                if (status == 1) {
                    // Message sent successfully
                    Toast.makeText(ChatDetailsActivity.this,"Message sent!", Toast.LENGTH_SHORT).show();
                    onMessageSent(jsonResponse.getString("msg"));
                } else {
                    Log.e("SendMessageTask", "Server returned status: " + status);
                }

            } catch (Exception e) {
                Log.e("SendMessageTask", "Error parsing JSON: " + e.getMessage());
            }
        }

        // Abstract method to be implemented by the subclasses
        protected abstract void onMessageSent(String message);
    }



    public abstract class GetMessageTask extends AsyncTask<String, Void, String> {

        private List<MessageModel> messageList;

        public GetMessageTask(List<MessageModel> messageList) {
            this.messageList = messageList;
        }

        @Override
        protected String doInBackground(String... params) {
            String senderUid = params[0];
            String receiverUid = params[1];
            String apiUrl = Utility.ip+"/SPOT-IT/getSpMsgs.php";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Create JSON object with sender and receiver IDs
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("senderUid", senderUid);
                jsonInput.put("receiverUid", receiverUid);

                connection.getOutputStream().write(jsonInput.toString().getBytes());

                // Read response from the server
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();

            } catch (Exception e) {
                Log.e("GetMessageTask", "Error retrieving messages: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {

                List<MessageModel> msgs_to_display = parseJson(result);



                if(msgs_to_display!=null){
                    messageAdapter.setMessageList(msgs_to_display);
                    messageAdapter.notifyDataSetChanged();
                }



            } else {
                Log.e("GetMessageTask", "Error: result is null");
            }
        }

        private List<MessageModel> parseJson(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("status");

                if (status == 1) {
                    JSONArray messagesArray = jsonResponse.getJSONArray("messages");

                    for (int i = 0; i < messagesArray.length(); i++) {
                        JSONObject messageObject = messagesArray.getJSONObject(i);

                        MessageModel messageModel = new MessageModel(
                                messageObject.getString("sender_uid"),
                                messageObject.getString("receiver_uid"),
                                messageObject.getString("message_text"),
                                messageObject.getString("message_type")
                        );
                        messageModel.setTime(messageObject.getString("time_sent"));
                        messageModel.setSenderProfileUrl(messageObject.getString("sender_profile_url"));

                        // Add additional fields as needed (e.g., time_sent, sender_profile_url, read_status)

                        messageList.add(messageModel);
                    }

                    // Notify any listener that messages are loaded
                    //onMessagesLoaded(messageList);
                    return messageList;

                } else {
                    Log.e("GetMessageTask", "Server returned status: " + status);
                }

            } catch (Exception e) {
                Log.e("GetMessageTask", "Error parsing JSON: " + e.getMessage());
            }
            return null;
        }

        // Abstract method to be implemented by the subclasses
        protected abstract void onMessagesLoaded(List<MessageModel> messageList);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMessageUpdates();
        getContentResolver().unregisterContentObserver(screenshotObserver);
    }



}