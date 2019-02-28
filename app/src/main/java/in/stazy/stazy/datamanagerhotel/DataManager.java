package in.stazy.stazy.datamanagerhotel;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface DataManager extends Serializable {

    String getName();

    void setName(String name);

    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    String getDescription();

    void setDescription(String description);

    String getLocation();

    void setLocation(String location);

    String getLastPerformed();

    void setLastPerformed(String lastPerformed);

    String getRating();

    void setRating(String rating);

    String getCity();

    void setCity(String city);

    Bitmap getProfilePictureLow();

    void setProfilePictureLow(Bitmap profilePictureLow);

    Bitmap getProfilePictureHigh();

    void setProfilePictureHigh(Bitmap profilePictureHigh);

    String getGenre();

    void setGenre(String genre);

    String getLastRating();

    void setLastRating(String lastRating);

    String getPrice();

    void setPrice(String price);

    String getUID();

    void setUID(String uid);

    String getToken();

    void setToken(String token);

    String getFacebook();

    void setFacebook(String facebook);

    String getInstagram();

    void setInstagram(String instagram);
}
