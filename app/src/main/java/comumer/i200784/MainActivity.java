package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        // nav text view
        TextView registerTextView = findViewById(R.id.navigate_to_register);
        registerTextView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // forgot pwd text view
        TextView forgotPwdTextView = findViewById(R.id.forgotPassword);
        forgotPwdTextView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


        // Login button
        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(view -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            // Sign in with Firebase Authentication
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {

                                // Replace "your_user_uid" with the actual UID
                                String userUid = user.getUid();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                CollectionReference usersCollection = db.collection("users");
                                DocumentReference userDocument = usersCollection.document(userUid);

                                userDocument.get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document.exists()) {
                                            // The document exists, and you can access its data
                                            String name = document.getString("name");
                                            String email = document.getString("email");
                                            String country = document.getString("country");
                                            String contact = document.getString("contact");
                                            String city = document.getString("city");
                                            // Retrieve other user data as needed

                                            User.currentUser = new User(name,email, contact, country, city);

                                            // Display a toast message with user data
                                            String toastMessage = "Name: " + name + "\nEmail: " + email;
                                            Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                                        } else {
                                            // The document does not exist
                                            Toast.makeText(MainActivity.this, "User document not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // An error occurred while fetching the document
                                        String errorMessage = "Error fetching user document: " + task.getException().getMessage();
                                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });



                                // Redirect to the welcome activity or your desired destination
                                Intent intent = new Intent(MainActivity.this, WelcomeActivityActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            // Login failed
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

}



