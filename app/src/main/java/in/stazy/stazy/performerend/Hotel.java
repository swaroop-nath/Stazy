package in.stazy.stazy.performerend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.authflow.MessageService;
import in.stazy.stazy.authflow.WaitFragment;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class Hotel extends AppCompatActivity implements OnCompleteListener<DocumentSnapshot>, View.OnClickListener {

    //View References
    @BindView(R.id.activity_hotel_display_picture) ImageView profilePicture;
    @BindView(R.id.activity_hotel_hotel_name_text_view) TextView hotelNameTextView;
    @BindView(R.id.activity_hotel_hotel_location_text_view) TextView hotelCityTextView;
    @BindView(R.id.activity_hotel_hotel_description) TextView hotelDescriptionTextView;
    @BindView(R.id.activity_hotel_shortlist_text) TextView hotelShortlistText;
    @BindView(R.id.activity_hotel_reject_button) CardView rejectButton;
    @BindView(R.id.activity_hotel_accept_button) CardView acceptButton;
    @BindView(R.id.activity_hotel_hotel_phone_text_view) TextView hotelPhone;

    //Activity Specific References
    private int receivedPosition;
    private String hireDesc;
    private String hireUID;
    private WaitFragment dataDownloading = null;
    private WaitFragment intentShowing = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        receivedPosition = intent.getIntExtra(MainActivityPerformer.INTENT_HOTEL_OBJECT_KEY, 0);
        if (intent.getBooleanExtra(MessageService.SHOW_EXTRA_CONTENT_PERFORMER_END, false)) {
            //TODO: Bug - Displays some bogus text.
//            hireDesc = "<i>" + intent.getStringExtra(MessageService.PERFORMANCE_DETAILS_PERFORMER_END) + " Are you available?</i>";
            hireDesc = "<i>Are you available for performance?</i>";
            hireUID = MessageService.RECEIVED_UID;
            dataDownloading = new WaitFragment();
            dataDownloading.setData("Loading . . .");
            dataDownloading.show(getSupportFragmentManager(), "data_downloading");
            downloadData();
        } else
            setContentsOfViews();

    }

    private void downloadData() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        Log.e("HIREUID", hireUID);
        DocumentReference reference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE).collection("hotels").document(hireUID);
        reference.get().addOnCompleteListener(this);
    }

    private void setContentsOfViews() {
        profilePicture.setImageBitmap(PerformerManager.PREV_HOTELS.get(receivedPosition).getProfilePictureHigh());
        hotelNameTextView.setText(PerformerManager.PREV_HOTELS.get(receivedPosition).getName());
        hotelCityTextView.setText(PerformerManager.PREV_HOTELS.get(receivedPosition).getCity());
        hotelDescriptionTextView.setText("        Description: "+PerformerManager.PREV_HOTELS.get(receivedPosition).getDescription());
//        hotelPhone.setText(PerformerManager.PREV_HOTELS.get(receivedPosition).getPhoneNumber());
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        dataDownloading.dismiss();
        dataDownloading = null;
        if (task.isSuccessful()) {
            PerformerManager.SHORTLIST_HOTEL = HotelData.setData(task.getResult());
            setSpecialContents();
        } else {
            Toast.makeText(this, "Please press the refresh button to try again", Toast.LENGTH_SHORT).show();
            //Introduce a refresh button
        }
    }

    private void setSpecialContents() {
        hotelNameTextView.setText(PerformerManager.SHORTLIST_HOTEL.getName());
        hotelCityTextView.setText(PerformerManager.SHORTLIST_HOTEL.getCity());
        hotelDescriptionTextView.setText("        Description: "+PerformerManager.SHORTLIST_HOTEL.getDescription());
//        hotelPhone.setText(PerformerManager.SHORTLIST_HOTEL.getPhoneNumber());
        hotelShortlistText.setText(Html.fromHtml(hireDesc));

        hotelShortlistText.setVisibility(View.VISIBLE);
        acceptButton.setVisibility(View.VISIBLE);
        rejectButton.setVisibility(View.VISIBLE);

        acceptButton.setOnClickListener(this);
        rejectButton.setOnClickListener(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageReference = storage.getReference().child(PerformerManager.SHORTLIST_HOTEL.getUID()+"/"+PerformerManager.SHORTLIST_HOTEL.getPicName());
        Glide.with(this).asBitmap().load(imageReference).into(profilePicture);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_hotel_reject_button:
                deleteShortlistedCandidate();
                break;
            case R.id.activity_hotel_accept_button:
                intentShowing = new WaitFragment();
                intentShowing.setData("Passing your intent");
                intentShowing.show(getSupportFragmentManager(), "intent_showing");
                final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference notificationReference = firebaseFirestore.collection("NotificationsHotel").document(FirebaseAuth.getInstance().getUid())
                                                            .collection("To").document(hireUID);
                Map<String, String> notificationBody = new HashMap<>();
                notificationBody.put("city", Manager.CITY_VALUE);
                notificationBody.put("type", PerformerManager.TYPE_VALUE);
                notificationBody.put("genre", PerformerManager.GENRE_VALUE);
                notificationBody.put("notification_title", "Performer " + PerformerManager.PERFORMER.getName() + " is available.");
                notificationBody.put("notification_body", PerformerManager.PERFORMER.getName() + " is free to perform at the specified time.");
                notificationReference.set(notificationBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DocumentReference shortlistReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                                                                .collection("Shortlists").document(hireUID).collection("List")
                                                                .document(FirebaseAuth.getInstance().getUid());
                            Map<String, Object> acceptedMap = new HashMap<>();
                            acceptedMap.put("isAccepted", 1);
                            shortlistReference.update(acceptedMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    intentShowing.dismiss();
                                    intentShowing = null;
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Hotel.this, "Nice Choice, We might be contacting you shortly", Toast.LENGTH_SHORT).show();
                                        hotelShortlistText.setVisibility(View.GONE);
                                        acceptButton.setVisibility(View.GONE);
                                        rejectButton.setVisibility(View.GONE);
                                    } else {
                                        Log.e("TAG NOTIF", task.getException().getMessage());
                                        Toast.makeText(Hotel.this, "Couldn't Notify. Please press \"Accept\" again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.e("TAG NOTIF", task.getException().getMessage());
                            Toast.makeText(Hotel.this, "Couldn't Notify. Please press \"Accept\" again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    private void deleteShortlistedCandidate() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE).collection("Shortlists")
                                                .document(hireUID).collection("List").document(FirebaseAuth.getInstance().getUid());
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Hotel.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                    hotelShortlistText.setVisibility(View.GONE);
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(Hotel.this, "Couldn't notify. Please press \"Reject\" again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
