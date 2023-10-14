package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageView itemImage, posterImage;
    TextView itemRate, itemName, itemDescription, itemLocation, itemDate, posterName, posterItemsAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        Advertisement advertisement = getIntent().getParcelableExtra("itemDetails");


        // back arrow icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_to_items);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemDetailsActivity.this, WelcomeActivityActivity.class);
            startActivity(intent);
        });

        // report
        TextView reportItem = findViewById(R.id.report_item);
        reportItem.setOnClickListener(view -> {
            Intent intent = new Intent(ItemDetailsActivity.this, ReportActivity.class);
            startActivity(intent);
        });

    }
}