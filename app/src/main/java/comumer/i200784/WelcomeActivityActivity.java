package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        // search icon
        ImageView searchIcn = findViewById(R.id.searchItems);
        searchIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivityActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // chat icon
        ImageView chatIcn = findViewById(R.id.searchItems);
        chatIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivityActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        // profile icon
        ImageView profileIcn = findViewById(R.id.searchItems);
        profileIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivityActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // plus icon
        ImageView plusIcn = findViewById(R.id.addItem);
        plusIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivityActivity.this, ItemActivity.class);
                startActivity(intent);
            }
        });


        // profile icon
        RelativeLayout itemBox = findViewById(R.id.Item1Box);
        itemBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivityActivity.this, ItemDetailsActivity.class);
                startActivity(intent);
            }
        });






    }
}