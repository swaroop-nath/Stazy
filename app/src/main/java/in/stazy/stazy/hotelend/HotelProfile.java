package in.stazy.stazy.hotelend;

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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import in.stazy.stazy.R;
import in.stazy.stazy.authflow.WaitFragment;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.performerend.PerformerProfile;

public class HotelProfile extends AppCompatActivity implements OnCompleteListener<DocumentSnapshot>, View.OnClickListener {

    //View References
    @BindView(R.id.activity_hotel_profile_display_picture) ImageView profilePicture;
    @BindView(R.id.activity_hotel_profile_hotel_name_text_view) TextView hotelName;
    @BindView(R.id.activity_hotel_profile_hotel_location_text_view) TextView hotelCity;
    @BindView(R.id.activity_hotel_profile_hotel_description) TextView hotelDescription;
    @BindView(R.id.activity_hotel_profile_hotel_phone_text_view) TextView hotelPhone;
    @BindView(R.id.activity_hotel_profile_back_button) FloatingActionButton backButton;
    @BindView(R.id.activity_hotel_profile_edit_button) FloatingActionButton editButton;
    @BindView(R.id.activity_hotel_profile_upload_button) FloatingActionButton uploadButton;
    @BindView(R.id.activity_hotel_profile_photo_upload) FloatingActionButton pickPhoto;
    @BindView(R.id.activity_hotel_profile_hotel_description_edit_text) EditText descriptionInput;

    //Activity Specific References
    private static final int GET_FROM_GALLERY = 1;
    private Uri selectedImage;
    private Bitmap newProfilePicture;
    private String picName = null, descriptionNewText;
    private static int FLAG_EDIT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_profile);
        ButterKnife.bind(this);
        if (Manager.HOTEL_DATA == null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                    .collection("hotels").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(this);
        } else
            setContentsOfViews();
        backButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        pickPhoto.setOnClickListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.getResult() != null) {
            Manager.HOTEL_DATA = HotelData.setData(task.getResult());
            setContentsOfViews();
        }
        else
            Log.e("Hotel Profile", task.getException().getMessage());
    }

    private void setContentsOfViews() {
        profilePicture.setImageBitmap(Manager.HOTEL_DATA.getProfilePictureHigh());
        hotelName.setText(Manager.HOTEL_DATA.getName());
        hotelCity.setText(Manager.HOTEL_DATA.getCity());
        hotelDescription.setText("        Description: " + Manager.HOTEL_DATA.getDescription());
        hotelPhone.setText(Manager.HOTEL_DATA.getPhoneNumber());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_hotel_profile_back_button:
                finish();
                break;
            case R.id.activity_hotel_profile_edit_button:
                startEditing();
                break;
            case R.id.activity_hotel_profile_upload_button:
                startUpload();
                break;
            case R.id.activity_hotel_profile_photo_upload:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                break;
        }
    }

    private void startUpload() {
        FLAG_EDIT = 0;
        WaitFragment uploadData = new WaitFragment();
        uploadData.setData("Uploading . . .");
        uploadData.show(getSupportFragmentManager(), "upload_data");

        uploadButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        pickPhoto.setVisibility(View.GONE);
        descriptionInput.setVisibility(View.GONE);
        hotelDescription.setVisibility(View.VISIBLE);

        DocumentReference hotelReference = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("hotels")
                                            .document(FirebaseAuth.getInstance().getUid());
        Map<String, Object> hotelMap = new HashMap<>();

        descriptionNewText = descriptionInput.getText().toString();
        hotelMap.put("description", descriptionNewText);

        if (picName != null) {
            StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()+"/"+Manager.HOTEL_DATA.getPicName());
            imageReference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        hotelMap.put("pic_name", picName);
                        StorageReference imageRefDel = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()+"/"+Manager.HOTEL_DATA.getPicName());
                        imageRefDel.delete();
                        Manager.HOTEL_DATA.setPicName(picName);
                        hotelReference.update(hotelMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                uploadData.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(HotelProfile.this, "Successfully Updated Description.", Toast.LENGTH_SHORT).show();
                                    Manager.HOTEL_DATA.setDescription(descriptionNewText);
                                    hotelDescription.setText("        Description: "+descriptionNewText);
                                } else {
                                    Toast.makeText(HotelProfile.this, "Couldn;t Upload Description.\nPlease try Again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        uploadData.dismiss();
                        Toast.makeText(HotelProfile.this, "Couldn't Upload Profile Picture.\nPlease Try Again", Toast.LENGTH_SHORT).show();
                        profilePicture.setImageBitmap(Manager.HOTEL_DATA.getProfilePictureHigh());
                    }
                }
            });
        } else {
            hotelReference.update(hotelMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uploadData.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(HotelProfile.this, "Successfully Updated Description.", Toast.LENGTH_SHORT).show();
                        Manager.HOTEL_DATA.setDescription(descriptionNewText);
                        hotelDescription.setText("        Description: "+descriptionNewText);
                    } else {
                        Toast.makeText(HotelProfile.this, "Couldn;t Upload Description.\nPlease try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void startEditing() {
        FLAG_EDIT = 1;
        pickPhoto.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        uploadButton.setVisibility(View.VISIBLE);
        hotelDescription.setVisibility(View.GONE);
        descriptionInput.setVisibility(View.VISIBLE);
        descriptionInput.setText(Manager.HOTEL_DATA.getDescription());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            profilePicture.setImageURI(selectedImage);
            if (selectedImage.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                try {
                    newProfilePicture = MediaStore.Images.Media.getBitmap(HotelProfile.this.getContentResolver(), selectedImage);
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

    @Override
    public void onBackPressed() {
        if (FLAG_EDIT == 1) {
            descriptionInput.setVisibility(View.GONE);
            hotelDescription.setVisibility(View.VISIBLE);
            pickPhoto.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            profilePicture.setImageBitmap(Manager.HOTEL_DATA.getProfilePictureHigh());
            FLAG_EDIT = 0;
        } else
            super.onBackPressed();
    }
}

