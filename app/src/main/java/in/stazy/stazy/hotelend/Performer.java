package in.stazy.stazy.hotelend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.ComedianData;
import in.stazy.stazy.datamanagerhotel.DataManager;
import in.stazy.stazy.datamanagerhotel.MucisianData;
import in.stazy.stazy.datamanagerhotel.OtherData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

import static in.stazy.stazy.hotelend.MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.INDIVIDUAL_PERFORMER_OBJECT_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_COMEDIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_MUCISIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_OTHERS;

public class Performer extends AppCompatActivity implements View.OnClickListener, PerformanceConditionsDialog.ConditionsSetListener {

    //View References
    @BindView(R.id.activity_performer_parent) ConstraintLayout parent;
    @BindView(R.id.activity_performer_display_picture) CircleImageView profilePicture;
    @BindView(R.id.activity_performer_rating_text_view) TextView ratingTextView;
    @BindView(R.id.activity_performer_name_text_view) TextView nameTextView;
    @BindView(R.id.activity_performer_genre_text_view) TextView genreTextView;
    @BindView(R.id.activity_performer_social_links_item_facebook) Button facebookLink;
    @BindView(R.id.activity_performer_social_links_item_instagram) Button instagramLink;
    @BindView(R.id.activity_performer_city_text_view) TextView cityTextView;
    @BindView(R.id.activity_performer_phone_image) ImageView phoneImageView;
    @BindView(R.id.activity_performer_phone_text_view) TextView phoneTextView;
    @BindView(R.id.activity_performer_description_text_view) TextView descriptionTextView;
    @BindView(R.id.activity_performer_shortlist_button) CardView shortlistButton;
    @BindView(R.id.activity_performer_hire_button) CardView hireButton;

    //Activity Specific References
    DataManager receivedPerformer;
    String receivedType;

    //Use this UID of hotel to access hotel details from database in Performer end to get the updated token of the hotel.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer);
        ButterKnife.bind(this);
        /*
        TODO: Define behaviours for Shortlist and Hire buttons
         */

        Intent receivedIntent = getIntent();
        receivedType = receivedIntent.getStringExtra(EXPLORE_INTENT_EXTRA_KEY);
        switch (receivedType) {
            case TYPE_VALUE_MUCISIANS:
                receivedPerformer = (MucisianData) receivedIntent.getSerializableExtra(INDIVIDUAL_PERFORMER_OBJECT_KEY);
                break;
            case TYPE_VALUE_COMEDIANS:
                receivedPerformer = (ComedianData) receivedIntent.getSerializableExtra(INDIVIDUAL_PERFORMER_OBJECT_KEY);
                break;
            case TYPE_VALUE_OTHERS:
                receivedPerformer = (OtherData) receivedIntent.getSerializableExtra(INDIVIDUAL_PERFORMER_OBJECT_KEY);
                break;
        }
        setContentsOfViews();
        shortlistButton.setOnClickListener(this);
    }

    private void setContentsOfViews() {
        downloadHighResProfilePicture();
        ratingTextView.setText(receivedPerformer.getRating());
        nameTextView.setText(receivedPerformer.getName());
        genreTextView.setText(receivedPerformer.getGenre());
        facebookLink.setText(getCleanedUpLink(receivedPerformer.getFacebook()));
        instagramLink.setText(getCleanedUpLink(receivedPerformer.getInstagram()));
        cityTextView.setText(receivedPerformer.getCity());
        phoneTextView.setText(receivedPerformer.getPhoneNumber());
        descriptionTextView.setText("        Description: " + receivedPerformer.getDescription());
    }

    private String getCleanedUpLink(String socialMediaLink) {
        String[] parts = socialMediaLink.split("/");
        return parts[parts.length - 1];
    }

    private void downloadHighResProfilePicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageReference = storage.getReference().child(receivedPerformer.getUID()+"/"+receivedPerformer.getPicName());
        Log.e("TAG", "Downloading....");
        Glide.with(getApplicationContext()).asBitmap().load(imageReference).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                Log.e("TAG", "Downloaded");
                profilePicture.setImageBitmap(resource);
                receivedPerformer.setProfilePictureHigh(resource);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_performer_shortlist_button:
                startShortlistProcedure();
                break;
            case R.id.activity_performer_hire_button:
                hirePerformer();
                break;
        }
    }

    private void hirePerformer() {
        //TODO: Clear the shortlist database.
        //TODO: Put the hired candidate to SQLite Database.
    }

    private void startShortlistProcedure() {
        PerformanceConditionsDialog conditionsDialog = new PerformanceConditionsDialog();
        conditionsDialog.show(getSupportFragmentManager(), "conditions_dialog");
        conditionsDialog.addOnConditionsSetListener(this);
    }

    @Override
    public void onConditionsSet(String performanceTime, String performanceDuration) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference notificationsReferences = firebaseFirestore.collection("NotificationsPerformer").document(FirebaseAuth.getInstance().getUid())
                                                    .collection("To").document(receivedPerformer.getUID());
        Map<String, String> notificationBody = new HashMap<>();
        notificationBody.put("sender_uid", Manager.HOTEL_DATA.getUID());
        notificationBody.put("city", Manager.CITY_VALUE);
        notificationBody.put("type", receivedType);
        notificationBody.put("genre", receivedPerformer.getGenre());
        notificationBody.put("notification_title", "Congratulations, " + Manager.HOTEL_DATA.getName() + " wants to hire you.");
        notificationBody.put("notification_body", "We expect you to perform here at- " + performanceTime + ", for a duration of: " + performanceDuration);

        notificationsReferences.set(notificationBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(parent, "Notification sent to performer.\nYou should contact him/her once (s)he has confirmed.", Snackbar.LENGTH_LONG).show();
                } else {
                    Log.e("TAG NOTIF", task.getException().getMessage());
                    Snackbar.make(parent, "Server Error, Please try again later", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }
}
