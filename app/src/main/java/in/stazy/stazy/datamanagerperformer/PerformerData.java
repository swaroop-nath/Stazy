package in.stazy.stazy.datamanagerperformer;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;

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
    private String uid;
    private String token;
    private String facebook;
    private String instagram;
    private String[] prevPerformances;
    private String credits;
    private Bitmap profilePictureHigh = null;

    public static PerformerData setData(DocumentSnapshot documentSnapshot) {
        PerformerData performerData = new PerformerData();

        performerData.setName(documentSnapshot.get("name").toString());
        performerData.setPhoneNumber(documentSnapshot.get("phone_number").toString());
        performerData.setDescription(documentSnapshot.get("description").toString());
        performerData.setLocation(documentSnapshot.get("location").toString());
        performerData.setLastPerformed(documentSnapshot.get("last_performed").toString());
        performerData.setRating(documentSnapshot.get("rating").toString());
        performerData.setCity(Manager.CITY_VALUE);
        performerData.setGenre(PerformerManager.GENRE_VALUE);
        performerData.setLastRating(documentSnapshot.get("last_rating").toString());
        performerData.setPrice(documentSnapshot.get("price").toString());
        performerData.setUID(documentSnapshot.get("uid").toString());
        performerData.setToken(documentSnapshot.get("token").toString());
        performerData.setFacebook(documentSnapshot.get("facebook").toString());
        performerData.setInstagram(documentSnapshot.get("instagram").toString());
        performerData.setPrevPerformances(documentSnapshot.get("prev_performances").toString());
        performerData.setCredits(documentSnapshot.get("credits").toString());

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

    public String getUID() {
        return uid;
    }

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

    public String[] getPrevPerformances() {
        return prevPerformances;
    }

    public void setPrevPerformances(String prevPerformances) {
        String[] prevPerformancesReverse = prevPerformances.split(",");
        int n = prevPerformancesReverse.length;
        this.prevPerformances = new String[n];
        for (int i = 0; i < n; i++)
            this.prevPerformances[i] = prevPerformancesReverse[n - i -1];
    }

    @Override
    public Bitmap getProfilePictureHigh() {
        return profilePictureHigh;
    }

    @Override
    public void setProfilePictureHigh(Bitmap profilePictureHigh) {
        this.profilePictureHigh = profilePictureHigh;
    }

}
