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

public class Performer extends AppCompatActivity implements View.OnClickListener {

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
    String performerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer);
        ButterKnife.bind(this);
        /*
        TODO: Define behaviours for Shortlist and Hire buttons
        TODO: Write code to download High Res profile picture here
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
        performerToken = receivedPerformer.getToken();
        setContentsOfViews();
        shortlistButton.setOnClickListener(this);
    }

    private void setContentsOfViews() {
        profilePicture.setImageBitmap(receivedPerformer.getProfilePictureLow());
        ratingTextView.setText(receivedPerformer.getRating());
        nameTextView.setText(receivedPerformer.getName());
        genreTextView.setText(receivedPerformer.getGenre());
        facebookLink.setText(receivedPerformer.getFacebook());
        instagramLink.setText(receivedPerformer.getInstagram());
        cityTextView.setText(receivedPerformer.getCity());
        phoneTextView.setText(receivedPerformer.getPhoneNumber());
        descriptionTextView.setText(receivedPerformer.getDescription());
        downloadHighResProfilePicture(receivedPerformer.getUID());
    }

    private void downloadHighResProfilePicture(String uid) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_performer_shortlist_button:
                startShortlistProcedure();
                Toast.makeText(this, "Notification sent to " + receivedPerformer.getName(), Toast.LENGTH_SHORT).show();
            case R.id.activity_performer_hire_button:
                hirePerformer();
                Toast.makeText(this, "Congratulations, you have hired " + receivedPerformer.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hirePerformer() {
    }

    private void startShortlistProcedure() {
        //TODO: Show a dialog to input number of hours and time of performance
        //TODO: Send a notfication to the performer that someone wants to hire him

    }
}
