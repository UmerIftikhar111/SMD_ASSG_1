package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // nav text view
        TextView registerTextView = findViewById(R.id.navigate_to_register);
        registerTextView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // forgot pwd text view
        TextView forgotPwdTextView = findViewById(R.id.forgotPassword);
        forgotPwdTextView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        //btn
        Button loginBtn = findViewById(R.id.login_btn);
        registerTextView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WelcomeActivityActivity.class);
            startActivity(intent);
        });

    }

    



}



