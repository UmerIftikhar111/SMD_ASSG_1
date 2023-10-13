package comumer.i200784;

import java.util.Date;

public class Advertisement {
    private String name;
    private String description;
    private Date date;
    private double rate;
    private String location;
    private String pictureUrl;
    private String posterUid;
    private String renterUid;

    public Advertisement() {
        // Default constructor required for Firestore
    }

    public Advertisement(String name, String description, Date date, double rate, String location, String pictureUrl, String posterUid, String renterUid) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.rate = rate;
        this.location = location;
        this.pictureUrl = pictureUrl;
        this.posterUid = posterUid;
        this.renterUid = renterUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPosterUid() {
        return posterUid;
    }

    public void setPosterUid(String posterUid) {
        this.posterUid = posterUid;
    }

    public String getRenterUid() {
        return renterUid;
    }

    public void setRenterUid(String renterUid) {
        this.renterUid = renterUid;
    }
}

