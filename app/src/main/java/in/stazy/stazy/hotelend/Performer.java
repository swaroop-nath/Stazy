package in.stazy.stazy.hotelend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import in.stazy.stazy.performerend.YoutubeAdapter;

import static in.stazy.stazy.hotelend.MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.INDIVIDUAL_PERFORMER_OBJECT_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_COMEDIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_MUCISIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_OTHERS;

public class Performer extends AppCompatActivity implements View.OnClickListener, PerformanceConditionsDialog.ConditionsSetListener, OnCompleteListener<DocumentSnapshot>, RateAndPayDialog.RateCommunication {

    //View References
    @BindView(R.id.activity_performer_display_picture)
    CircleImageView profilePicture;
    @BindView(R.id.activity_performer_rating_text_view)
    TextView ratingTextView;
    @BindView(R.id.activity_performer_name_text_view)
    TextView nameTextView;
    @BindView(R.id.activity_performer_genre_text_view)
    TextView genreTextView;
    @BindView(R.id.activity_performer_social_links_item_facebook)
    TextView facebookLink;
    @BindView(R.id.activity_performer_social_links_item_instagram)
    TextView instagramLink;
    @BindView(R.id.performer_contacts_card_facebook_container)
    LinearLayout facebookLinkContainer;
    @BindView(R.id.performer_contacts_card_instagram_container)
    LinearLayout instagramLinkContainer;
    @BindView(R.id.activity_performer_city_text_view)
    TextView cityTextView;
    @BindView(R.id.performer_contacts_card_phone_container)
    LinearLayout phoneContainer;
    @BindView(R.id.activity_performer_phone_text_view)
    TextView phoneText;
    @BindView(R.id.activity_performer_description_text_view)
    TextView descriptionTextView;
    @BindView(R.id.activity_performer_shortlist_button)
    Button shortlistButton;
    @BindView(R.id.activity_performer_hire_button)
    Button hireButton;
    @BindView(R.id.activity_performer_rate_and_pay_button)
    Button rateAndPay;
    @BindView(R.id.activity_performer_youtube_recycler_view)
    RecyclerView youtubeLinks;
    @BindView(R.id.prev_works_youtube_card_view_performer)
    CardView youtubeContainer;
    @BindView(R.id.activity_performer_back_button)
    CircleImageView backButton;

    //Activity Specific References
    private DataManager receivedPerformer;
    private String receivedType;
    private String performerUID;
    private String notifGenre;
    private WaitFragment waitFragment;
    private Shortlists shortlist = null;
    private Intent facebookIntent, instagramIntent;
    private YoutubeAdapter adapter;

    //Use this UID of hotel to access hotel details from database in Performer end to get the updated token of the hotel.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer);
        ButterKnife.bind(this);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        youtubeLinks.setLayoutManager(horizontalLayoutManager);

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
                if (receivedIntent.getIntExtra(ShortlistHiresAdapter.SELECTED_FLAG, 0) == MainActivityHotel.FLAG_SHORTLIST) {
                    //Shortlist opened
                    shortlist = Manager.SHORTLISTED_CANDIDATES.get(position);
                    if (shortlist.getIsAccepted() == 1) {
                        Log.e("TAGGAT", "entered");
                        phoneContainer.setVisibility(View.VISIBLE);
                        hireButton.setVisibility(View.VISIBLE);
                    }
                } else if (receivedIntent.getIntExtra(ShortlistHiresAdapter.SELECTED_FLAG, 0) == MainActivityHotel.FLAG_ACCEPTED) {
                    //Accepted Candidate opened
                    shortlist = Manager.ACCEPTED_SHORTLISTS.get(position);
                    phoneContainer.setVisibility(View.VISIBLE);
                    hireButton.setVisibility(View.VISIBLE);
                }else {
                    //Hire Opened
                    shortlist = Manager.HIRED_CANDIDATES.get(position);
                    phoneContainer.setVisibility(View.VISIBLE);
                    hireButton.setVisibility(View.GONE);
                    rateAndPay.setVisibility(View.VISIBLE);
                }

