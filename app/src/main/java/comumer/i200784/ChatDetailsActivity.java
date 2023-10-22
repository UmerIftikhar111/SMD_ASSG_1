package comumer.i200784;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    TextView send, contactName, status;
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
    public static String senderUid, receiverUid, receiverProfileUrl;

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

        contactName.setText(getIntent().getStringExtra("receiverUsername"));


        messageAdapter = new MessageAdapter(this, messageList);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        receiverUid = getIntent().getStringExtra("receiverUid");
        receiverProfileUrl = getIntent().getStringExtra("receiverProfileUrl");
        senderUid = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("all-users/" + receiverUid + "/status");
        userStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the user's status (online or offline)
                Boolean isOnline = dataSnapshot.getValue(Boolean.class);
                if (isOnline != null && isOnline) { status.setText("Online");}
                else { status.setText("Offline");}
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });


        DatabaseReference messagesRef = databaseReference.child("chats");
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the current message list to avoid duplications
                messageList.clear();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Parse the message data and add it to your message list
                    MessageModel message = messageSnapshot.getValue(MessageModel.class);
                    // Check if the message meets either of the conditions
                    if ((message.getSenderUid().equals(senderUid) && message.getReceiverUid().equals(receiverUid)) ||
                            (message.getSenderUid().equals(receiverUid) && message.getReceiverUid().equals(senderUid))) {

                        if(senderUid.equals(message.getReceiverUid())){
                            message.setReadStatus("true");
                            // Update the message in the database with the new readStatus
                            String messageId = messageSnapshot.getKey();
                            messagesRef.child(messageId).setValue(message);
                        }

                        messageList.add(message);
                        }
                }

                // Notify the adapter that the data has changed
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors or failure to fetch messages
                Toast.makeText(ChatDetailsActivity.this, "Error fetching messages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        send.setOnClickListener(v -> {
            // Get the message text from the EditText
            String message = messageText.getText().toString().trim();

            if (!message.isEmpty()) {
                // Create a message object
                MessageModel messageModel = new MessageModel(senderUid, receiverUid, message,"text");
                messageModel.setSenderProfileUrl(receiverProfileUrl);
                // Generate a unique key for the message
                String messageKey = databaseReference.child("chats").push().getKey();
                // Store the message in the Realtime Database under the 'chats' node
                databaseReference.child("chats").child(messageKey).setValue(messageModel);
                // Clear the message input field
                messageText.setText("");
                sendNotification(message,receiverUid, senderUid, User.currentUser.getName());
                Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(this, "Please enter a message to send.", Toast.LENGTH_SHORT).show();
        });

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            uploadImageToStorage(selectedImageUri);
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
        // Generate a unique key for the message
        String messageKey = databaseReference.child("chats").push().getKey();
        // Store the message in the Realtime Database under the 'chats' node
        databaseReference.child("chats").child(messageKey).setValue(messageModel);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(screenshotObserver);
    }

}