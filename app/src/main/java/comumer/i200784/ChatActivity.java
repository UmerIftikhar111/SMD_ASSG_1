package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(contactAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        // Get the current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String current_uid = currentUser.getUid();

        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<ChatUser> chatUsers = new ArrayList<>();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Extract user data from Firestore and create a ChatUser object
                String uid = document.getId();

                if(!uid.equals(current_uid)){
                    String username = document.getString("name");
                    String profileImageUrl = document.getString("mainProfileUrl");

                    ChatUser chatUser = new ChatUser();
                    chatUser.setUserId(uid);
                    chatUser.setUsername(username);
                    chatUser.setProfilePictureUrl(profileImageUrl);
                    chatUsers.add(chatUser);
                }

            }

            // Populate the RecyclerView with ChatUsers
            contactAdapter.setContactList(chatUsers);
            contactAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle the failure to fetch data from Firestore
            Toast.makeText(this, "Failed to fetch users. Please try again later.", Toast.LENGTH_SHORT).show();
        });

        // home icon
        ImageView homeIcn = findViewById(R.id.homeIcon);
        homeIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, WelcomeActivityActivity.class);
            startActivity(intent);
        });

        // search icon
        ImageView searchIcn = findViewById(R.id.searchItems);
        searchIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // chat icon
        ImageView chatIcn = findViewById(R.id.chat);
        chatIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // profile icon
        ImageView profileIcn = findViewById(R.id.profile);
        profileIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // plus icon
        ImageView plusIcn = findViewById(R.id.addItem);
        plusIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, ItemActivity.class);
            startActivity(intent);
        });


    }
}