                profilePicture.setImageBitmap(shortlist.getProfilePictureHigh());
                ratingTextView.setText(shortlist.getRating());
                nameTextView.setText(shortlist.getName());
                genreTextView.setText(shortlist.getGenre());
                if (!TextUtils.isEmpty(shortlist.getFacebookUsername()))
                    facebookLink.setText(shortlist.getFacebookUsername());
                else
                    facebookLinkContainer.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(shortlist.getInstagramUsername()))
                    instagramLink.setText(shortlist.getInstagramUsername());
                else
                    instagramLinkContainer.setVisibility(View.GONE);
                cityTextView.setText(shortlist.getCity());
                phoneText.setText(shortlist.getPhoneNumber());
                descriptionTextView.setText("        Description: "+shortlist.getDescription());

                if (shortlist.getYoutubeLinks().length == 0)
                    youtubeContainer.setVisibility(View.INVISIBLE);
                else {
                    adapter = new YoutubeAdapter(shortlist.getYoutubeLinks(), Performer.this);
                    youtubeLinks.setAdapter(adapter);
                }

                try {
                    facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + shortlist.getFacebookUID()));
                } catch (Exception e) {
                    facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("www.facebook.com/" + shortlist.getFacebookUID()));
                }
                try {
                    instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + shortlist.getInstagramUsername()));
                } catch (Exception e) {
                    instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + shortlist.getInstagramUsername()));
                }
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
        rateAndPay.setOnClickListener(this);
        facebookLinkContainer.setOnClickListener(this);
        instagramLinkContainer.setOnClickListener(this);
        backButton.setOnClickListener(this);
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
        if (!TextUtils.isEmpty(receivedPerformer.getFacebookUsername()))
            facebookLink.setText(receivedPerformer.getFacebookUsername());
        else
            facebookLinkContainer.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(receivedPerformer.getInstagramUsername()))
            instagramLink.setText(receivedPerformer.getInstagramUsername());
        else
            instagramLinkContainer.setVisibility(View.GONE);
        cityTextView.setText(receivedPerformer.getCity());
        phoneText.setText(receivedPerformer.getPhoneNumber());
        descriptionTextView.setText("        Description: " + receivedPerformer.getDescription());

        if (receivedPerformer.getYoutubeLinks().length == 0)
            youtubeContainer.setVisibility(View.INVISIBLE);
        else {
            adapter = new YoutubeAdapter(receivedPerformer.getYoutubeLinks(), Performer.this);
            youtubeLinks.setAdapter(adapter);
        }

        try {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + receivedPerformer.getFacebookUID()));
        } catch (Exception e) {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("www.facebook.com/" + receivedPerformer.getFacebookUID()));
        }

        try {
            instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + receivedPerformer.getInstagramUsername()));
        } catch (Exception e) {
            instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + receivedPerformer.getInstagramUsername()));
        }
    }

    private void downloadHighResProfilePicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageReference = storage.getReference().child(receivedPerformer.getUID() + "/" + receivedPerformer.getPicName());
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
            case R.id.activity_performer_rate_and_pay_button:
                rateAndPayPerformer();
                break;
            case R.id.performer_contacts_card_facebook_container:
                startActivity(facebookIntent);
                break;
            case R.id.performer_contacts_card_instagram_container:
                startActivity(instagramIntent);
                break;
            case R.id.activity_performer_back_button:
                finish();
                break;
        }
    }

    private void rateAndPayPerformer() {
        final DocumentReference prevHotelsList = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("PreviousHotels")
                .document(shortlist.getUID()).collection("List").document(FirebaseAuth.getInstance().getUid());
        final WaitFragment justAMinute = new WaitFragment();
        justAMinute.setData("Just a Minute");
        justAMinute.show(getSupportFragmentManager(), "just_a_minute");
        prevHotelsList.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                justAMinute.dismiss();
                if (task.isSuccessful()) {
                    String payment = task.getResult().get("payment").toString();
                    //Start the dialog.
                    RateAndPayDialog dialog = new RateAndPayDialog();
                    dialog.setData(payment, shortlist.getDoubleRating(), shortlist.getPrice(), shortlist.getName());
                    dialog.attachRateListener(Performer.this);
                    dialog.show(getSupportFragmentManager(), "rating_dialog");

                } else {
                    Toast.makeText(Performer.this, "Server Error, Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        prevHotelsMap.put("date", shortlist.getTentativeDate());
        prevHotelsMap.put("token", shortlist.getToken());

        DocumentReference notificationReference = FirebaseFirestore.getInstance().collection("NotificationsPerformer").document(FirebaseAuth.getInstance().getUid())
                .collection("To").document(shortlist.getUID());
        notificationReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                hireWait.dismiss();
                if (task.isSuccessful()) {
                    prevHotelsMap.put("performance_time", task.getResult().get("performance_time"));
                    prevHotelsMap.put("performance_duration", task.getResult().get("performance_duration"));
                    prevHotelsMap.put("payment", task.getResult().get("payment"));

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
                                            rateAndPay.setVisibility(View.VISIBLE);
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
    public void onConditionsSet(final String performanceTime, final String performanceDuration, final double payment, Date performanceDate) {
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
        DocumentReference approachReferences = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE).collection("Approaches")
                                            .document(receivedPerformer.getUID()).collection("List").document(FirebaseAuth.getInstance().getUid());
        DocumentReference hotelRef = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE).collection("hotels").document(FirebaseAuth.getInstance().getUid());
        Map<String, Object> approachMap = new HashMap<>();
        approachMap.put("approach_reference", hotelRef);
        approachMap.put("uid", FirebaseAuth.getInstance().getUid());

        approachReferences.set(approachMap);

        shortlistMap.put("performer", performerReference);
        shortlistMap.put("isHired", 0);
        shortlistMap.put("isAccepted", 0);
        shortlistMap.put("uid", receivedPerformer.getUID());
        shortlistMap.put("genre", receivedPerformer.getGenre());
        shortlistMap.put("type", receivedType);
        shortlistMap.put("date", performanceDate);

        Map<String, Object> notificationBody = new HashMap<>();
        notificationBody.put("city", Manager.CITY_VALUE);
        notificationBody.put("type", receivedType);
        notificationBody.put("genre", receivedPerformer.getGenre());
        notificationBody.put("performance_time", performanceTime);
        notificationBody.put("performance_duration", performanceDuration);
        notificationBody.put("payment", payment);
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
                                Toast.makeText(Performer.this, "Performer has been notified.\nYou should contact him/her once (s)he has confirmed.", Toast.LENGTH_SHORT).show();
                                shortlistButton.setVisibility(View.GONE);
                            } else {
                                Log.e("TAG NOTIF", task.getException().getMessage());
                                Toast.makeText(Performer.this, "Server Error, Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Log.e("TAG NOTIF", task.getException().getMessage());
                    Toast.makeText(Performer.this, "Server Error, Please try again.", Toast.LENGTH_LONG).show();
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
        if (!TextUtils.isEmpty(result.get("facebook").toString()))
            facebookLink.setText(result.get("facebook").toString());
        else
            facebookLinkContainer.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(result.get("instagram").toString()))
            instagramLink.setText(result.get("instagram").toString());
        else
            instagramLinkContainer.setVisibility(View.GONE);
        cityTextView.setText(Manager.CITY_VALUE);
        phoneText.setText(result.get("phone_number").toString());
        descriptionTextView.setText("        Description: " + result.get("description").toString());
        shortlistButton.setVisibility(View.GONE);
        phoneContainer.setVisibility(View.VISIBLE);
        hireButton.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(result.get("youtube").toString()))
            youtubeContainer.setVisibility(View.INVISIBLE);
        else {
            adapter = new YoutubeAdapter(result.get("youtube").toString().split(","), Performer.this);
            youtubeLinks.setAdapter(adapter);
        }

        try {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + result.get("facebook_uid").toString()));
        } catch (Exception e) {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("www.facebook.com/" + result.get("facebook_uid").toString()));
        }

        try {
            instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + result.get("instagram").toString()));
        } catch (Exception e) {
            instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + result.get("instagram").toString()));
        }

        //TODO: Write code to display profile picture.

        Toast.makeText(this, "You can now call the performer to interview him/her", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRatingSet(double rating, double ratingReceived, double price, String emailOne, String emailTwo) {
        //TODO: Remove the performer from the hire section and move to prev performers for hotel section.
        final WaitFragment uploadRating = new WaitFragment();
        uploadRating.setData("Uploading . . .");
        uploadRating.show(getSupportFragmentManager(), "upload_rating");
        DocumentReference prevHotelsList = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("PreviousHotels")
                .document(shortlist.getUID()).collection("List").document(FirebaseAuth.getInstance().getUid());
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("rating_received", ratingReceived);

        final DocumentReference performerReference = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("type")
                .document(shortlist.getType()).collection(shortlist.getGenre()).document(shortlist.getUID());

        final Map<String, Object> ratingPriceMap = new HashMap<>();

        final DocumentReference shortlistReference = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("Shortlists")
                .document(FirebaseAuth.getInstance().getUid()).collection("List").document(shortlist.getUID());

        final DocumentReference prevPerformances = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("PreviousPerformances")
                .document(FirebaseAuth.getInstance().getUid()).collection(shortlist.getGenre()).document(shortlist.getUID());

        final Map<String, Object> prevPerformancesMap = new HashMap<>();
        prevPerformancesMap.put("rating_received", ratingReceived);

        ratingPriceMap.put("rating", rating);
        ratingPriceMap.put("price", price);
        ratingPriceMap.put("last_rating", ratingReceived);

        shortlistReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    prevPerformancesMap.put("performer", task.getResult().getDocumentReference("performer"));
                    prevPerformancesMap.put("date", task.getResult().getTimestamp("date"));
                    ratingPriceMap.put("last_performed", task.getResult().getTimestamp("date"));
                    prevPerformances.set(prevPerformancesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                shortlistReference.delete();
                                performerReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            ratingPriceMap.put("num_performances", Long.valueOf(task.getResult().get("num_performances").toString()) + 1);
                                            performerReference.update(ratingPriceMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    uploadRating.dismiss();
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Performer.this, "Successfully Uploaded Rating.\nThank You", Toast.LENGTH_SHORT).show();
                                                        rateAndPay.setVisibility(View.GONE);
                                                    } else {
                                                        uploadRating.dismiss();
                                                        Toast.makeText(Performer.this, "Server Error, Please Try Again", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            uploadRating.dismiss();
                                            Toast.makeText(Performer.this, "Server Error, Please Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                uploadRating.dismiss();
                                Toast.makeText(Performer.this, "Server Error, Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    uploadRating.dismiss();
                    Toast.makeText(Performer.this, "Server Error. Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String randomIdOne = UUID.randomUUID().toString();
        String randomIdTwo = UUID.randomUUID().toString();

        DocumentReference emailRef = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("email").document(FirebaseAuth.getInstance().getUid());
        Map<String, String> emailMap = new HashMap<>();

        emailMap.put(randomIdOne, emailOne);
        emailMap.put(randomIdTwo, emailTwo);

        emailRef.set(emailMap);

        prevHotelsList.update(ratingMap);

    }
}
