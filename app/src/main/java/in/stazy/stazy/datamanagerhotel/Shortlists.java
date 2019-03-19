package in.stazy.stazy.datamanagerhotel;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

import in.stazy.stazy.datamanagercrossend.Manager;

public class Shortlists {
    private String name;
    private String phoneNumber;
    private String description;
    private String location;
    private String rating;
    private String city;
    private String genre;
    private String price;
    private String pic_name;
    private String token;
    private String facebook, facebookUID;
    private String instagram, instagramUID;
    private String uid;
    private String type;
    private Date tentativeDate;
    private Bitmap profilePictureHigh = null;
    private double doubleRating;
    private long isHired;
    private long isAccepted;

    public static Shortlists setData(DocumentSnapshot documentSnapshot, long isHired, long isAccepted, String genre, String type, Date tentativeDate) {

        Shortlists performerData = new Shortlists();

        performerData.setName(documentSnapshot.get("name").toString());
        performerData.setPhoneNumber(documentSnapshot.get("phone_number").toString());
        performerData.setDescription(documentSnapshot.get("description").toString());
        performerData.setLocation(documentSnapshot.get("location").toString());
        performerData.setRating(documentSnapshot.get("rating").toString());
        performerData.setCity(Manager.CITY_VALUE);
        performerData.setGenre(genre);
        performerData.setType(type);
        performerData.setPrice(documentSnapshot.get("price").toString());
        performerData.setPicName(documentSnapshot.get("pic_name").toString());
        performerData.setToken(documentSnapshot.get("token").toString());
        performerData.setFacebook(documentSnapshot.get("facebook").toString());
        performerData.setInstagram(documentSnapshot.get("instagram").toString());
        performerData.setUID(documentSnapshot.get("uid").toString());
        performerData.setDoubleRating(Double.valueOf(documentSnapshot.get("rating").toString()));
        performerData.setIsAccepted(isAccepted);
        performerData.setIsHired(isHired);
        performerData.setTentativeDate(tentativeDate);

        return performerData;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
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

    public double getDoubleRating() {
        return doubleRating;
    }

    public void setDoubleRating(double doubleRating) {
        this.doubleRating = doubleRating;
    }

    public long getIsHired() {
        return isHired;
    }

    public void setIsHired(long isHired) {
        this.isHired = isHired;
    }

    public long getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(long isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTentativeDate() {
        return tentativeDate;
    }

    public void setTentativeDate(Date tentativeDate) {
        this.tentativeDate = tentativeDate;
    }

    public String getFacebookUID() {
        return facebookUID;
    }

    public void setFacebookUID(String facebookUID) {
        this.facebookUID = facebookUID;
    }

    public String getInstagramUID() {
        return instagramUID;
    }

    public void setInstagramUID(String instagramUID) {
        this.instagramUID = instagramUID;
    }
}
