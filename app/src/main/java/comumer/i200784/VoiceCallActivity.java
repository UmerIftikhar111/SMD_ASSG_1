package comumer.i200784;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;




public class VoiceCallActivity extends AppCompatActivity {

    TextView Caller , time;
    public ImageView mute , speaker , hold, cancel;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_call);





        TextView name = findViewById(R.id.name);
        time = findViewById(R.id.time);
        cancel = findViewById(R.id.leave);





//        Caller = findViewById(R.id.caller_name);
//        time = findViewById(R.id.time);
//        mute = findViewById(R.id.mute);
//        speaker = findViewById(R.id.speaker);
//        hold = findViewById(R.id.hold);
//        cancel = findViewById(R.id.end_voice_call);



    }



}