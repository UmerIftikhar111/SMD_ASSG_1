package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // nav to chat details
        ImageView navFwdArrowIcn = findViewById(R.id.nav_to_chat_details);
        navFwdArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, ChatDetailsActivity.class);
            startActivity(intent);
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