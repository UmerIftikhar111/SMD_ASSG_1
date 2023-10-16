package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "items" collection
        CollectionReference itemsRef = db.collection("items");

        itemsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Advertisement> itemList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                Advertisement ad = new Advertisement();
                ad.setName(document.get("itemName").toString());
                ad.setDate(document.get("itemDate").toString());
                ad.setLocation(document.get("itemCity").toString());
                ad.setRate(Double.parseDouble(document.get("itemRate").toString()));
                ad.setDescription(document.get("itemDesc").toString());
                ad.setPictureUrl(document.get("imageUrl").toString());
                ad.setPosterUid(document.get("posterUid").toString());
                ad.setRenterUid(document.get("renterUid").toString());

                ad.setItemUid(document.getId());

                itemList.add(ad);
            }

            Toast.makeText(this, "Fetched item docs", Toast.LENGTH_SHORT).show();
            // Update the adapter with the fetched itemList
            adAdapter.setItemList(itemList);
            adAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            String errorMessage = "Failed to fetch data from Firestore. Please try again later.";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchMyItemsFromFirestore() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "items" collection
        CollectionReference itemsRef = db.collection("items");

        itemsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Advertisement> itemList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                if(document.get("posterUid").toString().equals(User.currentUser.getUid()) ||
                        document.get("renterUid").toString().equals(User.currentUser.getUid()) ){
                    Advertisement ad = new Advertisement();
                    ad.setName(document.get("itemName").toString());
                    ad.setDate(document.get("itemDate").toString());
                    ad.setLocation(document.get("itemCity").toString());
                    ad.setRate(Double.parseDouble(document.get("itemRate").toString()));
                    ad.setDescription(document.get("itemDesc").toString());
                    ad.setPictureUrl(document.get("imageUrl").toString());
                    ad.setPosterUid(document.get("posterUid").toString());
                    ad.setRenterUid(document.get("renterUid").toString());

                    ad.setItemUid(document.getId());

                    itemList.add(ad);
                }


            }

            Toast.makeText(this, "Fetched item docs", Toast.LENGTH_SHORT).show();
            // Update the adapter with the fetched itemList
            adAdapterForPersonalAds.setItemList(itemList);
            adAdapterForPersonalAds.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            String errorMessage = "Failed to fetch data from Firestore. Please try again later.";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        });
    }


}
