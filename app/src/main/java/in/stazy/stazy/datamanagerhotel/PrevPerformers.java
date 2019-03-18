package in.stazy.stazy.datamanagerhotel;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrevPerformers {
    private String name, location, lastPerformed, city, price, pic_name, uid;
    private Bitmap profilePictureHigh = null;
    private double ratingReceived;
    private String datePerformed;

    public static PrevPerformers setData(DocumentSnapshot docSnap, double ratingReceived, String datePerformed, String city) {
        PrevPerformers performer = new PrevPerformers();

        performer.setName(docSnap.get("name").toString());
        performer.setLocation(docSnap.get("location").toString());
        if (docSnap.getTimestamp("last_performed") != null)
            performer.setLastPerformed(docSnap.getTimestamp("last_performed").toDate());
        else
            performer.setLastPerformed(null);
        performer.setCity(city);
        performer.setPrice(docSnap.get("price").toString());
        performer.setPicName(docSnap.get("pic_name").toString());
        performer.setUID(docSnap.get("uid").toString());
        performer.setRatingReceived(ratingReceived);
        performer.setDatePerformed(datePerformed);
        return performer;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setLastPerformed(Date lastPerformed) {
        if (lastPerformed != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
            this.lastPerformed = formatter.format(lastPerformed);
        } else
            this.lastPerformed = "- - -";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPicName() {
        return pic_name;
    }

    public void setPicName(String pic_name) {
        this.pic_name = pic_name;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public Bitmap getProfilePictureHigh() {
        return profilePictureHigh;
    }

    public void setProfilePictureHigh(Bitmap profilePictureHigh) {
        this.profilePictureHigh = profilePictureHigh;
    }

    public double getRatingReceived() {
        return ratingReceived;
    }

    public void setRatingReceived(double ratingReceived) {
        this.ratingReceived = ratingReceived;
    }

    public String getDatePerformed() {
        return datePerformed;
    }

    public void setDatePerformed(String datePerformed) {
        this.datePerformed = datePerformed;
    }
}
