package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

        // close capture icon
        ImageView crossIcn = findViewById(R.id.close_capture);
        crossIcn.setOnClickListener(view -> {
            Intent intent = new Intent(CaptureActivity.this, ItemActivity.class);
            startActivity(intent);
        });

    }
}