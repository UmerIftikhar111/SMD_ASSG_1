package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        // back arrow icon
        ImageView backArrowIcn = findViewById(R.id.navigate_to_search);
        backArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(SearchResultsActivity.this, SearchActivity.class);
            startActivity(intent);
        });


    }
}