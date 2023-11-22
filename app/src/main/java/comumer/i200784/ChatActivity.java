package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = databaseReference.child("chats");

        List<ChatUser> chatUsers = new ArrayList<>();

        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Extract user data from Firestore and create a ChatUser object
                String uid = document.getId();

                if(!uid.equals(current_uid)){

                    messagesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int counter = 0;
                            int presentFlag = -1;

                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                MessageModel message = messageSnapshot.getValue(MessageModel.class);
                                if ((message.getSenderUid().equals(current_uid) && message.getReceiverUid().equals(uid)) ||
                                        (message.getSenderUid().equals(uid) && message.getReceiverUid().equals(current_uid))) {




                                    presentFlag = 1;
                                    if ((message.getSenderUid().equals(uid) && message.getReceiverUid().equals(current_uid))
                                            && !message.getReadStatus().equals("true")) {

                                        counter++;
                                    }
                                }
                            }

                            if (presentFlag == 1) {

                                boolean userExists = false;
                                for (ChatUser existingUser : chatUsers) {
                                    if (existingUser.getUserId().equals(uid)) {
                                        userExists = true;
                                        break;
                                    }
                                }

                                if(!userExists){

                                    String username = document.getString("name");
                                    String profileImageUrl = document.getString("mainProfileUrl");

                                    ChatUser chatUser = new ChatUser();
                                    chatUser.setUserId(uid);
                                    chatUser.setUsername(username);
                                    chatUser.setProfilePictureUrl(profileImageUrl);
                                    chatUser.setNumOfUnreadMessages(counter);
                                    chatUsers.add(chatUser);


                                    // Populate the RecyclerView with ChatUsers
                                    contactAdapter.setContactList(chatUsers);
                                    contactAdapter.notifyDataSetChanged();

                                }



                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }


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