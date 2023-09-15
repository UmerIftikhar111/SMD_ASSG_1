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

    }
}