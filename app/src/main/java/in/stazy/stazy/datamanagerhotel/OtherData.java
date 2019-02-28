package in.stazy.stazy.datamanagerhotel;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OtherData implements DataManager{
    private String name, phoneNumber, description, location, lastPerformed, rating, city, genre, lastRating, price, uid, token, facebook, instagram;
    private Bitmap profilePictureLow;
    private Bitmap profilePictureHigh = null;

    public static ArrayList<OtherData> fetchOthers(List<DocumentSnapshot> documentSnapshots, String city, String genre) {
        ArrayList<OtherData> others = new ArrayList<>(10);
        for (DocumentSnapshot docSnap : documentSnapshots) {
            OtherData otherData = new OtherData();
            otherData.setName(docSnap.get("name").toString());
            otherData.setPhoneNumber(docSnap.get("phone_number").toString());
            otherData.setDescription(docSnap.get("description").toString());
            otherData.setLocation(docSnap.get("location").toString());
            otherData.setLastPerformed(docSnap.get("last_performed").toString());
            otherData.setRating(docSnap.get("rating").toString());
            otherData.setCity(city);
            otherData.setGenre(genre);
            otherData.setLastRating(docSnap.get("last_rating").toString());
            otherData.setPrice(docSnap.get("price").toString());
            otherData.setUID(docSnap.get("uid").toString());
            otherData.setToken(docSnap.get("token").toString());
            otherData.setFacebook(docSnap.get("facebook").toString());
            otherData.setInstagram(docSnap.get("instagram").toString());
            others.add(otherData);
        }
        return others;
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
        return lastRating;
    }

    @Override
    public void setPrice(String price) {
        this.price = price;
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
}
