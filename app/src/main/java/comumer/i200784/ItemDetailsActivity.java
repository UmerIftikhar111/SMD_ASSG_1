package comumer.i200784;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageView itemImage, posterImage, chatWithPoster;
    TextView itemRate, itemName, itemDescription, itemLocation, itemDate, posterName, posterItemsAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        Advertisement advertisement = getIntent().getParcelableExtra("itemDetails");

        itemImage = findViewById(R.id.itemImage);
        posterImage = findViewById(R.id.posterImage);
        posterName = findViewById(R.id.posterName);
        posterItemsAdded = findViewById(R.id.posterItemsAdded);
        itemRate = findViewById(R.id.itemRate);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemLocation = findViewById(R.id.itemLocation);
        itemDate = findViewById(R.id.itemDate);
        chatWithPoster = findViewById(R.id.chatWithPoster);

        // Set values from the Advertisement object
        itemRate.setText(advertisement.getRate()+"$");
        itemName.setText(advertisement.getName());
        itemDescription.setText(advertisement.getDescription());
        itemLocation.setText(advertisement.getLocation());
        itemDate.setText(advertisement.getDate());
        Picasso.get().load(advertisement.getPictureUrl()).into(itemImage);

        String posterUid = advertisement.getPosterUid();

        // Reference to the Firestore collection where user documents are stored
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");

        // Query the user document using posterUid
        usersCollection.document(posterUid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // User document exists, you can map it to your User class
                User posterUser = documentSnapshot.toObject(User.class);

                posterName.setText(posterUser.getName());
                posterItemsAdded.setText(posterUser.getItemsRented()+" items rented");
                Picasso.get().load(posterUser.getMainProfileUrl()).into(posterImage);

            }
        });

        chatWithPoster.setOnClickListener(v -> {

            Intent intent = new Intent(ItemDetailsActivity.this, ChatDetailsActivity.class);
            intent.putExtra("receiverUid", posterUid);
            intent.putExtra("receiverUsername", posterName.getText());
            intent.putExtra("receiverProfileUrl", User.currentUser.getMainProfileUrl());
            startActivity(intent);

        });

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
            // Add the item UID as an extra to the intent
            intent.putExtra("itemUid", advertisement.getItemUid());
            startActivity(intent);
        });

    }
}