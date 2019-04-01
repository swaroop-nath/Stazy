package in.stazy.stazy.datamanagerperformer;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;

import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagercrossend.Manager;

public class HotelDataPerformerSide extends HotelData {
    private String date, rating;

    public static HotelDataPerformerSide setData(DocumentSnapshot documentSnapshot, String rating, String date) {
        HotelDataPerformerSide data = new HotelDataPerformerSide();
        data.setName(documentSnapshot.get("name").toString());
        data.setPhoneNumber(documentSnapshot.get("phone_number").toString());
        data.setDescription(documentSnapshot.get("description").toString());
        data.setLocation(documentSnapshot.get("location").toString());  //Unnecessary for this version
        data.setCity(Manager.CITY_VALUE);
        data.setPicName(documentSnapshot.get("pic_name").toString());
        data.setToken(documentSnapshot.get("token").toString());  //Unnecessary for this version
        data.setUID(documentSnapshot.get("uid").toString());
        if (rating != null)
            data.setRating(rating);
        if (date != null)
            data.setDate(date);


        return data;
    }
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getPhoneNumber() {
        return super.getPhoneNumber();
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        super.setPhoneNumber(phoneNumber);
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    public String getLocation() {
        return super.getLocation();
    }

    @Override
    public void setLocation(String location) {
        super.setLocation(location);
    }

    @Override
    public String getCity() {
        return super.getCity();
    }

    @Override
    public void setCity(String city) {
        super.setCity(city);
    }

    @Override
    public Bitmap getProfilePictureHigh() {
        return super.getProfilePictureHigh();
    }

    @Override
    public void setProfilePictureHigh(Bitmap profilePictureHigh) {
        super.setProfilePictureHigh(profilePictureHigh);
    }

    @Override
    public String getPicName() {
        return super.getPicName();
    }

    @Override
    public void setPicName(String picName) {
        super.setPicName(picName);
    }

    @Override
    public String getToken() {
        return super.getToken();
    }

    @Override
    public void setToken(String token) {
        super.setToken(token);
    }

    @Override
    public String getUID() {
        return super.getUID();
    }

    @Override
    public void setUID(String uid) {
        super.setUID(uid);
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
