package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class VoiceCallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_call);

        // nav to chat details
        ImageView endCallIcn = findViewById(R.id.end_voice_call);
        endCallIcn.setOnClickListener(view -> {
            Intent intent = new Intent(VoiceCallActivity.this, ChatDetailsActivity.class);
            startActivity(intent);
        });

    }
}