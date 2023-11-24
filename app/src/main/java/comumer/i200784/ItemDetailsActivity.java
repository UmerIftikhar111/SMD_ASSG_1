package comumer.i200784;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageView itemImage, posterImage, chatWithPoster;
    TextView itemRate, itemName, itemDescription, itemLocation, itemDate, posterName, posterItemsAdded;

    Button rentIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        Advertisement advertisement = getIntent().getParcelableExtra("itemDetails");

        itemImage = findViewById(R.id.itemImage);
        posterImage = findViewById(R.id.posterImage);
        posterName = findViewById(R.id.posterName);
        posterItemsAdded = findViewById(R.id.posterItemsAdded);
        itemRate = findViewById(R.id.itemRate);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemLocation = findViewById(R.id.itemLocation);
        itemDate = findViewById(R.id.itemDate);
        chatWithPoster = findViewById(R.id.chatWithPoster);
        rentIt=findViewById(R.id.rentBtn);

        // Set values from the Advertisement object
        itemRate.setText(advertisement.getRate()+"$");
        itemName.setText(advertisement.getName());
        itemDescription.setText(advertisement.getDescription());
        itemLocation.setText(advertisement.getLocation());
        itemDate.setText(advertisement.getDate());
        Picasso.get().load(advertisement.getPictureUrl()).into(itemImage);

        String posterUid = advertisement.getPosterUid();

        new GetUserDetailsTask().execute(posterUid);


        chatWithPoster.setOnClickListener(v -> {

            Intent intent = new Intent(ItemDetailsActivity.this, ChatDetailsActivity.class);
            intent.putExtra("receiverUid", posterUid);
            intent.putExtra("receiverUsername", posterName.getText());
            intent.putExtra("receiverProfileUrl", User.currentUser.getMainProfileUrl());
            startActivity(intent);

        });

        // back arrow icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_to_items);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemDetailsActivity.this, WelcomeActivityActivity.class);
            startActivity(intent);
        });

        // report
        TextView reportItem = findViewById(R.id.report_item);
        reportItem.setOnClickListener(view -> {
            Intent intent = new Intent(ItemDetailsActivity.this, ReportActivity.class);
            // Add the item UID as an extra to the intent
            intent.putExtra("itemUid", advertisement.getItemUid());
            startActivity(intent);
        });

        rentIt.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter the hours you want to rent..");

            // Create an EditText field in the dialog to edit the message
            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            //editText.setText(message.getMessage());
            builder.setView(editText);

            builder.setPositiveButton("Calculate", (dialog, which) -> {
                // Get the edited text from the EditText
                String editedText = editText.getText().toString();

                Toast.makeText(ItemDetailsActivity.this, "Total Rent: $" + ( Integer.parseInt(editedText)*advertisement.getRate()), Toast.LENGTH_SHORT).show();

            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.show();

        });

    }

    public class GetUserDetailsTask extends AsyncTask<String, Void, User> {

        private static final String TAG = "GetUserDetailsTask";

        @Override
        protected User doInBackground(String... params) {
            User user = null;
            String posterId = params[0];

            try {
                // Your PHP endpoint URL
                URL url = new URL(Utility.ip+"/SPOT-IT/getUserbyId.php");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("userId", posterId);


                    // Write JSON data to the request body
                    OutputStream outputStream = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
                    outputStream.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    int status = jsonResponse.getInt("status");

                    if (status == 1) {
                        JSONObject userObject = jsonResponse.getJSONObject("userData");

                        user = new User();
                        user.setUid(userObject.getString("userId"));
                        user.setName(userObject.getString("userName"));
                        user.setEmail(userObject.getString("userEmail"));
                        user.setContact(userObject.getString("userContact"));
                        user.setCountry(userObject.getString("selectedCountry"));
                        user.setCity(userObject.getString("selectedCity"));
                        user.setFCMToken(userObject.getString("FCMToken"));
                        user.setItemsPosted(userObject.getInt("itemsPosted"));
                        user.setItemsRented(userObject.getInt("itemsRented"));
                        user.setCoverProfileUrl(userObject.getString("coverProfileUrl"));
                        user.setMainProfileUrl(userObject.getString("mainProfileUrl"));

                        // Update UI on the main thread
                        User finalUser = user;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                posterName.setText(finalUser.getName());
                                posterItemsAdded.setText(finalUser.getItemsRented() + " items rented");
                                Toast.makeText(ItemDetailsActivity.this, "Details: " + finalUser.getName(), Toast.LENGTH_LONG).show();
                                if(!finalUser.getMainProfileUrl().isEmpty() && finalUser.getMainProfileUrl()!=null){
                                    Picasso.get().load(finalUser.getMainProfileUrl()).into(posterImage);
                                }

                            }
                        });


                    }
                } finally {
                    urlConnection.disconnect();
                }

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching user details: " + e.getMessage());
            }


            return user;
        }


    }
}