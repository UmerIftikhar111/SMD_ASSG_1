package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoCallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_call);

        // nav to chat details
        ImageView endCallIcn = findViewById(R.id.end_video_call);
        endCallIcn.setOnClickListener(view -> {
            Intent intent = new Intent(VideoCallActivity.this, ChatDetailsActivity.class);
            startActivity(intent);
        });

    }
}