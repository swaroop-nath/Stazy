package in.stazy.stazy.performerend;

import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    @BindView(R.id.activity_main_performer_buy_priority_fab) FloatingActionButton buyPriority;

    //Activity References


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_profile);
        ButterKnife.bind(this);
        buyCreditsButton.setOnClickListener(this);
        buyPriority.setOnClickListener(this);
        setContentsOfViews();


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

        if (PerformerManager.PERFORMER.getProfilePictureHigh() == null)
            downloadProfilePicture();
        else
            profilePicture.setImageBitmap(PerformerManager.PERFORMER.getProfilePictureHigh());
    }

    private void downloadProfilePicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageReference = storage.getReference().child(FirebaseAuth.getInstance().getUid()+"/"+PerformerManager.PERFORMER.getPicName());
        Glide.with(this).asBitmap().load(imageReference).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                PerformerManager.PERFORMER.setProfilePictureHigh(resource);
                profilePicture.setImageBitmap(resource);
            }
        });
    }

    private String getCleanedUpLink(String socialMediaLink) {
        String[] parts = socialMediaLink.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_performer_profile_buy_credits_button:
                PaymentDialog paymentDialog = new PaymentDialog();
                paymentDialog.show(getSupportFragmentManager(), "payment_dialog");
                break;
            case R.id.activity_main_performer_buy_priority_fab:
                DialogEarnPriority priority = new DialogEarnPriority();
                priority.show(getSupportFragmentManager(), "priority_dialog");
                break;
        }
    }
}
