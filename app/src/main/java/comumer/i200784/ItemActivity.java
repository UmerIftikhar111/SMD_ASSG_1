package comumer.i200784;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ItemActivity extends AppCompatActivity {

    RelativeLayout image, video;
    EditText name, desc, rate;
    Button post;
    Spinner city;
    String userId;

    private static final int IMAGE_REQUEST = 1;
    private static final int VIDEO_REQUEST = 2;
    public Uri selectedImageUri;
    private Uri selectedVideoUri;

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        image = findViewById(R.id.image);
        video = findViewById(R.id.video);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);
        city = findViewById(R.id.city);
        rate = findViewById(R.id.rate);
        post = findViewById(R.id.post);

        // Populate the Spinner with city options
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this, R.array.pakistani_cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = User.currentUser.getUid();

        // Close screen icon
        TextView homeIcn = findViewById(R.id.nav_back);
        homeIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemActivity.this, WelcomeActivityActivity.class);
            startActivity(intent);
        });

        // Capture pic icon (Image selection)
        image.setOnClickListener(view -> showMediaSelectionDialog(IMAGE_REQUEST));

        // Capture pic icon (Video selection)
        video.setOnClickListener(view -> showMediaSelectionDialog(VIDEO_REQUEST));

        // Handle posting to Firestore
        post.setOnClickListener(view -> {
            String itemName = name.getText().toString();
            String itemDesc = desc.getText().toString();
            String itemCity = city.getSelectedItem().toString();
            String itemRate = rate.getText().toString();

            if (selectedImageUri != null || selectedVideoUri != null) {
                // Upload selected image and video to Firebase Storage
                uploadImageAndVideo(itemName, itemDesc, itemCity, itemRate);
            } else {
                // Handle the case where no image or video is selected
                Toast.makeText(ItemActivity.this, "Please select an image or video.", Toast.LENGTH_SHORT).show();
            }
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
                        Intent cameraIntent = new Intent(ItemActivity.this, CaptureActivity.class);
                        startActivityForResult(cameraIntent,requestCode);
                        Toast.makeText(this, "Camera option selected", Toast.LENGTH_SHORT).show();
                    } else if (requestCode == VIDEO_REQUEST) {
                        Intent videoIntent = new Intent(ItemActivity.this, Video.class);
                        startActivityForResult(videoIntent,requestCode);
                        Toast.makeText(this, "Video option selected", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        });
        builder.show();
    }

    private void uploadImageAndVideo(String itemName, String itemDesc, String itemCity, String itemRate) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child("images/" + UUID.randomUUID().toString());
        StorageReference videoRef = storage.getReference().child("videos/" + UUID.randomUUID().toString());

        UploadTask imageUploadTask = imageRef.putFile(selectedImageUri);
        UploadTask videoUploadTask = videoRef.putFile(selectedVideoUri);

        imageUploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                videoUploadTask.addOnSuccessListener(taskSnapshot1 -> {
                    videoRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        String videoUrl = uri1.toString();
                        Log.i("uploaded", imageUrl);
                        storeItemDetails(itemName, itemDesc, itemCity, itemRate, imageUrl, videoUrl);
                    });
                });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ItemActivity.this, "Failed to upload image. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void storeItemDetails(String itemName, String itemDesc, String itemCity, String itemRate, String imageUrl, String videoUrl) {
        try {
            // Create a JSON object
            JSONObject itemData = new JSONObject();
            itemData.put("item_name", itemName);
            itemData.put("item_desc", itemDesc);
            itemData.put("item_city", itemCity);
            itemData.put("item_rate", itemRate);
            itemData.put("poster_uid", userId);
            itemData.put("image_url", imageUrl);
            itemData.put("video_url", videoUrl);
            itemData.put("renter_uid", " ");

            // Get the current date
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
            // Format the current date
            String formattedDate = dateFormat.format(currentDate);
            // Now, you can put the formatted date into your itemData
            itemData.put("itemDate", formattedDate);

            // Execute AsyncTask to post data

            Log.i("data",itemData.toString());

            new PostDataTask().execute(itemData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ItemActivity.this, "Failed to create JSON object.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      //  Toast.makeText(this,data.getData().toString(),Toast.LENGTH_LONG).show();

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == VIDEO_REQUEST) {
                selectedVideoUri = data.getData();
                Toast.makeText(this, "Video selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class PostDataTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                // Your PHP endpoint URL
                URL url = new URL(Utility.ip+"/SPOT-IT/insertItems.php");

                // Create connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Convert parameters to JSON
                String postData = params[0];

                Log.i("post data", postData);

                // Write data to the connection
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

                String responseLine;
                StringBuilder response = new StringBuilder();

                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine);
                }

                return response.toString();



            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result!=null && result.contains("success")) {
                Toast.makeText(ItemActivity.this, "Item posted successfully.", Toast.LENGTH_SHORT).show();
            } else if(result!=null) {
                Toast.makeText(ItemActivity.this, "Failed to post the item.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ItemActivity.this, "null response.", Toast.LENGTH_LONG).show();
            }
        }

        private String getPostDataString(Map<String, Object> params) throws Exception {
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(param.getKey());
                postData.append('=');
                postData.append(param.getValue());
            }
            return postData.toString();
        }
    }

}
