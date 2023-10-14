package comumer.i200784;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LEFT_MESSAGE = 1;
    private static final int VIEW_TYPE_RIGHT_MESSAGE = 2;
    private List<MessageModel> messageList;
    private String currentUserId;

    public MessageAdapter(List<MessageModel> messageList) {
        currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.messageList = messageList;
    }

    public List<MessageModel> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MessageModel> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LEFT_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_left, parent, false);
            return new LeftMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_RIGHT_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_right, parent, false);
            return new RightMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderUid().equals(currentUserId)) {
            return VIEW_TYPE_RIGHT_MESSAGE;
        } else {
            return VIEW_TYPE_LEFT_MESSAGE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        if (holder instanceof LeftMessageViewHolder) {
            // Bind data for left message view
            LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
            leftMessageViewHolder.receiverMessageBody.setText(message.getMessage());
            leftMessageViewHolder.receiverTime.setText(message.getTime());
            if(message.getSenderProfileUrl()!=null && !message.getSenderProfileUrl().isEmpty())
                Picasso.get().load(message.getSenderProfileUrl()).into(leftMessageViewHolder.receiver_profile_icon);

        } else if (holder instanceof RightMessageViewHolder) {
            // Bind data for right message view
            RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
            rightMessageViewHolder.senderMessageBody.setText(message.getMessage());
            rightMessageViewHolder.senderTime.setText(message.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class LeftMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMessageBody;
        TextView receiverTime;
        ImageView receiver_profile_icon;

        LeftMessageViewHolder(View itemView) {
            super(itemView);
            receiverMessageBody = itemView.findViewById(R.id.receiver_messageBody);
            receiverTime = itemView.findViewById(R.id.receiver_time);
            receiver_profile_icon = itemView.findViewById(R.id.receiver_profile_icon);
        }
    }

    static class RightMessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageBody;
        TextView senderTime;

        RightMessageViewHolder(View itemView) {
            super(itemView);
            senderMessageBody = itemView.findViewById(R.id.messageBody);
            senderTime = itemView.findViewById(R.id.sender_time);
        }
    }
}


