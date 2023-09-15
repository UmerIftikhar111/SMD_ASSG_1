package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // forgot pwd text view
        ImageView editProf = findViewById(R.id.editProfile);
        editProf.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, EditprofileActivity.class);
            startActivity(intent);
        });
    }
}