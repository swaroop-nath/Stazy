package in.stazy.stazy.hotelend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagerhotel.ComedianData;
import in.stazy.stazy.datamanagerhotel.DataManager;
import in.stazy.stazy.datamanagerhotel.MucisianData;
import in.stazy.stazy.datamanagerhotel.OtherData;

import static in.stazy.stazy.hotelend.MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.INDIVIDUAL_PERFORMER_OBJECT_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_COMEDIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_MUCISIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_OTHERS;

public class Performer extends AppCompatActivity implements View.OnClickListener, PerformanceConditionsDialog.ConditionsSetListener {

    //View References
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

    //Use this UID of hotel to access hotel details from database in Performer end to get the updated token of the hotel.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer);
        ButterKnife.bind(this);
        /*
        TODO: Define behaviours for Shortlist and Hire buttons
        TODO: Write code to download High Res profile picture here and find a way to keep this image in the performer object.
         */

        Intent receivedIntent = getIntent();
        String receivedType = receivedIntent.getStringExtra(EXPLORE_INTENT_EXTRA_KEY);
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
        if (receivedPerformer.getProfilePictureHigh() == null) {
            profilePicture.setImageBitmap(receivedPerformer.getProfilePictureLow());
            downloadHighResProfilePicture(receivedPerformer.getUID());
        } else
            profilePicture.setImageBitmap(receivedPerformer.getProfilePictureHigh());
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

    private void downloadHighResProfilePicture(String uid) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_performer_shortlist_button:
                startShortlistProcedure();
                //TODO: Remove this Toast with a self-dismissed dialog, to display information
                Toast.makeText(this, "Please call at the displayed number, and pres Hire, if you finalize your choice.", Toast.LENGTH_LONG).show();
            case R.id.activity_performer_hire_button:
                hirePerformer();
                Toast.makeText(this, "Congratulations, you have hired " + receivedPerformer.getName(), Toast.LENGTH_LONG).show();
        }
    }

    private void hirePerformer() {
        //TODO: Clear the shortlist database.
        //TODO: Put the hired candidate to SQLite Database.
        //TODO: Think of some way to send the hired information to the performer
    }

    private void startShortlistProcedure() {
        PerformanceConditionsDialog conditionsDialog = new PerformanceConditionsDialog();
        conditionsDialog.show(getSupportFragmentManager(), "conditions_dialog");
    }

    @Override
    public void onConditionsSet(String performanceTime, String performanceDuration) {
        //TODO: Add the shortlisted candidate to SQLite Database.
        phoneImageView.setVisibility(View.VISIBLE);
        phoneTextView.setVisibility(View.VISIBLE);
    }
}
