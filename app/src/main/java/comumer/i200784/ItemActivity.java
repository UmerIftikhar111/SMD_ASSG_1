package comumer.i200784;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        // close screen icon
        TextView homeIcn = findViewById(R.id.nav_back);
        homeIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemActivity.this, WelcomeActivityActivity.class);
            startActivity(intent);
        });

        // capture pic icon
        ImageView cameraIcn = findViewById(R.id.cameraIcon);
        cameraIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemActivity.this, CaptureActivity.class);
            startActivity(intent);
        });

    }
}