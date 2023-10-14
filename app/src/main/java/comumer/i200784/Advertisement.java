package comumer.i200784;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Advertisement implements Parcelable {
    private String name;
    private String description;
    private String date;
    private double rate;
    private String location;
    private String pictureUrl;
    private String posterUid;
    private String renterUid;


    private String itemUid;

    public Advertisement() {
        // Default constructor
    }

    public Advertisement(String name, String description, String date, double rate, String location, String pictureUrl, String posterUid, String renterUid) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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


    public String getItemUid() {
        return itemUid;
    }

    public void setItemUid(String itemUid) {
        this.itemUid = itemUid;
    }

    protected Advertisement(Parcel in) {
        // Read data from the Parcel and initialize your object
        name = in.readString();
        description = in.readString();
        date = in.readString();
        rate = in.readDouble();
        location = in.readString();
        pictureUrl = in.readString();
        posterUid = in.readString();
        renterUid = in.readString();
        itemUid = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write data to the Parcel
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeDouble(rate);
        dest.writeString(location);
        dest.writeString(pictureUrl);
        dest.writeString(posterUid);
        dest.writeString(renterUid);
        dest.writeString(itemUid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Advertisement> CREATOR = new Creator<Advertisement>() {
        @Override
        public Advertisement createFromParcel(Parcel in) {
            return new Advertisement(in);
        }

        @Override
        public Advertisement[] newArray(int size) {
            return new Advertisement[size];
        }
    };

}

