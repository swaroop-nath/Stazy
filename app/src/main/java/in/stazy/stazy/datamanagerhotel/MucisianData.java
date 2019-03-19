package in.stazy.stazy.datamanagerhotel;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MucisianData implements DataManager {
    private String name, phoneNumber, description, location, lastPerformed, rating, city, genre, lastRating, price, pic_name, token, facebook, instagram, uid, facebookUID, instagramUID;
    private Bitmap profilePictureLow = null;
    private Bitmap profilePictureHigh = null;
    private double doubleRating;
    private long numPerformances;
    private String[] youtubeLinks;

    public static ArrayList<MucisianData> fetchMucisians(@NonNull List<DocumentSnapshot> documentSnapshots, String city, String genre) {
        ArrayList<MucisianData> mucisians = new ArrayList<>(10);
        for (DocumentSnapshot docSnap : documentSnapshots) {
            MucisianData mucisianData = new MucisianData();
            mucisianData.setName(docSnap.get("name").toString());
            mucisianData.setPhoneNumber(docSnap.get("phone_number").toString());
            mucisianData.setDescription(docSnap.get("description").toString());
            mucisianData.setLocation(docSnap.get("location").toString());
            mucisianData.setNumPerformances(Long.valueOf(docSnap.get("num_performances").toString()));
            if (docSnap.getTimestamp("last_performed") != null)
                mucisianData.setLastPerformed(docSnap.getTimestamp("last_performed").toDate());
            else
                mucisianData.setLastPerformed(null);
            mucisianData.setRating(docSnap.get("rating").toString());
            mucisianData.setCity(city);
            mucisianData.setGenre(genre);
            mucisianData.setLastRating(docSnap.get("last_rating").toString());
            mucisianData.setPrice(docSnap.get("price").toString());
            mucisianData.setPicName(docSnap.get("pic_name").toString());
            mucisianData.setToken(docSnap.get("token").toString());
            mucisianData.setFacebookUsername(docSnap.get("facebook").toString());
            mucisianData.setInstagramUsername(docSnap.get("instagram").toString());
            mucisianData.setFacebookUID(docSnap.get("facebook_uid").toString());
            mucisianData.setUID(docSnap.get("uid").toString());
            mucisianData.setDoubleRating(Double.valueOf(docSnap.get("rating").toString()));
            mucisianData.setYoutubeLinks(docSnap.get("youtube").toString());
            mucisians.add(mucisianData);
        }
        return mucisians;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getLastPerformed() {
        return lastPerformed;
    }

    @Override
    public void setLastPerformed(Date lastPerformed) {
        if (lastPerformed != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
            this.lastPerformed = formatter.format(lastPerformed);
        } else
            this.lastPerformed = "- - -";
    }

    @Override
    public String getRating() {
        return rating;
    }

    @Override
    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public Bitmap getProfilePictureLow() {
        return profilePictureLow;
    }

    @Override
    public void setProfilePictureLow(Bitmap profilePictureLow) {
        this.profilePictureLow = profilePictureLow;
    }

    @Override
    public Bitmap getProfilePictureHigh() {
        return profilePictureHigh;
    }

    @Override
    public void setProfilePictureHigh(Bitmap profilePictureHigh) {
        this.profilePictureHigh = profilePictureHigh;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String getLastRating() {
        return lastRating;
    }

    @Override
    public void setLastRating(String lastRating) {
        this.lastRating = lastRating;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String getPicName() {
        return pic_name;
    }

    @Override
    public void setPicName(String picName) {
        this.pic_name = picName;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getFacebookUsername() {
        return facebook;
    }

    @Override
    public void setFacebookUsername(String facebook) {
        this.facebook = facebook;
    }

    @Override
    public String getInstagramUsername() {
        return instagram;
    }

    @Override
    public void setInstagramUsername(String instagram) {
        this.instagram = instagram;
    }

    @Override
    public String getUID() {
        return uid;
    }

    @Override
    public void setUID(String uid) {
        this.uid = uid;
    }

    @Override
    public double getDoubleRating() {
        return doubleRating;
    }

    @Override
    public void setDoubleRating(double doubleRating) {
        this.doubleRating = doubleRating;
    }

    @Override
    public long getNumPerformances() {
        return numPerformances;
    }

    @Override
    public void setNumPerformances(long numPerformances) {
        this.numPerformances = numPerformances;
    }

    @Override
    public String getFacebookUID() {
        return facebookUID;
    }

    @Override
    public void setFacebookUID(String facebookUID) {
        this.facebookUID = facebookUID;
    }

    @Override
    public String[] getYoutubeLinks() {
        return youtubeLinks;
    }

    @Override
    public void setYoutubeLinks(String youtubeLinks) {
        this.youtubeLinks = youtubeLinks.split(",");
    }
}
