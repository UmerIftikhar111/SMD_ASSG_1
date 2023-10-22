package comumer.i200784;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    TextView back;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        email = findViewById(R.id.email);
        back = findViewById(R.id.back);
        reset = findViewById(R.id.reset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();

                if (!userEmail.isEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Password reset email sent successfully.
                                        Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent. Check your email.", Toast.LENGTH_LONG).show();

                                        // Navigate to EmailVerificationActivity.
                                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Failed to send password reset email.
                                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send password reset email. Check your email address.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    // Handle the case where the email field is empty.
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}