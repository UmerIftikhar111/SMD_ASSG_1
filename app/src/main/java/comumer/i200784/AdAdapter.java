package comumer.i200784;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {



    private List<Advertisement> itemList;
    private Context context;

    public AdAdapter(List<Advertisement> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public List<Advertisement> getItemList() {
        return itemList;
    }

    public void setItemList(List<Advertisement> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        Advertisement item = itemList.get(position);

        // Bind data to the views in ad_item.xml
        holder.adTitle.setText(item.getName());
        holder.adPrice.setText(item.getRate()+"/hr");
        holder.adLocation.setText(item.getLocation());
        holder.adDate.setText(item.getDate());

        Picasso.get().load(item.getPictureUrl()).into(holder.adImage);


        holder.itemBox.setOnClickListener(v -> {

            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("itemDetails", item);
            context.startActivity(intent);

        });

        holder.itemBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(item.getPosterUid().equals(User.currentUser.getUid())){
                    new DeleteItemTask().execute(item.getItemUid());

                }else{
                    Toast.makeText(context, "You cant see me"+item.getPosterUid()+"-"+User.currentUser.getUid(), Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView adImage;
        TextView adTitle, adPrice, adLocation, adDate;

        RelativeLayout itemBox;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.adImage);
            adTitle = itemView.findViewById(R.id.adTitle);
            adPrice = itemView.findViewById(R.id.adPrice);
            adDate = itemView.findViewById(R.id.adDate);
            itemBox = itemView.findViewById(R.id.itemBox);
            adLocation = itemView.findViewById(R.id.adLocation);



        }


    }

    public class DeleteItemTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "DeleteItemTask";

        @Override
        protected String doInBackground(String... params) {
            try {
                // Your deleteItem.php endpoint URL
                URL url = new URL(Utility.ip+"/SPOT-IT/deleteItems.php");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);

                    // Create the JSON object for the item ID
                    String itemId = params[0];

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("item_id", itemId);

                    Log.i("json", jsonParam.toString());

                    // Write JSON data to the request body
                    OutputStream outputStream = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
                    outputStream.close();

                    // Read the response from the server
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));


                    String responseLine;
                    StringBuilder response = new StringBuilder();

                    while ((responseLine = reader.readLine()) != null) {
                        response.append(responseLine);
                    }

                    // Return the response as a string
                    return response.toString();

                } finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error deleting item: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result as needed
            if (result!=null && result.contains("success")) {
                Log.d(TAG, "Item deleted successfully");
                // You might want to notify the user or update your UI accordingly
            } else {
                Log.e(TAG, "Failed to delete item");
                // Handle the case when deleting the item fails
            }
        }
    }




}

