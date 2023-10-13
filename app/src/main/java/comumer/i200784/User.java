package comumer.i200784;

import com.google.firebase.firestore.PropertyName;

public class User {
    private String name;
    private String email;
    private String contact;
    private String country;
    private String city;

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
}

