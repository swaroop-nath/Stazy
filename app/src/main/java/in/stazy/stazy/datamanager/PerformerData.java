package in.stazy.stazy.datamanager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public class PerformerData extends DataManager {
    private String name, phoneNumber, description, location, lastPerformed, rating, city, credits;
    private Bitmap profilePictureLow;
    private Bitmap profilePictureHigh = null;

    @NonNull
    public static PerformerData getInstance() {
        return new PerformerData();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLastPerformed() {
        return lastPerformed;
    }

    public void setLastPerformed(String lastPerformed) {
        this.lastPerformed = lastPerformed;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public Bitmap getProfilePictureLow() {
        return profilePictureLow;
    }

    public void setProfilePictureLow(Bitmap profilePictureLow) {
        this.profilePictureLow = profilePictureLow;
    }

    public Bitmap getProfilePictureHigh() {
        return profilePictureHigh;
    }

    public void setProfilePictureHigh(Bitmap profilePictureHigh) {
        this.profilePictureHigh = profilePictureHigh;
    }
}
