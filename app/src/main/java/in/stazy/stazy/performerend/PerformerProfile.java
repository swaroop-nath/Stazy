package in.stazy.stazy.performerend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerperformer.PerformerData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class PerformerProfile extends AppCompatActivity implements View.OnClickListener {

    //View References
    @BindView(R.id.activity_performer_profile_display_picture) CircleImageView profilePicture;
    @BindView(R.id.activity_performer_profile_rating_text_view) TextView ratingTextView;
    @BindView(R.id.activity_performer_profile_name_text_view) TextView nameTextView;
    @BindView(R.id.activity_performer_profile_genre_text_view) TextView genreTextView;
    @BindView(R.id.activity_performer_profile_social_links_item_facebook) TextView facebookLink;
    @BindView(R.id.activity_performer_profile_social_links_item_instagram) TextView instagramLink;
    @BindView(R.id.activity_performer_profile_city_text_view) TextView cityTextView;
    @BindView(R.id.activity_performer_profile_phone_text_view) TextView phoneTextView;
    @BindView(R.id.activity_performer_profile_credits_text_view) TextView creditsTextView;
    @BindView(R.id.activity_performer_profile_description_text_view) TextView descriptionTextView;
    @BindView(R.id.activity_performer_profile_price_text_view) TextView priceTextView;
    @BindView(R.id.activity_performer_profile_last_performed_text_view) TextView lastPerformedTextView;
    @BindView(R.id.activity_performer_profile_buy_credits_button) Button buyCreditsButton;
    @BindView(R.id.activity_main_performer_buy_priority_fab) FloatingActionButton buyPriority;
    @BindView(R.id.activity_performer_profile_back_button) ImageView backButton;
    @BindView(R.id.activity_performer_profile_edit_button) ImageView editButton;
    @BindView(R.id.performer_profile_contacts_card_facebook_container) LinearLayout facebookLinkContainer;
    @BindView(R.id.performer_profile_contacts_card_instagram_container) LinearLayout instagramLinkContainer;
    @BindView(R.id.activity_performer_profile_youtube_recycler_view) RecyclerView youtubeLinks;
    @BindView(R.id.prev_works_youtube_card_view_performer_profile) CardView youtubeContainer;
    //Activity References
    private YoutubeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_profile);
        ButterKnife.bind(this);
        buyCreditsButton.setOnClickListener(this);
        buyPriority.setOnClickListener(this);
        backButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        facebookLinkContainer.setOnClickListener(this);
        instagramLinkContainer.setOnClickListener(this);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        youtubeLinks.setLayoutManager(horizontalLayoutManager);

        if (PerformerManager.PERFORMER == null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE)
                    .collection("type").document(PerformerManager.TYPE_VALUE)
                    .collection(PerformerManager.GENRE_VALUE).document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                        PerformerManager.PERFORMER = PerformerData.setData(task.getResult());
                        setContentsOfViews();
                }
            });
        } else {
            setContentsOfViews();
        }
    }

    private void setContentsOfViews() {

        ratingTextView.setText(PerformerManager.PERFORMER.getRating());
        nameTextView.setText(PerformerManager.PERFORMER.getName());
        genreTextView.setText(PerformerManager.PERFORMER.getGenre());
        if (!TextUtils.isEmpty(PerformerManager.PERFORMER.getFacebookUsername()))
            facebookLink.setText(PerformerManager.PERFORMER.getFacebookUsername());
        else
            facebookLinkContainer.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(PerformerManager.PERFORMER.getInstagramUsername()))
            instagramLink.setText(PerformerManager.PERFORMER.getInstagramUsername());
        else
            instagramLinkContainer.setVisibility(View.GONE);
        cityTextView.setText(PerformerManager.PERFORMER.getCity());
        phoneTextView.setText(PerformerManager.PERFORMER.getPhoneNumber());
        descriptionTextView.setText("        Description: " + PerformerManager.PERFORMER.getDescription());
        creditsTextView.setText("Credits: " + PerformerManager.PERFORMER.getCredits());
        priceTextView.setText("Price: "+PerformerManager.PERFORMER.getPrice());
        lastPerformedTextView.setText(PerformerManager.PERFORMER.getLastPerformed());

        if (PerformerManager.PERFORMER.getYoutubeLinks().length == 0)
            youtubeContainer.setVisibility(View.INVISIBLE);
        else {
            adapter = new YoutubeAdapter(PerformerManager.PERFORMER.getYoutubeLinks(), PerformerProfile.this);
            youtubeLinks.setAdapter(adapter);
        }

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
            case R.id.activity_performer_profile_back_button:
                finish();
                break;
            case R.id.activity_performer_profile_edit_button:
                //start edit process
                break;
            case R.id.performer_profile_contacts_card_instagram_container:
                Intent instagramIntent = getInstagramIntent();
                startActivity(instagramIntent);
                break;
            case R.id.performer_profile_contacts_card_facebook_container:
                Intent facebookIntent = getFacebookIntent();
                startActivity(facebookIntent);
                break;
        }
    }

    private Intent getFacebookIntent() {
        try {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + PerformerManager.PERFORMER.getFacebookUID()));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("www.facebook.com/" + PerformerManager.PERFORMER.getFacebookUID()));
        }
    }

    public Intent getInstagramIntent() {
        try {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + PerformerManager.PERFORMER.getInstagramUsername()));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + PerformerManager.PERFORMER.getInstagramUsername()));
        }
    }
}
