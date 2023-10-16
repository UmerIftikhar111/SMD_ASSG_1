package comumer.i200784;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<ChatUser> contactList;
    private Context context;

    public ContactAdapter(List<ChatUser> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    public List<ChatUser> getContactList() {
        return contactList;
    }

    public void setContactList(List<ChatUser> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ChatUser contact = contactList.get(position);
        holder.username.setText(contact.getUsername());

        if(contact.getNumOfUnreadMessages()==0)
            holder.messageInfo.setText("no new message");
        else
            holder.messageInfo.setText( contact.getNumOfUnreadMessages() + " new messages");

        if(contact.getProfilePictureUrl()!=null && !contact.getProfilePictureUrl().isEmpty()) {
            Picasso.get().load(contact.getProfilePictureUrl()).into(holder.displayPic);
        }

        holder.navigator.setOnClickListener(v -> {
            String receiverUid = contact.getUserId();

            Intent intent = new Intent(context, ChatDetailsActivity.class);
            intent.putExtra("receiverUid", receiverUid);
            intent.putExtra("receiverUsername", contact.getUsername());
            intent.putExtra("receiverProfileUrl", User.currentUser.getMainProfileUrl());
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView displayPic, navigator;
        TextView username, messageInfo;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            displayPic = itemView.findViewById(R.id.display_pic);
            username = itemView.findViewById(R.id.username);
            messageInfo = itemView.findViewById(R.id.message_info);
            navigator = itemView.findViewById(R.id.nav_to_chat_details);

        }
    }

}

