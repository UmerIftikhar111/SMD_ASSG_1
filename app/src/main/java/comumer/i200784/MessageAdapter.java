package comumer.i200784;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LEFT_MESSAGE = 1;
    private static final int VIEW_TYPE_RIGHT_MESSAGE = 2;
    private List<MessageModel> messageList;
    private String currentUserId;

    Context context;

    public MessageAdapter(Context context, List<MessageModel> messageList) {
        currentUserId=User.currentUser.getUid();
        this.context = context;
        this.messageList=messageList;
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

        if(message.getMessageType().equals("text")){
            if (holder instanceof LeftMessageViewHolder) {
                // Bind data for left message view
                LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
                leftMessageViewHolder.receiverMessageBody.setText(message.getMessage());
                // Parse the input date string
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date;
                try {
                    date = inputFormat.parse(message.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Handle the parse exception if needed
                    return;
                }

                // Format the date to display only the time part
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String formattedTime = outputFormat.format(date);

                // Set the formatted time in your TextView
                leftMessageViewHolder.receiverTime.setText(formattedTime);
                if(message.getSenderProfileUrl()!=null && !message.getSenderProfileUrl().isEmpty())
                    Picasso.get().load(message.getSenderProfileUrl()).into(leftMessageViewHolder.receiver_profile_icon);

            } else if (holder instanceof RightMessageViewHolder) {
                // Bind data for right message view
                RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
                rightMessageViewHolder.senderMessageBody.setText(message.getMessage());
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date;
                try {
                    date = inputFormat.parse(message.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Handle the parse exception if needed
                    return;
                }

                // Format the date to display only the time part
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String formattedTime = outputFormat.format(date);

                // Set the formatted time in your TextView
                rightMessageViewHolder.senderTime.setText(formattedTime);

                rightMessageViewHolder.senderMessageBody.setOnLongClickListener(view -> {
                    showDeleteConfirmationDialog(message); // Pass the appropriate message here
                    return true; // Indicate that the long press event was handled
                });

                rightMessageViewHolder.senderMessageBody.setOnClickListener(view -> {
                    // Handle single tap event (show edit dialog)
                    showEditMessageDialog(message);
                });


            }
        }else if( message.getMessageType().equals("img") ){

            if (holder instanceof LeftMessageViewHolder) {
                // Bind data for left message view
                LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
                Picasso.get().load(message.getMessage()).into(leftMessageViewHolder.receiverImageBody);
                leftMessageViewHolder.receiverTime.setText(message.getTime());
                if(message.getSenderProfileUrl()!=null && !message.getSenderProfileUrl().isEmpty())
                    Picasso.get().load(message.getSenderProfileUrl()).into(leftMessageViewHolder.receiver_profile_icon);

            } else if (holder instanceof RightMessageViewHolder) {
                // Bind data for right message view
                RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
                Picasso.get().load(message.getMessage()).into(rightMessageViewHolder.imageBody);
                rightMessageViewHolder.imageBody.setMaxHeight(20);
                rightMessageViewHolder.imageBody.setMinimumHeight(20);
                rightMessageViewHolder.senderTime.setText(message.getTime());

                rightMessageViewHolder.imageBody.setOnLongClickListener(view -> {
                    showDeleteConfirmationDialog(message); // Pass the appropriate message here
                    return true; // Indicate that the long press event was handled
                });

            }

        }else{

        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class LeftMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMessageBody;
        ImageView receiverImageBody;
        TextView receiverTime;
        ImageView receiver_profile_icon;

        LeftMessageViewHolder(View itemView) {
            super(itemView);
            receiverMessageBody = itemView.findViewById(R.id.receiver_messageBody);
            receiverTime = itemView.findViewById(R.id.receiver_time);
            receiver_profile_icon = itemView.findViewById(R.id.receiver_profile_icon);
            receiverImageBody = itemView.findViewById(R.id.receiver_imageBody);
        }



    }

    static class RightMessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessageBody;
        TextView senderTime;
        ImageView imageBody;

        RightMessageViewHolder(View itemView) {
            super(itemView);
            senderMessageBody = itemView.findViewById(R.id.messageBody);
            senderTime = itemView.findViewById(R.id.sender_time);
            imageBody = itemView.findViewById(R.id.imageBody);
        }
    }

    private void showDeleteConfirmationDialog(MessageModel message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Step 3: Handle User Response

                    // Assuming you have the message time as a string (e.g., "13:05 PM")
                    String messageTimeStr = message.getTime(); // Replace with your actual message time

                    // 1. Get the current time
                    Calendar calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = calendar.get(Calendar.MINUTE);

                    // 2. Parse the message time
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
                    Date messageTime;
                    try {
                        messageTime = timeFormat.parse(messageTimeStr);
                    } catch (ParseException e) {
                        // Handle the parsing exception
                        e.printStackTrace();
                        return;
                    }

                    // 3. Calculate the time difference in milliseconds
                    long currentTimeMillis = (currentHour * 60 + currentMinute) * 60 * 1000;
                    long messageTimeMillis = (messageTime.getHours() * 60 + messageTime.getMinutes()) * 60 * 1000;
                    long timeDifference = currentTimeMillis - messageTimeMillis;

                    // 4. Compare the time difference to 5 minutes (300,000 milliseconds)
                    if (Math.abs(timeDifference) < 300000) {
                        deleteMessageFromDatabase(message);
                    } else {
                        Toast.makeText(context, "The time has passed! ", Toast.LENGTH_SHORT).show();
                    }


                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    public void deleteMessageFromDatabase(MessageModel messageToDelete) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatsReference = databaseReference.child("chats");

        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    // Check if the chat document has a matching message to delete
                    MessageModel chatMessage = chatSnapshot.getValue(MessageModel.class);

                    if (chatMessage != null && areMessagesEqual(chatMessage, messageToDelete)) {
                        // If the chat message matches the message to delete, remove it
                        chatSnapshot.getRef().removeValue();
                        Toast.makeText(context, "message deleted! ", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors or failure to fetch messages
                Toast.makeText(context, "Error deleting message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to check if two messages are equal
    private boolean areMessagesEqual(MessageModel message1, MessageModel message2) {
        // Check if the sender UID, time, and text are the same
        return message1.getSenderUid().equals(message2.getSenderUid()) &&
                message1.getReceiverUid().equals(message2.getReceiverUid()) &&
                message1.getTime().equals(message2.getTime()) &&
                message1.getMessage().equals(message2.getMessage());
    }


    private void showEditMessageDialog(MessageModel message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Message");

        // Create an EditText field in the dialog to edit the message
        EditText editText = new EditText(context);
        editText.setText(message.getMessage());
        builder.setView(editText);

        builder.setPositiveButton("Edit", (dialog, which) -> {
            // Get the edited text from the EditText
            String editedText = editText.getText().toString();

            // Check if the message edit is allowed (less than 5 minutes)
            if (isEditAllowed(message)) {
                // Update the message text in the database
                updateMessageTextInDatabase(message, editedText);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Function to check if message edit is allowed (less than 5 minutes)
    private boolean isEditAllowed(MessageModel message) {

        String messageTimeStr = message.getTime();

        // 1. Get the current time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // 2. Parse the message time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
        Date messageTime;
        try {
            messageTime = timeFormat.parse(messageTimeStr);
        } catch (ParseException e) {
            // Handle the parsing exception
            e.printStackTrace();
            return false;
        }

        // 3. Calculate the time difference in milliseconds
        long currentTimeMillis = (currentHour * 60 + currentMinute) * 60 * 1000;
        long messageTimeMillis = (messageTime.getHours() * 60 + messageTime.getMinutes()) * 60 * 1000;
        long timeDifference = currentTimeMillis - messageTimeMillis;

        // 4. Compare the time difference to 5 minutes (300,000 milliseconds)
        if (Math.abs(timeDifference) < 300000) {
            return true;
        } else {
            Toast.makeText(context, "The time has passed! ", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    // Function to update the message text in the database
    private void updateMessageTextInDatabase(MessageModel message, String editedText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatsReference = databaseReference.child("chats");

        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    MessageModel chatMessage = chatSnapshot.getValue(MessageModel.class);

                    if (areMessagesEqual(chatMessage, message)) {
                        // Update the message text in the database
                        chatMessage.setMessage(editedText);
                        chatSnapshot.getRef().setValue(chatMessage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors or failure to update the message
                Toast.makeText(context, "Error updating message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}


