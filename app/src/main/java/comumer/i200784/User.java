package comumer.i200784;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.PropertyName;

public class User {
    private String uid;
    private String name;
    private String email;
    private String contact;
    private String country;
    private String city;

    private String FCMToken;
    private int itemsPosted;
    private int itemsRented;
    private String coverProfileUrl;
    private String mainProfileUrl;
    public static User currentUser;

    public User() {
        // Default constructor required for Firestore
    }

    public User(String name, String email, String contact, String country, String city) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.country = country;
        this.city = city;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("contact")
    public String getContact() {
        return contact;
    }

    @PropertyName("contact")
    public void setContact(String contact) {
        this.contact = contact;
    }

    @PropertyName("country")
    public String getCountry() {
        return country;
    }

    @PropertyName("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @PropertyName("city")
    public String getCity() {
        return city;
    }

    @PropertyName("city")
    public void setCity(String city) {
        this.city = city;
    }

    public int getItemsPosted() {
        return itemsPosted;
    }

    public void setItemsPosted(int itemsPosted) {
        this.itemsPosted = itemsPosted;
    }

    public int getItemsRented() {
        return itemsRented;
    }

    public void setItemsRented(int itemsRented) {
        this.itemsRented = itemsRented;
    }

    public String getCoverProfileUrl() {
        return coverProfileUrl;
    }

    public void setCoverProfileUrl(String coverProfileUrl) {
        this.coverProfileUrl = coverProfileUrl;
    }

    public String getMainProfileUrl() {
        return mainProfileUrl;
    }

    public void setMainProfileUrl(String mainProfileUrl) {
        this.mainProfileUrl = mainProfileUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }
}

