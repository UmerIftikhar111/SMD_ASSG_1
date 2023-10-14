package comumer.i200784;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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




}

