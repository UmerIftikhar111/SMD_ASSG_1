package comumer.i200784;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ContactAdapter contactAdapter;

    List<ChatUser> chatUsers=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(contactAdapter);

        // Get the current user
        String current_uid = User.currentUser.getUid();

        new FetchUsersTask().execute();


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



    public class FetchUsersTask extends AsyncTask<Void, Void, List<ChatUser>> {

        private static final String TAG = "FetchUsersTask";

        @Override
        protected List<ChatUser> doInBackground(Void... voids) {
            List<ChatUser> userList = new ArrayList<>();

            try {
                URL url = new URL(Utility.ip+"/SPOT-IT/getAllUser.php"); // Replace with your actual users endpoint URL

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
                        JSONArray usersArray = jsonResponse.getJSONArray("items");

                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject userObject = usersArray.getJSONObject(i);

                            ChatUser chatUser = new ChatUser();
                            chatUser.setUserId(userObject.getString("userId"));
                            chatUser.setUsername(userObject.getString("userName"));
                            chatUser.setProfilePictureUrl(userObject.getString("mainProfileUrl"));

                            userList.add(chatUser);
                        }

                        Log.i("chatusersize", String.valueOf(userList.size()));
                        chatUsers=userList;

                       return userList;

                    }
                } finally {
                    urlConnection.disconnect();
                }

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching users: " + e.getMessage());
            }

            return userList;
        }


        @Override
        protected void onPostExecute(List<ChatUser> result) {
            if (result != null) {
                new FetchMessagesTask().execute(result);
            }
        }
    }



    public class FetchMessagesTask extends AsyncTask<List<ChatUser>, Void, List<ChatUser>> {

        private static final String TAG = "FetchMessagesTask";
        @Override
        protected List<ChatUser> doInBackground(List<ChatUser>... chatUserLists) {
            List<MessageModel> messageList = new ArrayList<>();
            List<ChatUser> chatUsers = chatUserLists[0];
            try {
                URL url = new URL(Utility.ip+"/SPOT-IT/getAllMsg.php");

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
                    Log.i("msgresponse", ""+jsonResponse);
                    int status = jsonResponse.getInt("status");

                    if (status == 1) {
                        JSONArray messagesArray = jsonResponse.getJSONArray("messages");

                        for (int i = 0; i < messagesArray.length(); i++) {
                            JSONObject messageObject = messagesArray.getJSONObject(i);

                            MessageModel message = new MessageModel();
                            message.setSenderUid(messageObject.getString("sender_uid"));
                            message.setReceiverUid(messageObject.getString("receiver_uid"));
                            message.setMessage(messageObject.getString("message_text"));
                            message.setTime(messageObject.getString("time_sent"));
                            message.setSenderProfileUrl(messageObject.getString("sender_profile_url"));
                            message.setReadStatus(messageObject.getString("read_status"));
                            message.setMessageType(messageObject.getString("message_type"));

                            messageList.add(message);
                        }

                        List<ChatUser> relevantChatUsers = new ArrayList<>();

                        for (ChatUser chatUser : chatUsers) {
                            String userId = chatUser.getUserId();
                            int unreadMessageCounter = 0;
                            String currentUserId=User.currentUser.getUid();

                            for (MessageModel message : messageList) {
                                if ((message.getSenderUid().equals(userId) && message.getReceiverUid().equals(currentUserId)) ||
                                        (message.getSenderUid().equals(currentUserId) && message.getReceiverUid().equals(userId))) {

                                    // This chat user has messages exchanged with the current user
                                    if (message.getSenderUid().equals(userId) && !message.getReadStatus().equals("true")) {
                                        unreadMessageCounter++;
                                    }
                                }
                            }

                            if (unreadMessageCounter > 0) {
                                // Add the chat user with unread messages to the relevant list
                                ChatUser relevantChatUser = new ChatUser();
                                relevantChatUser.setUserId(userId);
                                relevantChatUser.setUsername(chatUser.getUsername());
                                relevantChatUser.setProfilePictureUrl(chatUser.getProfilePictureUrl());
                                relevantChatUser.setNumOfUnreadMessages(unreadMessageCounter);
                                relevantChatUsers.add(relevantChatUser);
                            }
                        }

                        return relevantChatUsers;


                    }else{
                        Log.i("status", ""+status);
                    }
                } finally {
                    urlConnection.disconnect();
                }

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching messages: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<ChatUser> result) {
            if (result != null) {
                contactAdapter.setContactList(result);
                contactAdapter.notifyDataSetChanged();
            }
        }

    }



}