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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.authflow.MessageService;
import in.stazy.stazy.authflow.WaitFragment;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.ComedianData;
import in.stazy.stazy.datamanagerhotel.DataManager;
import in.stazy.stazy.datamanagerhotel.MucisianData;
import in.stazy.stazy.datamanagerhotel.OtherData;
import in.stazy.stazy.datamanagerhotel.Shortlists;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

import static in.stazy.stazy.hotelend.MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.INDIVIDUAL_PERFORMER_OBJECT_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_COMEDIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_MUCISIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_OTHERS;

public class Performer extends AppCompatActivity implements View.OnClickListener, PerformanceConditionsDialog.ConditionsSetListener, OnCompleteListener<DocumentSnapshot> {

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
    private DataManager receivedPerformer;
    private String receivedType;
    private String performerUID;
    private String notifGenre;
    private WaitFragment waitFragment;
    private Shortlists shortlist = null;

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
        if (receivedIntent.getBooleanExtra(MessageService.SHOW_EXTRA_CONTENT_HOTEL_END, false)) {
            performerUID = MessageService.RECEIVED_UID;
            String performerType = receivedIntent.getStringExtra(MessageService.NOTIF_TYPE_RECEIVED);
            notifGenre = receivedIntent.getStringExtra(MessageService.NOTIF_GENRE_RECEIVED);
            waitFragment = new WaitFragment();
            waitFragment.setData("Loading Data . . .");
            waitFragment.show(getSupportFragmentManager(), "loading_data");
            downloadData(performerType, notifGenre);
        } else if (receivedIntent.getBooleanExtra(MainActivityHotel.SHORTLIST_HIRE_INTENT_KEY, false)) {
            int position = receivedIntent.getIntExtra(ShortlistHiresAdapter.CLICKED_POSITION, -1);
            if (position != -1) {
                shortlistButton.setVisibility(View.GONE);
                if (receivedIntent.getIntExtra(ShortlistHiresAdapter.SELECTED_FLAG, 0) == 1) {
                    //Shortlist opened
                    shortlist = Manager.SHORTLISTED_CANDIDATES.get(position);
                    if (shortlist.getIsAccepted() == 1) {
                        Log.e("TAGGAT", "entered");
                        phoneTextView.setVisibility(View.VISIBLE);
                        phoneImageView.setVisibility(View.VISIBLE);
                        hireButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    shortlist = Manager.HIRED_CANDIDATES.get(position);
                    phoneTextView.setVisibility(View.VISIBLE);
                    phoneImageView.setVisibility(View.VISIBLE);
                    hireButton.setVisibility(View.GONE);
                    //Set visibility of Rate and Pay
                }

                profilePicture.setImageBitmap(shortlist.getProfilePictureHigh());
                ratingTextView.setText(shortlist.getRating());
                nameTextView.setText(shortlist.getName());
                genreTextView.setText(shortlist.getGenre());
                facebookLink.setText(getCleanedUpLink(shortlist.getFacebook()));
                instagramLink.setText(getCleanedUpLink(shortlist.getInstagram()));
                cityTextView.setText(shortlist.getCity());
                phoneTextView.setText(shortlist.getPhoneNumber());
                descriptionTextView.setText(shortlist.getDescription());

            }
        } else {
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
        }
        shortlistButton.setOnClickListener(this);
        hireButton.setOnClickListener(this);
    }

    private void downloadData(String performerType, String performerGenre) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                                                .collection("type").document(performerType).collection(performerGenre).document(performerUID);
        documentReference.get().addOnCompleteListener(this);
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
        final WaitFragment hireWait = new WaitFragment();
        hireWait.setData("Notifying Performer . . .");
        hireWait.show(getSupportFragmentManager(), "hire_intent");
        final DocumentReference prevHotelsList = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("PreviousHotels")
                                        .document(shortlist.getUID()).collection("List").document(FirebaseAuth.getInstance().getUid());
        DocumentReference hiringHotel = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("hotels")
                                        .document(FirebaseAuth.getInstance().getUid());
        final Map<String, Object> prevHotelsMap = new HashMap<>();

        final DocumentReference shortlistReference = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("Shortlists")
                .document(FirebaseAuth.getInstance().getUid()).collection("List").document(shortlist.getUID());

        prevHotelsMap.put("hotel", hiringHotel);
        prevHotelsMap.put("rating_received", -1);
        prevHotelsMap.put("uid", shortlist.getUID());

