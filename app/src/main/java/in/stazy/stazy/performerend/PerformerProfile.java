package in.stazy.stazy.performerend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import android.widget.EditText;
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
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.authflow.WaitFragment;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerperformer.PerformerData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;
import in.stazy.stazy.hotelend.Performer;

public class PerformerProfile extends AppCompatActivity implements View.OnClickListener, DialogEarnPriority.CreditsListener {

    //View References
    @BindView(R.id.activity_performer_profile_display_picture)
    CircleImageView profilePicture;
    @BindView(R.id.activity_performer_profile_rating_text_view)
    TextView ratingTextView;
    @BindView(R.id.activity_performer_profile_name_text_view)
    TextView nameTextView;
    @BindView(R.id.activity_performer_profile_genre_text_view)
    TextView genreTextView;
    @BindView(R.id.activity_performer_profile_social_links_item_facebook)
    TextView facebookLink;
    @BindView(R.id.activity_performer_profile_social_links_item_instagram)
    TextView instagramLink;
    @BindView(R.id.activity_performer_profile_city_text_view)
    TextView cityTextView;
    @BindView(R.id.activity_performer_profile_phone_text_view)
    TextView phoneTextView;
    @BindView(R.id.activity_performer_profile_credits_text_view)
    TextView creditsTextView;
    @BindView(R.id.activity_performer_profile_description_text_view)
    TextView descriptionTextView;
    @BindView(R.id.activity_performer_profile_price_text_view)
    TextView priceTextView;
    @BindView(R.id.activity_performer_profile_last_performed_text_view)
    TextView lastPerformedTextView;
    @BindView(R.id.activity_performer_profile_buy_credits_button)
    Button buyCreditsButton;
    @BindView(R.id.activity_main_performer_buy_priority_fab)
    FloatingActionButton buyPriority;
    @BindView(R.id.activity_performer_profile_back_button)
    CircleImageView backButton;
    @BindView(R.id.activity_performer_profile_edit_button)
    CircleImageView editButton;
    @BindView(R.id.performer_profile_contacts_card_facebook_container)
    LinearLayout facebookLinkContainer;
    @BindView(R.id.performer_profile_contacts_card_instagram_container)
    LinearLayout instagramLinkContainer;
    @BindView(R.id.activity_performer_profile_youtube_recycler_view)
    RecyclerView youtubeLinks;
    @BindView(R.id.prev_works_youtube_card_view_performer_profile)
    CardView youtubeContainer;
    @BindView(R.id.rating_card_view)
    CardView ratingCard;

    @BindView(R.id.activity_performer_profile_edit_photo_button)
    CircleImageView photoSelect;
    @BindView(R.id.activity_performer_profile_facebook_user_name_edit_text)
    EditText facebookUserName;
    @BindView(R.id.activity_performer_profile_instagram_user_name_edit_text)
    EditText instagramUserName;
    @BindView(R.id.performer_profile_contacts_card_facebook_uid_container)
    LinearLayout facebookUIDContainer;
    @BindView(R.id.activity_performer_profile_facebook_uid_edit_text)
    EditText facebookUIDText;
    @BindView(R.id.activity_performer_profile_upload_button)
    CircleImageView uploadButton;
    @BindView(R.id.activity_performer_profile_description_edit_text)
    EditText descriptionEditText;

    //Activity References
    private YoutubeAdapter adapter;
    private String picName = null, facebookUserNameString, instagramUserNameString, facebookUIDString;
    private Uri selectedImage;
    private Bitmap newProfilePicture;

