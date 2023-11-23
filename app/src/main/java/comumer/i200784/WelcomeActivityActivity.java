package comumer.i200784;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivityActivity extends AppCompatActivity{

    private RecyclerView recyclerView, recyclerViewAdsPersonal;
    private AdAdapter adAdapter, adAdapterForPersonalAds;
    TextView current_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        current_username = findViewById(R.id.current_username);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewAds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adAdapter = new AdAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adAdapter);

        recyclerViewAdsPersonal =findViewById(R.id.recyclerViewAdsPersonal);
        recyclerViewAdsPersonal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adAdapterForPersonalAds = new AdAdapter(new ArrayList<>(), this);
        recyclerViewAdsPersonal.setAdapter(adAdapterForPersonalAds);

        fetchAllItemsFromFirestore();

        fetchMyItemsFromFirestore();

        // Set the username
        current_username.setText(User.currentUser.getName());

        // Setup icons and click actions
        setupIcons();
    }
    private void setupIcons() {
        // home icon
        ImageView homeIcn = findViewById(R.id.homeIcon);
        homeIcn.setOnClickListener(view -> {
            // Handle click action for home icon
        });

        // search icon
        ImageView searchIcn = findViewById(R.id.searchItems);
        searchIcn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivityActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // chat icon
        ImageView chatIcn = findViewById(R.id.chat);
        chatIcn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivityActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // profile icon
        ImageView profileIcn = findViewById(R.id.profile);
        profileIcn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivityActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // plus icon
        ImageView plusIcn = findViewById(R.id.addItem);
        plusIcn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivityActivity.this, ItemActivity.class);
            startActivity(intent);
        });


    }


    private void fetchAllItemsFromFirestore() {
        // Call the AsyncTask to fetch items
        new FetchItemsTask().execute(Utility.ip+"/SPOT-IT/getItems.php");

    }

    private void fetchMyItemsFromFirestore() {
        new FetchMyItemsTask().execute(Utility.ip+"/SPOT-IT/getItemsbyId.php");
    }


    public class FetchItemsTask extends AsyncTask<String, Void, List<Advertisement>> {

        private static final String TAG = "FetchItemsTask";


        @Override
        protected List<Advertisement> doInBackground(String... params) {
            List<Advertisement> itemList = new ArrayList<>();

            try {
                URL url = new URL(params[0]); // Your getItems.php endpoint URL

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
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
                        JSONArray itemsArray = jsonResponse.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);

                            Advertisement ad = new Advertisement();
                            ad.setItemUid(itemObject.getString("item_id"));
                            ad.setPictureUrl(itemObject.getString("image_url"));
                            ad.setLocation(itemObject.getString("item_city"));
                            ad.setDate(itemObject.getString("item_date"));
                            ad.setDescription(itemObject.getString("item_desc"));
                            ad.setName(itemObject.getString("item_name"));
                            ad.setRate(Double.parseDouble(itemObject.getString("item_rate")));
                            ad.setPosterUid(itemObject.getString("poster_uid"));
                            ad.setRenterUid(itemObject.getString("renter_uid"));


                            itemList.add(ad);
                        }
                    }
                } finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching items: " + e.getMessage());
            }

            return itemList;
        }

        @Override
        protected void onPostExecute(List<Advertisement> result) {
            if (result != null) {
                adAdapter.setItemList(result);
                adAdapter.notifyDataSetChanged();
            }
        }


    }


    public class FetchMyItemsTask extends AsyncTask<String, Void, List<Advertisement>> {

        private static final String TAG = "FetchMyItemsTask";


        @Override
        protected List<Advertisement> doInBackground(String... params) {
            List<Advertisement> itemList = new ArrayList<>();

            try {
                URL url = new URL(params[0]); // Your getItems.php endpoint URL

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    // Create the JSON object for the user ID
                    JSONObject jsonUserId = new JSONObject();
                    jsonUserId.put("poster_uid", User.currentUser.getUid());

                    // Write the JSON object to the request body
                    OutputStream outputStream = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(jsonUserId.toString());
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
                        JSONArray itemsArray = jsonResponse.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemObject = itemsArray.getJSONObject(i);

                            Advertisement ad = new Advertisement();
                            ad.setItemUid(itemObject.getString("item_id"));
                            ad.setPictureUrl(itemObject.getString("image_url"));
                            ad.setLocation(itemObject.getString("item_city"));
                            ad.setDate(itemObject.getString("item_date"));
                            ad.setDescription(itemObject.getString("item_desc"));
                            ad.setName(itemObject.getString("item_name"));
                            ad.setRate(Double.parseDouble(itemObject.getString("item_rate")));
                            ad.setPosterUid(itemObject.getString("poster_uid"));
                            ad.setRenterUid(itemObject.getString("renter_uid"));


                            itemList.add(ad);
                        }
                    }
                } finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching items: " + e.getMessage());
            }

            return itemList;
        }

        @Override
        protected void onPostExecute(List<Advertisement> result) {
            if (result != null) {
                adAdapterForPersonalAds.setItemList(result);
                adAdapterForPersonalAds.notifyDataSetChanged();
            }
        }


    }

}
