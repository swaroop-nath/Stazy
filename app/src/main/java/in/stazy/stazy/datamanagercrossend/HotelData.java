package in.stazy.stazy.datamanagercrossend;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

public class HotelData implements Serializable{
    private String name, phoneNumber, description, location, city, uid, token;
    private Bitmap profilePictureHigh = null;

    public static HotelData setData(DocumentSnapshot documentSnapshot) {
        HotelData data = new HotelData();
        data.setName(documentSnapshot.get("name").toString());
        data.setPhoneNumber(documentSnapshot.get("phone_number").toString());
        data.setDescription(documentSnapshot.get("description").toString());
        data.setLocation(documentSnapshot.get("location").toString());  //Unnecessary for this version
        data.setCity(Manager.CITY_VALUE);
        data.setUID(documentSnapshot.get("uid").toString());
        data.setToken(documentSnapshot.get("token").toString());  //Unnecessary for this version

        return data;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Bitmap getProfilePictureHigh() {
        return profilePictureHigh;
    }

    public void setProfilePictureHigh(Bitmap profilePictureHigh) {
        this.profilePictureHigh = profilePictureHigh;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
