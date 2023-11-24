package comumer.i200784;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    Button reportBtn;
    EditText reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        // Retrieve the item UID from the intent
        String itemUid = getIntent().getStringExtra("itemUid");


        // Initialize UI components
        reportBtn = findViewById(R.id.reportBtn);
        reason = findViewById(R.id.reason);

        // Handle the report button click
        reportBtn.setOnClickListener(view -> {

            ReportActivity.ReviewAsyncTask registerAsyncTask = new ReportActivity.ReviewAsyncTask();
            registerAsyncTask.execute(User.currentUser.getUid(),reason.getText().toString());



        });
    }
    private class ReviewAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            String comment = params[1];


            try {
                URL url = new URL(Utility.ip + "/SPOT-IT/createReview.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Create JSON object
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("user_id", userId);
                jsonParams.put("comment", comment);



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
                Intent intent = new Intent(ReportActivity.this, WelcomeActivityActivity.class);
                startActivity(intent);
            } else {
                // Registration failed, show an error message
                Toast.makeText(ReportActivity.this, "Registration failed: "+result, Toast.LENGTH_SHORT).show();
            }
        }
    }



}