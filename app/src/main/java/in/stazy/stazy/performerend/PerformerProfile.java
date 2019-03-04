package in.stazy.stazy.performerend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class PerformerProfile extends AppCompatActivity implements View.OnClickListener {

    //View References
    @BindView(R.id.activity_performer_profile_display_picture) CircleImageView profilePicture;
    @BindView(R.id.activity_performer_profile_rating_text_view) TextView ratingTextView;
    @BindView(R.id.activity_performer_profile_name_text_view) TextView nameTextView;
    @BindView(R.id.activity_performer_profile_genre_text_view) TextView genreTextView;
    @BindView(R.id.activity_performer_profile_social_links_item_facebook) Button facebookLink;
    @BindView(R.id.activity_performer_profile_social_links_item_instagram) Button instagramLink;
    @BindView(R.id.activity_performer_profile_city_text_view) TextView cityTextView;
    @BindView(R.id.activity_performer_profile_phone_text_view) TextView phoneTextView;
    @BindView(R.id.activity_performer_profile_credits_text_view) TextView creditsTextView;
    @BindView(R.id.activity_performer_profile_description_text_view) TextView descriptionTextView;
    @BindView(R.id.activity_performer_profile_buy_credits_button) CardView buyCreditsButton;

    //Activity References


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_profile);
        ButterKnife.bind(this);
        //setContentsOfViews();

        buyCreditsButton.setOnClickListener(this);

    }

    private void setContentsOfViews() {

        ratingTextView.setText(PerformerManager.PERFORMER.getRating());
        nameTextView.setText(PerformerManager.PERFORMER.getName());
        genreTextView.setText(PerformerManager.PERFORMER.getGenre());
        facebookLink.setText(getCleanedUpLink(PerformerManager.PERFORMER.getFacebook()));
        instagramLink.setText(getCleanedUpLink(PerformerManager.PERFORMER.getInstagram()));
        cityTextView.setText(PerformerManager.PERFORMER.getCity());
        phoneTextView.setText(PerformerManager.PERFORMER.getPhoneNumber());
        descriptionTextView.setText("        Description: " + PerformerManager.PERFORMER.getDescription());
        creditsTextView.setText("Credits: " + PerformerManager.PERFORMER.getCredits());

        downloadProfilePicture();
    }

    private void downloadProfilePicture() {
        //TODO: Write code to download profile picture
    }

    private String getCleanedUpLink(String socialMediaLink) {
        String[] parts = socialMediaLink.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public void onClick(View v) {
        PaymentDialog paymentDialog = new PaymentDialog();
        paymentDialog.show(getSupportFragmentManager(), "payment_dialog");
    }
}
