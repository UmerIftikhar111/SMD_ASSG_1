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
    private final String appId = "0283cb05259049acaae50273aa7e7f6f";
    // Fill the channel name.
    private String channelName = "Spot IT";
    // Fill the temp token generated on Agora Console.
    private String token = "007eJxTYJjyjYclfr3zP/96I1ue+ZFpHl8yz97j1T/N8/hezpGdAp0KDAZGFsbJSQamRqaWBiaWicmJiammBkbmxomJ5qnmaWZp6nu1UhsCGRm0dhxlYWSAQBCfnSG4IL9EwTOEgQEAZ5QfXg==";
    // An integer that identifies the local user.
    private int uid = 0;
    // Track the status of your connection
    private boolean isJoined = false;

    // Agora engine instance
    private RtcEngine agoraEngine;
    private TextView infoText;
    private Button joinLeaveButton;


    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }


    private void setupVoiceSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> {
                infoText.setText("Remote user joined: " + uid);
                Toast.makeText(getApplicationContext(), "User " + uid + " joined the channel", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            runOnUiThread(() -> {
                infoText.setText("Waiting for a remote user to join");
                Toast.makeText(getApplicationContext(), "Joined the channel successfully", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            if (isJoined) {
                runOnUiThread(() -> {
                    infoText.setText("Waiting for a remote user to join");
                    Toast.makeText(getApplicationContext(), "User " + uid + " left the channel", Toast.LENGTH_SHORT).show();
                });
            }
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            runOnUiThread(() -> {
                infoText.setText("Press the button to join a channel");
                Toast.makeText(getApplicationContext(), "Left the channel", Toast.LENGTH_SHORT).show();
            });
            isJoined = false;
        }
    };


    private void joinChannel() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.autoSubscribeAudio = true;
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channelName, uid, options);
    }




    public void joinLeaveChannel(View view) {
        if (isJoined) {
            agoraEngine.leaveChannel();
        } else {
            joinChannel();
        }
    }




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_details);

        setupVoiceSDKEngine();

        // close screen icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_back_to_chat);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailsActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // voice call icon
        joinLeaveButton = findViewById(R.id.joinLeaveButton);
        infoText = findViewById(R.id.infoText);
        joinLeaveButton.setOnClickListener(view -> {
            joinLeaveChannel(joinLeaveButton);
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
    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.leaveChannel();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }
}