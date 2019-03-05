package in.stazy.stazy.authflow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, OTPFragment.OTPCommunication, AdapterView.OnItemSelectedListener {

    //View References
    //Common
    @BindView(R.id.sign_up_activity_path_chooser_container) ConstraintLayout initialContainer;
    @BindView(R.id.sign_up_activity_hotel_button) CardView hotelSelection;
    @BindView(R.id.sign_up_activity_performer_button) CardView performerSelection;
    @BindView(R.id.sign_up_activity_inital_introduction_text_view) TextView introductionTextView; //TODO: Set typeface to some stylish one.
    @BindView(R.id.sign_up_activity_parent) FrameLayout parent;

    //Hotel Side
    @BindView(R.id.sign_up_activity_hotel_layout) ScrollView hotelContainer;
    @BindView(R.id.sign_up_activity_hotel_name_input) TextInputEditText hotelName;
    @BindView(R.id.sign_up_activity_hotel_phone_number_prefix) TextInputEditText hotelPhonePrefix;
    @BindView(R.id.sign_up_activity_hotel_phone_number_input) TextInputEditText hotelPhoneNumber;
    @BindView(R.id.activity_sign_up_hotel_description_input) TextInputEditText hotelDescription;
    @BindView(R.id.sign_up_activity_hotel_city_input) Spinner hotelCity;
    @BindView(R.id.activity_sign_up_hotel_upload_image_input) TextInputEditText hotelImageName;
    @BindView(R.id.activity_sign_up_hotel_button) CardView hotelSignUp;

    //Performer Side
    @BindView(R.id.sign_up_activity_performer_layout) ScrollView performerContainer;
    @BindView(R.id.sign_up_activity_performer_name_input) TextInputEditText performerName;
    @BindView(R.id.sign_up_activity_performer_phone_number_prefix) TextInputEditText performerPhonePrefix;
    @BindView(R.id.sign_up_activity_performer_phone_number_input) TextInputEditText performerPhoneNumber;
    @BindView(R.id.activity_sign_up_performer_description_input) TextInputEditText performerDescription;
    @BindView(R.id.sign_up_activity_performer_city_input) Spinner performerCity;
    @BindView(R.id.activity_sign_up_performer_upload_image_input) TextInputEditText performerImageName;
    @BindView(R.id.activity_sign_up_performer_facebook_link_input) TextInputEditText performerFacebook;
    @BindView(R.id.activity_sign_up_performer_instagram_link_input) TextInputEditText performerInstagram;
    @BindView(R.id.activity_sign_up_performer_type_input) Spinner performerType;
    @BindView(R.id.activity_sign_up_performer_genre_input) Spinner performerGenre;
    @BindView(R.id.activity_sign_up_performer_price_input) TextInputEditText performerPrice;
    @BindView(R.id.activity_sign_up_performer_button) CardView performerSignUp;

    //Activity Specific References
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthCredential mCredential;
    private String mVerificationId;
    private String mOTP;
    private Context context;
    private Map<String, String> hotelData = new HashMap<>();
    private OTPFragment fragment = null;
    private Map<String, String> performerData = new HashMap<>();
    private Uri selectedImage;
    private String picName;
    private ArrayAdapter<String> mucisiansAdapter, comediansAdapter, othersAdapter;

    //Constant Fields
    private static final int GET_FROM_GALLERY = 1;
    static int INITIAL_SELECTION ;
    static int HOTEL = 0;
    static int PERFORMER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        context = getApplicationContext();

        //Initial View manipulations
        hotelSelection.setOnClickListener(this);
        performerSelection.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                //This is login credential, send this to SignInActivity and login
                Log.e("Callback", "onVerificationCompleted called");
                mCredential = phoneAuthCredential;

                //TODO: Show some progress dialogs.
                if (fragment != null)
                    fragment.dismiss();
                if (INITIAL_SELECTION == HOTEL)
                    SignInManager.signInWithNewCredentials(mCredential, context, hotelData, selectedImage, hotelCity.getSelectedItem().toString());
                else if (INITIAL_SELECTION == PERFORMER)
                    SignInManager.signInWithNewCredentials(mCredential, context, performerData, selectedImage, performerCity.getSelectedItem().toString());

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e("Callback", "onVerificationFailed called");
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(parent, "Please Enter a Valid Phone Number", Snackbar.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(parent, "Server Error, Try Again later", Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                mVerificationId = verificationId;
                Log.e("Callback", "onCodeSent called");
                fragment = new OTPFragment();
                fragment.attachCommunicationListener(SignUpActivity.this);
                fragment.show(getSupportFragmentManager(), "otp_fragment");
            }

        };

        mucisiansAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinner_genre_mucisians_entries));
        comediansAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinner_genre_comedians_entries));
        othersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinner_genre_others_entries));

        performerType.setOnItemSelectedListener(this);
        //Hotel Sign Up Setup
        hotelSignUp.setOnClickListener(this);
        performerSignUp.setOnClickListener(this);


    }

    public void uploadImage(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            if (selectedImage.getScheme().equals("content")) {
                    Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            picName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            if (INITIAL_SELECTION == HOTEL)
                                hotelImageName.setText(picName);
                            else if (INITIAL_SELECTION == PERFORMER);
                                performerImageName.setText(picName);
                        }
                    } finally {
                        cursor.close();
                    }
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_activity_hotel_button:
                //Hotel Button Clicked
                INITIAL_SELECTION = HOTEL;
                doSwipeAnimation();
                break;
            case R.id.sign_up_activity_performer_button:
                //Performer Button Clicked
                INITIAL_SELECTION = PERFORMER;
                doSwipeAnimation();
                break;
            case R.id.activity_sign_up_hotel_button:
                //Hotel Sign Up clicked
                signHotelUp();
                break;
            case R.id.activity_sign_up_performer_button:
                //Performer Sign Up clicked
                signPerformerUp();
                break;
        }
    }

    private void signPerformerUp() {
        if (checkLegitimacyPerformer()) {
            performerData.put("name", performerName.getText().toString());
            performerData.put("phone_number", performerPhonePrefix.getText().toString()+performerPhoneNumber.getText().toString());
            performerData.put("description", performerDescription.getText().toString());
            performerData.put("facebook", performerFacebook.getText().toString());
            performerData.put("instagram", performerInstagram.getText().toString());
            performerData.put("price", performerPrice.getText().toString());
            performerData.put("location", "");
            performerData.put("last_performed", "");
            performerData.put("rating", "3");
            performerData.put("last_rating", "");
            performerData.put("token", "");
            performerData.put("prev_performances", "");
            performerData.put("credits", "2");
            performerData.put("pic_name", picName);

            PerformerManager.TYPE_VALUE = performerType.getSelectedItem().toString();
            PerformerManager.GENRE_VALUE = performerGenre.getSelectedItem().toString();

            PhoneAuthProvider.getInstance().verifyPhoneNumber(performerPhonePrefix.getText().toString()+performerPhoneNumber.getText().toString(),
                                                                60,
                                                                TimeUnit.SECONDS,
                                                                this,
                                                                mCallbacks);
        }
    }

    private boolean checkLegitimacyPerformer() {

        if (TextUtils.isEmpty(performerName.getText().toString())) {
            performerName.setError("Please Type Your Name");
            return false;
        }
        if (TextUtils.isEmpty(performerPhoneNumber.getText().toString())) {
            performerPhoneNumber.setError("Please Enter Your Contact");
            return false;
        }
        if (TextUtils.isEmpty(performerDescription.getText().toString())) {
            performerDescription.setError("Describe Yourself First");
            return false;
        }
        if (TextUtils.isEmpty(performerImageName.getText().toString())) {
            performerImageName.setError("");
            Snackbar.make(parent, "Please upload a Profile Picture", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(performerFacebook.getText().toString())) {
            performerFacebook.setError("Provide a valid Facebook Profile");
            return false;
        }
        if (TextUtils.isEmpty(performerInstagram.getText().toString())) {
            performerInstagram.setError("Provide a valid Instagram Profile");
            return false;
        }
        if (TextUtils.isEmpty(performerPrice.getText().toString())) {
            performerPrice.setError("Please Input your hourly price");
        }

        return true;
    }

    private void signHotelUp() {
        if (checkLegitimacyHotel()) {
            //Legitimate data has been entered, sign it up
            hotelData.put("name", hotelName.getText().toString());
            hotelData.put("phone_number", hotelPhonePrefix.getText().toString()+hotelPhoneNumber.getText().toString());
            hotelData.put("description", hotelDescription.getText().toString());
            hotelData.put("location", "");
            hotelData.put("token", "");
            hotelData.put("prev_performances", "");
            hotelData.put("pic_name", picName);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(hotelPhonePrefix.getText().toString()+hotelPhoneNumber.getText().toString(),
                                                                60,
                                                                TimeUnit.SECONDS,
                                                                this,
                                                                mCallbacks);
        }
    }

    private boolean checkLegitimacyHotel() {
        if (TextUtils.isEmpty(hotelName.getText().toString())) {
            hotelName.setError("Please Enter A Name");
            return false;
        }
        if (TextUtils.isEmpty(hotelPhoneNumber.getText().toString())) {
            hotelPhoneNumber.setError("Please Input An Official Contact");
            return false;
        }
        if (TextUtils.isEmpty(hotelDescription.getText().toString())) {
            hotelDescription.setError("Please Describe Yourself");
            return false;
        }
        if (TextUtils.isEmpty(hotelImageName.getText().toString())) {
            hotelImageName.setError("");
            Snackbar.make(parent, "Click on the icon upload A Profile Picture" ,Snackbar.LENGTH_LONG).show();
        }
        return true;
    }

    private void doSwipeAnimation() {
        //TODO: Change the nature of this method to perform a swipe operation
        initialContainer.setVisibility(View.GONE);
        if (INITIAL_SELECTION == HOTEL) {
            hotelContainer.setVisibility(View.VISIBLE);
            Toast.makeText(this, hotelCity.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        }
        else if (INITIAL_SELECTION == PERFORMER) {
            performerContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void sendOTP(String otp) {
        mOTP = otp;
        mCredential = PhoneAuthProvider.getCredential(mVerificationId, mOTP);
        if (INITIAL_SELECTION == HOTEL)
            SignInManager.signInWithNewCredentials(mCredential, context, hotelData, selectedImage, hotelCity.getSelectedItem().toString());
        else if (INITIAL_SELECTION == PERFORMER)
            SignInManager.signInWithNewCredentials(mCredential, context, performerData, selectedImage, performerCity.getSelectedItem().toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                performerGenre.setAdapter(mucisiansAdapter);
                break;
            case 1:
                performerGenre.setAdapter(comediansAdapter);
                break;
            case 2:
                performerGenre.setAdapter(othersAdapter);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
