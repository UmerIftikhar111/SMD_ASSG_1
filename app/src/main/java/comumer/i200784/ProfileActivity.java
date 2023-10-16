package comumer.i200784;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
            if(profileUrl!="")
            Picasso.get().load(profileUrl).into(profile_pic);
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
        String uid = mAuth.getCurrentUser().getUid();

        StorageReference imageRef = storageRef.child("images/" + uid + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            // Get the download URL for the uploaded image
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
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
            fieldToUpdate="mainProfileUrl";
        }else if(requestCode == 2){
            fieldToUpdate="coverProfileUrl";
        } else {
            fieldToUpdate = "img";
        }

        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        userRef.update(fieldToUpdate, downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated Firestore document
                    // Set the image in the ImageView
                    if ( fieldToUpdate=="profileImageUrl") {
                        Picasso.get().load(downloadUrl).into(profile_pic);
                    } else if(fieldToUpdate=="coverImageUrl") {
                        Picasso.get().load(downloadUrl).into(cover_pic);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error when updating Firestore document
                    Toast.makeText(ProfileActivity.this, "Failed to update Firestore document", Toast.LENGTH_SHORT).show();
                });
    }

}