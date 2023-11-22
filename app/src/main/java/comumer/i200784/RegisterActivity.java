package comumer.i200784;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

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
import java.net.URL;
import java.net.URLEncoder;

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

            // Execute AsyncTask
            RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask();
            registerAsyncTask.execute(userName, userEmail, userPassword, userContact, selectedCountry, selectedCity);

        });


    }

    private class RegisterAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userName = params[0];
            String userEmail = params[1];
            String userPassword = params[2];
            String userContact = params[3];
            String selectedCountry = params[4];
            String selectedCity = params[5];

            try {
                URL url = new URL(Utility.ip + "/SPOT-IT/register.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Create JSON object
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("userName", userName);
                jsonParams.put("userEmail", userEmail);
                jsonParams.put("userPassword", userPassword);
                jsonParams.put("userContact", userContact);
                jsonParams.put("selectedCountry", selectedCountry);
                jsonParams.put("selectedCity", selectedCity);



                writer.write(jsonParams.toString());
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

            } catch (IOException e) {
                e.printStackTrace();
                return "Error";
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.contains("success")) {
                // Registration successful, navigate to the main activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                // Registration failed, show an error message
                Toast.makeText(RegisterActivity.this, "Registration failed: "+result, Toast.LENGTH_SHORT).show();
            }
        }
    }



}