package comumer.i200784;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        //loadSavedCredentials();

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
        loginBtn = findViewById(R.id.login_btn);

//        loginBtn.setOnClickListener(view -> {
//            String userEmail = email.getText().toString().trim();
//            String userPassword = password.getText().toString().trim();
//
//            // Save the email and password in shared preferences
//            saveUserCredentials(userEmail, userPassword);
//
//            // Sign in with Firebase Authentication
//            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
//                    .addOnCompleteListener(this, task -> {
//                        if (task.isSuccessful()) {
//                            // Login successful
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            if (user != null) {
//
//                                String userUid = user.getUid();
//
//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                CollectionReference usersCollection = db.collection("users");
//                                DocumentReference userDocument = usersCollection.document(userUid);
//
//                                userDocument.get().addOnCompleteListener(task1 -> {
//                                    if (task1.isSuccessful()) {
//                                        DocumentSnapshot document = task1.getResult();
//                                        if (document.exists()) {
//
//                                            String name = document.getString("name");
//                                            String email = document.getString("email");
//                                            String country = document.getString("country");
//                                            String contact = document.getString("contact");
//                                            String city = document.getString("city");
//                                            String coverUrl = document.getString("coverProfileUrl");
//                                            String profileUrl = document.getString("mainProfileUrl");
//
//                                            // Retrieve other user data as needed
//                                            //  User.currentUser=null;
//                                            User curr = new User(name,email, contact, country, city);
//                                            curr.setUid(userUid);
//                                            User.currentUser=curr;
//                                            User.currentUser.setMainProfileUrl(profileUrl);
//                                            User.currentUser.setCoverProfileUrl(coverUrl);
//
//                                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task3 -> {
//                                                if(task3.isSuccessful()){
//                                                    String token = task3.getResult();
//                                                    User.currentUser.setFCMToken(token);
//                                                    userDocument.update("fcmtoken",token);
//                                                }
//                                            });
//
//                                            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//                                            DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("all-users/"+userUid);
//
//                                            connectedRef.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot snapshot) {
//                                                    boolean connected = snapshot.getValue(Boolean.class);
//                                                    if (connected) {
//                                                        // User is connected
//                                                        userStatusRef.child("online").setValue(true);
//                                                        userStatusRef.child("lastOnline").onDisconnect().setValue(ServerValue.TIMESTAMP);
//                                                    } else {
//                                                        // User is disconnected
//                                                        userStatusRef.child("online").setValue(false);
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError error) {
//                                                    // Handle error
//                                                }
//                                            });
//
//
//                                            // Display a toast message with user data
//                                            String toastMessage = "Name: " + name + "\nEmail: " + email;
//                                            Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
//                                            // Redirect to the welcome activity or your desired destination
//                                            Intent intent = new Intent(MainActivity.this, WelcomeActivityActivity.class);
//                                            startActivity(intent);
//                                        } else {
//                                            // The document does not exist
//                                            Toast.makeText(MainActivity.this, "User document not found", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else {
//                                        // An error occurred while fetching the document
//                                        String errorMessage = "Error fetching user document: " + task.getException().getMessage();
//                                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//
//
//
//                            }
//                        } else {
//                            // Login failed
//                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });

        loginBtn.setOnClickListener(view -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            // Save the email and password in shared preferences
            // saveUserCredentials(userEmail, userPassword);

            String loginApiUrl = Utility.ip + "/SPOT-IT/login.php";
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("userEmail", userEmail);
                requestBody.put("userPassword", userPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Execute the login task asynchronously
            new LoginTask().execute(loginApiUrl, requestBody.toString());
        });




    }

    // Implementation of makeSyncHttpRequest
    private String makeSyncHttpRequest(String apiUrl, String requestBody) {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result = null;

        try {
            // Create URL object
            URL url = new URL(apiUrl);

            // Open connection
            urlConnection = (HttpURLConnection) url.openConnection();

            // Set HTTP method
            urlConnection.setRequestMethod("POST");

            // Enable input/output streams
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            // Set request headers
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Get output stream and write request body
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(requestBody.getBytes("UTF-8"));
            outputStream.close();

            // Get response code
            int responseCode = urlConnection.getResponseCode();
            //Toast.makeText(this, "Res code.: "+ responseCode , Toast.LENGTH_SHORT).show();

            Log.i("response code", String.valueOf(responseCode));

            // If the request was successful (status 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
              //  Toast.makeText(this, "Res code0: "+ responseCode , Toast.LENGTH_SHORT).show();
                // Read response
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                result = stringBuilder.toString();
            } else {
               // Toast.makeText(this, "Res code: "+ responseCode , Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close connections
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }




    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String loginApiUrl = params[0];
            String requestBody = params[1];


            // Make synchronous HTTP request
            return makeSyncHttpRequest(loginApiUrl, requestBody);
        }

        @Override
        protected void onPostExecute(String result) {

            // Process the result on the main thread
            handleLoginResponse(result);
        }
    }

    private void handleLoginResponse(String response) {
        try {

            if (response == null) {
                Toast.makeText(this, "Response is null or empty: "+response, Toast.LENGTH_SHORT).show();
                return;

            }

            Toast.makeText(this, "here: ", Toast.LENGTH_SHORT).show();

            JSONObject jsonResponse = new JSONObject(response);
            boolean success = jsonResponse.getBoolean("success");
          //  Toast.makeText(this, "res: "+jsonResponse, Toast.LENGTH_SHORT).show();


            if (success) {
                // Login successful
                JSONObject userData = jsonResponse.getJSONObject("userData");

               // Toast.makeText(this, "Body: "+ userData, Toast.LENGTH_SHORT).show();
               // Log.i("data: ", String.valueOf(userData));

                String userId = userData.getString("userId");
                String name = userData.getString("userName");
                String userEmail = userData.getString("userEmail");
                String userContact = userData.getString("userContact");
                String selectedCountry = userData.getString("selectedCountry");
                String selectedCity = userData.getString("selectedCity");
                int itemsRented = Integer.parseInt(userData.getString("itemsRented"));
                int itemsPosted = Integer.parseInt(userData.getString("itemsPosted"));
                String mainProfileUrl = userData.getString("mainProfileUrl");
                String coverProfileUrl = userData.getString("coverProfileUrl");

                // Create a User object
                User currentUser = new User(name, userEmail, userContact, selectedCountry, selectedCity, itemsRented, itemsPosted, mainProfileUrl, coverProfileUrl);
                currentUser.setUid(userId);

                // Add additional fields to the User object as needed

                // Set the current user in your application
                User.currentUser = currentUser;

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task3 -> {
                    if (task3.isSuccessful()) {
                        String token = task3.getResult();
                        User.currentUser.setFCMToken(token);
                    }
                });

                // Display a toast message with user data
                String toastMessage = "Welcome " + name + "!";
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();

                // Redirect to the welcome activity or your desired destination
                Intent intent = new Intent(MainActivity.this, WelcomeActivityActivity.class);
                startActivity(intent);
            } else {
                // Login failed
                String message = jsonResponse.getString("message");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



//    private void saveUserCredentials(String email, String password) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("email", email);
//        editor.putString("password", password);
//        editor.apply();
//    }

//    private void loadSavedCredentials() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String savedEmail = preferences.getString("email", "");
//        String savedPassword = preferences.getString("password", "");
//
//        email.setText(savedEmail);
//        password.setText(savedPassword);
////        if(!savedPassword.isEmpty() && !savedPassword.isEmpty())
////        loginBtn.performClick();
//    }


}



