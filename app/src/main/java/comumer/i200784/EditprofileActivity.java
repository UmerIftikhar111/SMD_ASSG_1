package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditprofileActivity extends AppCompatActivity {

    EditText name, email, contact, country, city;
    TextView saveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        saveChanges=findViewById(R.id.save_changes);

        name.setText(User.currentUser.getName());
        email.setText(User.currentUser.getEmail());
        contact.setText(User.currentUser.getContact());
        country.setText(User.currentUser.getCountry());
        city.setText(User.currentUser.getCity());

        // Handle the "Save Changes" button click
        saveChanges.setOnClickListener(view -> {
            // Get the updated values
            String updatedName = name.getText().toString().trim();
            String updatedContact = contact.getText().toString().trim();
            String updatedCountry = country.getText().toString().trim();
            String updatedCity = city.getText().toString().trim();

            // Get the current user's UID
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String userUid = mAuth.getCurrentUser().getUid();

            // Update the user's document in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userUid)
                    .update("name", updatedName, "contact", updatedContact, "country", updatedCountry, "city", updatedCity)
                    .addOnSuccessListener(aVoid -> {
                        // Profile updated successfully
                        Toast.makeText(EditprofileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        // Redirect back to the profile screen
                        Intent intent = new Intent(EditprofileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error, e.g., display an error message
                        Toast.makeText(EditprofileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        });

        // nav text view
        ImageView navProfile = findViewById(R.id.navigate_to_profile);
        navProfile.setOnClickListener(view -> {
            Intent intent = new Intent(EditprofileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

    }
}