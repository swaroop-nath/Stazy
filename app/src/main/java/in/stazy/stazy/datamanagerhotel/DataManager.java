package in.stazy.stazy.datamanagerhotel;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public interface DataManager extends Serializable {

    String getName();

    void setName(String name);

    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    String getDescription();

    void setDescription(String description);

    String getLocation(); //Unnecessary for this version

    void setLocation(String location); //Unnecessary for this version

    String getLastPerformed();

    void setLastPerformed(Date lastPerformed);

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

    String getPicName();

    void setPicName(String picName);

    String getToken(); //Unnecessary for this version

    void setToken(String token); //Unnecessary for this version

    String getFacebook();

    void setFacebook(String facebook);

    String getInstagram();

    void setInstagram(String instagram);

    String getUID();

    void setUID(String uid);

    double getDoubleRating();

    void setDoubleRating(double doubleRating);

    long getNumPerformances();

    void setNumPerformances(long numPerformances);
}
