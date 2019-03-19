package in.stazy.stazy.datamanagerperformer;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.DataManager;

public class PerformerData implements DataManager {
    private String name;
    private String phoneNumber;
    private String description;
    private String location;
    private String lastPerformed;
    private String rating;
    private String city;
    private String genre;
    private String lastRating;
    private String price;
    private String pic_name;
    private String token;
    private String facebook, facebookUID;
    private String instagram, instagramUID;
    private String[] prevPerformances;
    private String credits;
    private String uid;
    private Bitmap profilePictureHigh = null;
    private double doubleRating, doubleCredits;
    private long numPerformances;
    private String[] youtubeLinks;

    public static PerformerData setData(DocumentSnapshot documentSnapshot) {
        PerformerData performerData = new PerformerData();

        performerData.setName(documentSnapshot.get("name").toString());
        performerData.setPhoneNumber(documentSnapshot.get("phone_number").toString());
        performerData.setDescription(documentSnapshot.get("description").toString());
        performerData.setLocation(documentSnapshot.get("location").toString());
        performerData.setNumPerformances(Long.valueOf(documentSnapshot.get("num_performances").toString()));
        if (documentSnapshot.getTimestamp("last_performed") != null)
            performerData.setLastPerformed(documentSnapshot.getTimestamp("last_performed").toDate());
        else
            performerData.setLastPerformed(null);
        performerData.setRating(documentSnapshot.get("rating").toString());
        performerData.setCity(Manager.CITY_VALUE);
        performerData.setGenre(PerformerManager.GENRE_VALUE);
        performerData.setLastRating(documentSnapshot.get("last_rating").toString());
        performerData.setPrice(documentSnapshot.get("price").toString());
        performerData.setPicName(documentSnapshot.get("pic_name").toString());
        performerData.setToken(documentSnapshot.get("token").toString());
        performerData.setFacebookUsername(documentSnapshot.get("facebook").toString());
        performerData.setInstagramUsername(documentSnapshot.get("instagram").toString());
//        performerData.setFacebookUID(documentSnapshot.get("facebook_uid").toString());
        performerData.setCredits(documentSnapshot.get("credits").toString());
        performerData.setUID(documentSnapshot.get("uid").toString());
        performerData.setDoubleRating(Double.valueOf(documentSnapshot.get("rating").toString()));
        performerData.setDoubleCredits(Double.valueOf(documentSnapshot.get("credits").toString()));
//        performerData.setYoutubeLinks(documentSnapshot.get("youtube").toString());

        return performerData;
    }

    public static PerformerData setDataOnSignUp(Map<String, String> data) {
        PerformerData performerData = new PerformerData();

        performerData.setName(data.get("name"));
        performerData.setPhoneNumber(data.get("phone_number"));
        performerData.setDescription(data.get("description"));
        performerData.setLocation(data.get("location"));
        performerData.setLastPerformed(null);
        performerData.setRating(String.valueOf(data.get("rating")));
        performerData.setCity(Manager.CITY_VALUE);
        performerData.setGenre(PerformerManager.GENRE_VALUE);
        performerData.setLastRating(String.valueOf(data.get("last_rating")));
        performerData.setPrice(String.valueOf(data.get("price")));
        performerData.setPicName(data.get("pic_name"));
        performerData.setToken(data.get("token"));
        performerData.setFacebookUsername(data.get("facebook"));
        performerData.setInstagramUsername(data.get("instagram"));
        performerData.setCredits(String.valueOf(data.get("credits")));

        return performerData;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
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
        return null;
    }

    @Override
    public void setProfilePictureLow(Bitmap profilePictureLow) {

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
    public void setPicName(String uid) {
        this.pic_name = uid;
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
    public Bitmap getProfilePictureHigh() {
        return profilePictureHigh;
    }

    @Override
    public void setProfilePictureHigh(Bitmap profilePictureHigh) {
        this.profilePictureHigh = profilePictureHigh;
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

    public double getDoubleCredits() {
        return doubleCredits;
    }

    public void setDoubleCredits(double doubleCredits) {
        this.doubleCredits = doubleCredits;
    }

    @Override
    public String getFacebookUID() {
        return facebookUID;
    }

    @Override
    public void setFacebookUID(String facebookUID) {
        this.facebookUID = facebookUID;
    }

    public void setYoutubeLinks(String youtubeLinks) {
        this.youtubeLinks = youtubeLinks.split(",");
    }

    public String[] getYoutubeLinks() {
        return youtubeLinks;
    }
}
