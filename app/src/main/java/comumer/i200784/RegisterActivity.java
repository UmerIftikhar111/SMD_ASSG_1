package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // text view
        TextView registerTextView = findViewById(R.id.navigate_to_login);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event here, e.g., navigate to the register.xml screen
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //btn
        Button loginBtn = findViewById(R.id.register_btn);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event here, e.g., navigate to the register.xml screen
                Intent intent = new Intent(RegisterActivity.this, WelcomeActivityActivity.class);
                startActivity(intent);
            }
        });
    }
}