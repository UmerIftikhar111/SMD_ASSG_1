package comumer.i200784;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class ChatDetailsActivity extends AppCompatActivity {

    // Fill the App ID of your project generated on Agora Console.


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_details);


        // close screen icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_back_to_chat);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // voice call icon
        Button joinLeaveButton = findViewById(R.id.joinLeaveButton);

        joinLeaveButton.setOnClickListener(view -> {
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