        DocumentReference notificationReference = FirebaseFirestore.getInstance().collection("NotificationsPerformer").document(FirebaseAuth.getInstance().getUid())
                                                .collection("To").document(shortlist.getUID());
        notificationReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                hireWait.dismiss();
                if (task.isSuccessful()) {
                    prevHotelsMap.put("performance_time", task.getResult().get("performance_time"));
                    prevHotelsMap.put("performance_duration", task.getResult().get("performance_duration"));

                    Map<String, Object> hiredMap = new HashMap<>();
                    hiredMap.put("isHired", 1);

                    shortlistReference.update(hiredMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                prevHotelsList.set(prevHotelsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Performer.this, "Congrats, you have hired " + shortlist.getName(), Toast.LENGTH_SHORT).show();
                                            hireButton.setVisibility(View.GONE);
                                            //Display Rate and Pay button
                                        } else {
                                            Toast.makeText(Performer.this, "Server Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Performer.this, "Server Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else {
                    Toast.makeText(Performer.this, "Server Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        

    }

    private void startShortlistProcedure() {
        PerformanceConditionsDialog conditionsDialog = new PerformanceConditionsDialog();
        conditionsDialog.show(getSupportFragmentManager(), "conditions_dialog");
        conditionsDialog.addOnConditionsSetListener(this);
    }

    @Override
    public void onConditionsSet(final String performanceTime, final String performanceDuration) {
        final WaitFragment notify = new WaitFragment();
        notify.setData("Notifying Performer . . .");
        notify.show(getSupportFragmentManager(), "notify");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference notificationsReferences = firebaseFirestore.collection("NotificationsPerformer").document(FirebaseAuth.getInstance().getUid())
                                                    .collection("To").document(receivedPerformer.getUID());

        final DocumentReference shortlistReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE).collection("Shortlists")
                .document(FirebaseAuth.getInstance().getUid()).collection("List").document(receivedPerformer.getUID());
        final Map<String, Object> shortlistMap = new HashMap<>();
        DocumentReference performerReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE).collection("type")
                .document(receivedType).collection(receivedPerformer.getGenre()).document(receivedPerformer.getUID());
        shortlistMap.put("performer", performerReference);
        shortlistMap.put("isHired", 0);
        shortlistMap.put("isAccepted", 0);
        shortlistMap.put("uid", receivedPerformer.getUID());
        shortlistMap.put("genre", receivedPerformer.getGenre());

        Map<String, String> notificationBody = new HashMap<>();
        notificationBody.put("city", Manager.CITY_VALUE);
        notificationBody.put("type", receivedType);
        notificationBody.put("genre", receivedPerformer.getGenre());
        notificationBody.put("performance_time", performanceTime);
        notificationBody.put("performance_duration", performanceDuration);
        //Make a field for hiring date too.
        notificationBody.put("notification_title", "Congratulations, " + Manager.HOTEL_DATA.getName() + " wants to hire you.");
        notificationBody.put("notification_body", "We expect you to perform here at- " + performanceTime + ", for a duration of: " + performanceDuration);

        notificationsReferences.set(notificationBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    shortlistReference.set(shortlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            notify.dismiss();
                            if (task.isSuccessful()) {
                                Snackbar.make(parent, "Performer has been notified.\nYou should contact him/her once (s)he has confirmed.", Snackbar.LENGTH_LONG).show();
                                shortlistButton.setVisibility(View.GONE);
                            } else {
                                Log.e("TAG NOTIF", task.getException().getMessage());
                                Snackbar.make(parent, "Server Error, Please try again.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Log.e("TAG NOTIF", task.getException().getMessage());
                    Snackbar.make(parent, "Server Error, Please try again.", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        waitFragment.dismiss();
        waitFragment = null;
        if (task.isSuccessful()) {
            //Set views
            setSpecialContents(task.getResult());
        } else {
            Log.e("DOWNLOAD NOTIF HOTEL", task.getException().getMessage());
        }
    }

    private void setSpecialContents(DocumentSnapshot result) {
        ratingTextView.setText(result.get("rating").toString());
        nameTextView.setText(result.get("name").toString());
        genreTextView.setText(notifGenre);
        facebookLink.setText(getCleanedUpLink(result.get("facebook").toString()));
        instagramLink.setText(getCleanedUpLink(result.get("instagram").toString()));
        cityTextView.setText(Manager.CITY_VALUE);
        phoneTextView.setText(result.get("phone_number").toString());
        descriptionTextView.setText(result.get("description").toString());
        shortlistButton.setVisibility(View.GONE);
        phoneTextView.setVisibility(View.VISIBLE);
        hireButton.setVisibility(View.VISIBLE);

        //TODO: Write code to display profile picture.

        Toast.makeText(this, "You can now call the performer to interview him/her", Toast.LENGTH_SHORT).show();
    }
}
