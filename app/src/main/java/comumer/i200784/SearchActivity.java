package comumer.i200784;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import comumer.i200784.ChatActivity;
import comumer.i200784.ItemActivity;
import comumer.i200784.ProfileActivity;
import comumer.i200784.R;
import comumer.i200784.SearchResultsActivity;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    FirebaseFirestore db; // Initialize Firebase Firestore

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchView = findViewById(R.id.search);
        db = FirebaseFirestore.getInstance(); // Initialize Firebase Firestore

        // nav img view
        ImageView navProfile = findViewById(R.id.nav_to_item1Details);
        navProfile.setOnClickListener(view -> {
                    Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                    startActivity(intent);
                });

                // search icon
                ImageView searchIcn = findViewById(R.id.searchItems);
        searchIcn.setOnClickListener(view -> {
            // Do nothing when the search icon is clicked; search is performed in the SearchView
        });

        // chat icon
        ImageView chatIcn = findViewById(R.id.chat);
        chatIcn.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // profile icon
        ImageView profileIcn = findViewById(R.id.profile);
        profileIcn.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // plus icon
        ImageView plusIcn = findViewById(R.id.addItem);
        plusIcn.setOnClickListener(view -> {
            Intent intent = new Intent(SearchActivity.this, ItemActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform a search when the user submits the query
                searchForItem(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle query text changes (if needed)
                return false;
            }
        });
    }

    private void searchForItem(String query) {
        // Replace "items" with the name of your Firebase collection
        db.collection("items")
                .whereEqualTo("itemName", query) // Adjust the field name for your items
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the item details and display them in a toast
                                String itemName = document.getString("itemName");
                                String itemCity = document.getString("itemCity");
                                String itemDate = document.getString("itemDate");
                                String itemDesc = document.getString("itemDesc");
                                String itemRate = document.getString("itemRate");

                                String itemDetails = "Item Name: " + itemName +
                                        "\nCity: " + itemCity +
                                        "\nDate: " + itemDate +
                                        "\nDescription: " + itemDesc +
                                        "\nRate: " + itemRate;

                                Toast.makeText(SearchActivity.this, itemDetails, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Handle errors or display a message when no item is found
                            Toast.makeText(SearchActivity.this, "Item not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}