package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    TextView logout, username, city, items_posted, items_rented;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        logout = findViewById(R.id.logout);
        username = findViewById(R.id.username);
        city = findViewById(R.id.city);
        items_posted = findViewById(R.id.items_posted);
        items_rented = findViewById(R.id.items_rented);

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
}