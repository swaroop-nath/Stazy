package in.stazy.stazy.datamanagerhotel;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MucisianData implements DataManager {
    private String name, phoneNumber, description, location, lastPerformed, rating, city, genre, lastRating, price, pic_name, token, facebook, instagram, uid;
    private Bitmap profilePictureLow = null;
    private Bitmap profilePictureHigh = null;

    public static ArrayList<MucisianData> fetchMucisians(@NonNull List<DocumentSnapshot> documentSnapshots, String city, String genre) {
        ArrayList<MucisianData> mucisians = new ArrayList<>(10);
        for (DocumentSnapshot docSnap : documentSnapshots) {
            MucisianData mucisianData = new MucisianData();
            mucisianData.setName(docSnap.get("name").toString());
            mucisianData.setPhoneNumber(docSnap.get("phone_number").toString());
            mucisianData.setDescription(docSnap.get("description").toString());
            mucisianData.setLocation(docSnap.get("location").toString());
            mucisianData.setLastPerformed(docSnap.get("last_performed").toString());
            mucisianData.setRating(docSnap.get("rating").toString());
            mucisianData.setCity(city);
            mucisianData.setGenre(genre);
            mucisianData.setLastRating(docSnap.get("last_rating").toString());
            mucisianData.setPrice(docSnap.get("price").toString());
            mucisianData.setPicName(docSnap.get("pic_name").toString());
            mucisianData.setToken(docSnap.get("token").toString());
            mucisianData.setFacebook(docSnap.get("facebook").toString());
            mucisianData.setInstagram(docSnap.get("instagram").toString());
            mucisianData.setUID(docSnap.get("uid").toString());

            //TODO: Write code to download low res profile picture
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
    public void setLastPerformed(String lastPerformed) {
        this.lastPerformed = lastPerformed;
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
    public String getFacebook() {
        return facebook;
    }

    @Override
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    @Override
    public String getInstagram() {
        return instagram;
    }

    @Override
    public void setInstagram(String instagram) {
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
}
