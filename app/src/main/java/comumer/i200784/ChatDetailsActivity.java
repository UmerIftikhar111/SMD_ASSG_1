package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatDetailsActivity extends AppCompatActivity {

    TextView send, contactName;
    EditText messageText;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;

    private List<MessageModel> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_details);

        send = findViewById(R.id.send);
        messageText = findViewById(R.id.messageText);
        contactName=findViewById(R.id.contactName);

        contactName.setText(getIntent().getStringExtra("receiverUsername"));

        messageAdapter = new MessageAdapter(messageList);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String receiverUid = getIntent().getStringExtra("receiverUid");
        String receiverProfileUrl = getIntent().getStringExtra("receiverProfileUrl");

        String senderUid = firebaseAuth.getCurrentUser().getUid();

        // Define your DatabaseReference
        DatabaseReference messagesRef = databaseReference.child("chats");
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the current message list to avoid duplications
                messageList.clear();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Parse the message data and add it to your message list
                    MessageModel message = messageSnapshot.getValue(MessageModel.class);
                    // Check if the message meets either of the conditions
                    if ((message.getSenderUid().equals(senderUid) && message.getReceiverUid().equals(receiverUid)) ||
                            (message.getSenderUid().equals(receiverUid) && message.getReceiverUid().equals(senderUid))) {
                        messageList.add(message);
                    }
                }

                // Notify the adapter that the data has changed
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors or failure to fetch messages
                Toast.makeText(ChatDetailsActivity.this, "Error fetching messages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        send.setOnClickListener(v -> {
            // Get the message text from the EditText
            String message = messageText.getText().toString().trim();

            if (!message.isEmpty()) {
                // Create a message object
                MessageModel messageModel = new MessageModel(senderUid, receiverUid, message);
                messageModel.setSenderProfileUrl(receiverProfileUrl);
                // Generate a unique key for the message
                String messageKey = databaseReference.child("chats").push().getKey();
                // Store the message in the Realtime Database under the 'chats' node
                databaseReference.child("chats").child(messageKey).setValue(messageModel);
                // Clear the message input field
                messageText.setText("");

                Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(this, "Please enter a message to send.", Toast.LENGTH_SHORT).show();
        });

        // close screen icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_back_to_chat);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // voice call icon
        ImageView voiceCallIcon = findViewById(R.id.start_voice_call);
        voiceCallIcon.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, VoiceCallActivity.class);
            startActivity(intent);
        });

        // video call icon
        ImageView videoCallIcon = findViewById(R.id.start_video_call);
        videoCallIcon.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, VideoCallActivity.class);
            startActivity(intent);
        });

    }
}