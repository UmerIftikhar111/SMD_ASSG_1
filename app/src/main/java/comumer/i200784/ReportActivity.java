package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    Button reportBtn;
    EditText reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        // Retrieve the item UID from the intent
        String itemUid = getIntent().getStringExtra("itemUid");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize UI components
        reportBtn = findViewById(R.id.reportBtn);
        reason = findViewById(R.id.reason);

        // Handle the report button click
        reportBtn.setOnClickListener(view -> {

            String reportReason = reason.getText().toString();

            // Create a new Firestore document under the "reports" collection
            CollectionReference reportsRef = db.collection("reports");

            // Create a new document and set the fields based on the user's report
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("itemUid", itemUid); // Item UID
            reportData.put("reason", reportReason); // User's reason for the report

            // Add the report document to Firestore
            reportsRef.add(reportData)
                    .addOnSuccessListener(documentReference -> {
                        // Report was successfully added
                        Toast.makeText(ReportActivity.this, "Reported successfully", Toast.LENGTH_SHORT).show();

                        // Redirect the user to the home page or any desired destination
                        Intent intent = new Intent(ReportActivity.this, WelcomeActivityActivity.class);
                        startActivity(intent);
                        finish(); // Close the ReportActivity
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to add the report
                        Toast.makeText(ReportActivity.this, "Failed to report. Please try again later.", Toast.LENGTH_SHORT).show();
                    });

        });

        // nav back arrow icon
        ImageView navBackArrowIcn = findViewById(R.id.nav_back_to_item_detail);
        navBackArrowIcn.setOnClickListener(view -> {
            Intent intent = new Intent(ReportActivity.this, ItemDetailsActivity.class);
            startActivity(intent);
        });

    }
}