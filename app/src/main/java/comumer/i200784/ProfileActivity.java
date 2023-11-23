package comumer.i200784;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    TextView logout, username, city, items_posted, items_rented;
    ImageView cover_pic, profile_pic;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        logout = findViewById(R.id.logout);
        username = findViewById(R.id.username);
        city = findViewById(R.id.city);
        items_posted = findViewById(R.id.items_posted);
        items_rented = findViewById(R.id.items_rented);

        cover_pic=findViewById(R.id.cover_pic);
        profile_pic=findViewById(R.id.profile_pic);

        username.setText(User.currentUser.getName());
        city.setText(User.currentUser.getCity());
        items_posted.setText(User.currentUser.getItemsPosted()+" items posted");
        items_rented.setText(User.currentUser.getItemsRented()+" items rented");



        if (User.currentUser.getMainProfileUrl() != null) {
            String profileUrl = User.currentUser.getMainProfileUrl();
            if(profileUrl!=""){
                Picasso.get().load(profileUrl).into(profile_pic);
            }
        }



        if (!User.currentUser.getCoverProfileUrl().isEmpty()) {
            String coverUrl = User.currentUser.getCoverProfileUrl();
            Picasso.get().load(coverUrl).into(cover_pic);
        }


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        profile_pic.setOnClickListener(view -> pickImageFromGallery(1));
        cover_pic.setOnClickListener(view -> pickImageFromGallery(2));

        // forgot pwd text view
        ImageView editProf = findViewById(R.id.editProfile);
        editProf.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, EditprofileActivity.class);
            startActivity(intent);
        });

        // Set up Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Handle user logout
        logout.setOnClickListener(view -> {
            mAuth.signOut(); // Sign out the user
            User.currentUser=null;
            // Redirect to the login screen
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }

    // Trigger image selection from the gallery
    private void pickImageFromGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1 || requestCode == 2) {
                Uri selectedImageUri = data.getData();
                // Upload the selected image to Firebase Storage
                uploadImageToStorage(selectedImageUri, requestCode);
            }
        }
    }

    // ...
    private void uploadImageToStorage(Uri imageUri, int requestCode) {
        String uid = User.currentUser.getUid();

        StorageReference imageRef = storageRef.child("images/" + uid + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            // Get the download URL for the uploaded image
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {

                if(requestCode==1){
                    User.currentUser.setMainProfileUrl(downloadUri.toString());
                    Picasso.get().load(downloadUri.toString()).into(profile_pic);
                }else if(requestCode==2){
                    User.currentUser.setCoverProfileUrl(downloadUri.toString());
                    Picasso.get().load(downloadUri.toString()).into(cover_pic);
                }


                // Update the user's Firestore document with the download URL
                updateProfileImageInFirestore(downloadUri.toString(), requestCode);
            }).addOnFailureListener(e -> {
                // Handle the error when retrieving download URL
                Toast.makeText(ProfileActivity.this, "Failed to get download URL for the image", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Handle the error when uploading the image
            Toast.makeText(ProfileActivity.this, "Failed to upload the image", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileImageInFirestore(String downloadUrl, int requestCode) {
        String fieldToUpdate;

        if(requestCode == 1){
            new UpdateProfileImageTask().execute(downloadUrl);
        }else if(requestCode == 2){
            new UpdateCoverImageTask().execute(downloadUrl);
        } else {
            fieldToUpdate = "img";
        }
    }


    private class UpdateProfileImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = Utility.ip + "/SPOT-IT/updateProfilePic.php";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Create JSON object
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userUid", User.currentUser.getUid());
                jsonParam.put("profilePicUrl", params[0]); // The image URL

                // Write JSON data to the request body
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();
                outputStream.close();

                // Read the response from the server
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String responseLine;
                StringBuilder response = new StringBuilder();



                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine);
                }

                String res = response.toString();
                if(res.contains("success")){

                }

                // Return the response as a string
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {

                if (response.contains("success")) {
                    Toast.makeText(ProfileActivity.this, "Profile pic update success ", Toast.LENGTH_SHORT).show();

                } else {
                    // Registration failed, show an error message
                    Toast.makeText(ProfileActivity.this, "Profile pic update failed: "+response, Toast.LENGTH_SHORT).show();
                }



            } else {
                Toast.makeText(ProfileActivity.this, "Profile pic update failed: Invalid response ", Toast.LENGTH_SHORT).show();

            }


        }
    }

    private class UpdateCoverImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = Utility.ip + "/SPOT-IT/updateCoverPic.php";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Create JSON object
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userUid", User.currentUser.getUid());
                jsonParam.put("coverProfileUrl", params[0]); // The image URL

                // Write JSON data to the request body
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();
                outputStream.close();

                // Read the response from the server
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String responseLine;
                StringBuilder response = new StringBuilder();

                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine);
                }

                // Return the response as a string
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {

                if (response.contains("success")) {
                    Toast.makeText(ProfileActivity.this, "Profile pic update success ", Toast.LENGTH_SHORT).show();

                } else {
                    // Registration failed, show an error message
                    Toast.makeText(ProfileActivity.this, "Profile pic update failed: "+response, Toast.LENGTH_SHORT).show();
                }



            } else {
                Toast.makeText(ProfileActivity.this, "Profile pic update failed: Invalid response ", Toast.LENGTH_SHORT).show();

            }


        }
    }


}