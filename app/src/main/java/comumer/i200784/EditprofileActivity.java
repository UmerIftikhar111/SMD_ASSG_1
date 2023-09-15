package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EditprofileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        // nav text view
        ImageView navProfile = findViewById(R.id.navigate_to_profile);
        navProfile.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = new Intent(EditprofileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

    }
}