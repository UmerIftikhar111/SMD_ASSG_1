package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        // nav back arrow icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_back_to_item_detail);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ReportActivity.this, ItemDetailsActivity.class);
            startActivity(intent);
        });

    }
}