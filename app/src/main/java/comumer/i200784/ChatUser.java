package comumer.i200784;

public class ChatUser {

    private String userId;
    private String username;
    private String profilePictureUrl;
    private String status;

    public ChatUser(){}

    public ChatUser(String userId, String username, String profilePictureUrl, String status) {
        this.userId = userId;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
