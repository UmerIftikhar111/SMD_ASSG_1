package comumer.i200784;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageModel {

    private String senderUid;
    private String receiverUid;
    private String message;
    private String time;
    private String senderProfileUrl;
    private String readStatus;
    private String messageType;

    public MessageModel(){}

    public MessageModel(String senderUid, String receiverUid, String message, String messageType) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.time = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
        readStatus="false";
        this.messageType=messageType;

    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderProfileUrl() {
        return senderProfileUrl;
    }

    public void setSenderProfileUrl(String senderProfileUrl) {
        this.senderProfileUrl = senderProfileUrl;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