    //Constants
    private static final int GET_FROM_GALLERY = 1;
    private static int FLAG_EDIT = 0;

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
        photoSelect.setOnClickListener(this);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        youtubeLinks.setLayoutManager(horizontalLayoutManager);
        uploadButton.setOnClickListener(this);

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
        priceTextView.setText("Price: " + PerformerManager.PERFORMER.getPrice());
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
        StorageReference imageReference = storage.getReference().child(FirebaseAuth.getInstance().getUid() + "/" + PerformerManager.PERFORMER.getPicName());
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
                priority.attachCreditsListener(PerformerProfile.this);
                break;
            case R.id.activity_performer_profile_back_button:
                finish();
                break;
            case R.id.activity_performer_profile_edit_button:
                showEditProcess();
                break;
            case R.id.performer_profile_contacts_card_instagram_container:
                if (PerformerManager.PERFORMER != null) {
                    Intent instagramIntent = getInstagramIntent();
                    startActivity(instagramIntent);
                }
                break;
            case R.id.performer_profile_contacts_card_facebook_container:
                if (PerformerManager.PERFORMER != null) {
                    Intent facebookIntent = getFacebookIntent();
                    startActivity(facebookIntent);
                }
                break;
            case R.id.activity_performer_profile_edit_photo_button:
                launchPhotoPicker();
                break;
            case R.id.activity_performer_profile_upload_button:
                startUpload();
                break;
        }
    }

    private void startUpload() {
        FLAG_EDIT = 0;
        facebookUserNameString = facebookUserName.getText().toString();
        instagramUserNameString = instagramUserName.getText().toString();
        facebookUIDString = facebookUIDText.getText().toString();

        final WaitFragment[] uploadData = {new WaitFragment()};
        uploadData[0].setData("Uploading . . .");
        uploadData[0].show(getSupportFragmentManager(), "upload_data");

        uploadButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        facebookUIDContainer.setVisibility(View.GONE);
        facebookUserName.setVisibility(View.GONE);
        instagramUserName.setVisibility(View.GONE);
        descriptionEditText.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.VISIBLE);
        facebookLink.setVisibility(View.VISIBLE);
        instagramLink.setVisibility(View.VISIBLE);

        DocumentReference performerReference = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("type")
                .document(PerformerManager.TYPE_VALUE).collection(PerformerManager.GENRE_VALUE).document(FirebaseAuth.getInstance().getUid());
        Map<String, Object> performerMap = new HashMap<>();
        if (picName != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid() + "/" + picName);
            imageRef.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(PerformerProfile.this, "Successfully Uploaded Profile Picture", Toast.LENGTH_SHORT).show();
                        if (newProfilePicture != null)
                            PerformerManager.PERFORMER.setProfilePictureHigh(newProfilePicture);
                        StorageReference imageRefDel = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid() + "/" + PerformerManager.PERFORMER.getPicName());
                        imageRefDel.delete();
                        PerformerManager.PERFORMER.setPicName(picName);
                        performerMap.put("pic_name", picName);
                        performerMap.put("facebook", facebookUserNameString);
                        performerMap.put("instagram", instagramUserNameString);
                        performerMap.put("facebook_uid", facebookUIDString);
                        performerMap.put("description", descriptionEditText.getText().toString());

                        performerReference.update(performerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (uploadData[0] != null) {
                                    uploadData[0].dismiss();
                                    uploadData[0] = null;
                                }
                                if (task.isSuccessful()) {
                                    Toast.makeText(PerformerProfile.this, "Data Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                    PerformerManager.PERFORMER.setFacebookUID(facebookUIDString);
                                    PerformerManager.PERFORMER.setFacebookUsername(facebookUserNameString);
                                    PerformerManager.PERFORMER.setInstagramUsername(instagramUserNameString);
                                    facebookLink.setText(facebookUserNameString);
                                    instagramLink.setText(instagramUserNameString);
                                } else {
                                    Toast.makeText(PerformerProfile.this, "Couldn't upload data.\nPlease Try Again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        if (uploadData[0] != null) {
                            uploadData[0].dismiss();
                            uploadData[0] = null;
                        }
                        Toast.makeText(PerformerProfile.this, "Couldn't upload profile picture.\nPlease Try Again.", Toast.LENGTH_SHORT).show();
                        profilePicture.setImageBitmap(PerformerManager.PERFORMER.getProfilePictureHigh());
                    }
                }
            });
        } else {
            performerMap.put("facebook", facebookUserNameString);
            performerMap.put("instagram", instagramUserNameString);
            performerMap.put("facebook_uid", facebookUIDString);

            performerReference.update(performerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (uploadData[0] != null) {
                        uploadData[0].dismiss();
                        uploadData[0] = null;
                    }
                    if (task.isSuccessful()) {
                        Toast.makeText(PerformerProfile.this, "Data Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                        PerformerManager.PERFORMER.setFacebookUID(facebookUIDString);
                        PerformerManager.PERFORMER.setFacebookUsername(facebookUserNameString);
                        PerformerManager.PERFORMER.setInstagramUsername(instagramUserNameString);
                        facebookLink.setText(facebookUserNameString);
                        instagramLink.setText(instagramUserNameString);
                    } else {
                        Toast.makeText(PerformerProfile.this, "Couldn't upload data.\nPlease Try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void launchPhotoPicker() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            profilePicture.setImageURI(selectedImage);
            if (selectedImage.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                try {
                    newProfilePicture = MediaStore.Images.Media.getBitmap(PerformerProfile.this.getContentResolver(), selectedImage);
                    if (cursor != null && cursor.moveToFirst()) {
                        picName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }
    }

    private void showEditProcess() {
        FLAG_EDIT = 1;
        if (facebookLinkContainer.getVisibility() == View.GONE) {
            facebookLinkContainer.setVisibility(View.VISIBLE);
            facebookLink.setVisibility(View.GONE);
            facebookUserName.setVisibility(View.VISIBLE);
        } else {
            facebookUserName.setText(facebookLink.getText().toString());
            facebookLink.setVisibility(View.GONE);
            facebookUserName.setVisibility(View.VISIBLE);
        }
        if (instagramLinkContainer.getVisibility() == View.GONE) {
            instagramLinkContainer.setVisibility(View.VISIBLE);
            instagramLink.setVisibility(View.GONE);
            instagramUserName.setVisibility(View.VISIBLE);
        } else {
            instagramUserName.setText(instagramLink.getText().toString());
            instagramLink.setVisibility(View.GONE);
            instagramUserName.setVisibility(View.VISIBLE);
        }
        ratingCard.setVisibility(View.GONE);
        photoSelect.setVisibility(View.VISIBLE);
        facebookUIDContainer.setVisibility(View.VISIBLE);
        descriptionTextView.setVisibility(View.GONE);
        descriptionEditText.setVisibility(View.VISIBLE);
        descriptionEditText.setText(PerformerManager.PERFORMER.getDescription());
        if (facebookLinkContainer.getVisibility() == View.GONE) {
            facebookUIDText.setText(PerformerManager.PERFORMER.getFacebookUID());
        }
        editButton.setVisibility(View.GONE);
        uploadButton.setVisibility(View.VISIBLE);
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

    @Override
    public void onBackPressed() {
        if (FLAG_EDIT == 1) {
            facebookUIDContainer.setVisibility(View.GONE);
            facebookUserName.setVisibility(View.GONE);
            instagramUserName.setVisibility(View.GONE);
            descriptionEditText.setVisibility(View.GONE);
            facebookLink.setVisibility(View.VISIBLE);
            instagramLink.setVisibility(View.VISIBLE);
            photoSelect.setVisibility(View.GONE);
            ratingCard.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            profilePicture.setImageBitmap(PerformerManager.PERFORMER.getProfilePictureHigh());
            FLAG_EDIT = 0;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void updateCreditsView() {
        creditsTextView.setText("Credits: " + PerformerManager.PERFORMER.getCredits());
    }
}
