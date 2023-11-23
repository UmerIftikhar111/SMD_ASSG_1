package comumer.i200784;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
            String userUid = User.currentUser.getUid();

            // Create JSON object for the request body
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("userUid", userUid);
                requestBody.put("updatedName", updatedName);
                requestBody.put("updatedContact", updatedContact);
                requestBody.put("updatedCountry", updatedCountry);
                requestBody.put("updatedCity", updatedCity);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Execute AsyncTask to update the profile
            new UpdateProfileTask().execute(requestBody.toString());

        });

        // nav text view
        ImageView navProfile = findViewById(R.id.navigate_to_profile);
        navProfile.setOnClickListener(view -> {
            Intent intent = new Intent(EditprofileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

    }


    // AsyncTask to perform the network operation in the background
    private class UpdateProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Specify the URL of your PHP endpoint
            String updateProfileUrl = Utility.ip + "/SPOT-IT/updateProfile.php";

            try {
                URL url = new URL(updateProfileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Write JSON data to the request body
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(params[0]);
                writer.flush();
                writer.close();
                outputStream.close();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String responseLine;
                StringBuilder response = new StringBuilder();

                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine);
                }

                return response.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            handleUpdateResponse(response);
        }

        private void handleUpdateResponse(String response) {
            if (response != null) {

                if (response.contains("success")) {
                    Toast.makeText(EditprofileActivity.this, "Profile update success ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditprofileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    // Registration failed, show an error message
                    Toast.makeText(EditprofileActivity.this, "Profile update failed: "+response, Toast.LENGTH_SHORT).show();
                }



            } else {
                Toast.makeText(EditprofileActivity.this, "Profile update failed: Invalid response ", Toast.LENGTH_SHORT).show();

            }
        }
    }

}