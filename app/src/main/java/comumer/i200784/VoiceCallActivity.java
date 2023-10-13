package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VoiceCallActivity extends AppCompatActivity {

    TextView Caller , time;
    ImageView mute , speaker , hold, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_call);


        Caller = findViewById(R.id.caller_name);
        time = findViewById(R.id.time);
        mute = findViewById(R.id.mute);
        speaker = findViewById(R.id.speaker);
        hold = findViewById(R.id.hold);
        cancel = findViewById(R.id.end_voice_call);

        // nav to chat details
        ImageView endCallIcn = findViewById(R.id.end_voice_call);
        endCallIcn.setOnClickListener(view -> {
            Intent intent = new Intent(VoiceCallActivity.this, ChatDetailsActivity.class);
            startActivity(intent);
        });

    }
}