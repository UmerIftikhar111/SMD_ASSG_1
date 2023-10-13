package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, contact, password;
    Spinner country, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //set ids
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        country = findViewById(R.id.country);
        password = findViewById(R.id.password);
        city = findViewById(R.id.city);

        // Populate Country Spinner
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(countryAdapter);

        // Populate City Spinner
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this, R.array.pakistani_cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);



        // text view
        TextView loginTextView = findViewById(R.id.navigate_to_login);
        loginTextView.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });


        // Inside the onCreate method
        Button registerButton = findViewById(R.id.register_btn);
        registerButton.setOnClickListener(view -> {
            // Get user input
            String userName = name.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String userContact = contact.getText().toString().trim();
            String selectedCountry = country.getSelectedItem().toString();
            String selectedCity = city.getSelectedItem().toString();


            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {

                                // User registration successful
                                // Create a Firestore reference for the user
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db.collection("users").document(user.getUid());

                                // Create a user object with additional data
                                User newUser = new User(userName, userEmail, userContact, selectedCountry, selectedCity);
                                newUser.setItemsPosted(0);
                                newUser.setItemsRented(0);
                                newUser.setCoverProfileUrl("");
                                newUser.setMainProfileUrl("");

                                // Set the user object in Firestore
                                userRef.set(newUser)
                                        .addOnSuccessListener(aVoid -> {
                                            // Data has been successfully saved in Firestore
                                            Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Error handling
                                            Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            // User registration failed
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        });


    }
}