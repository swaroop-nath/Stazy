package in.stazy.stazy.authflow;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, SignInManager.SignInCommunication, PinField.OnTextCompleteListener {

    //View References
    @BindView(R.id.activity_sign_in_parent) ConstraintLayout parent;
    @BindView(R.id.sign_in_activity_welcome_text_view) TextView introductionTextView;
    @BindView(R.id.activity_sign_in_phone_prefix) TextInputEditText phoneNumberPrefix;
    @BindView(R.id.sign_in_activity_phone_number) TextInputEditText phoneNumberInput;
    @BindView(R.id.sign_in_activity_password) CirclePinField otpInput;
    @BindView(R.id.sign_in_activity_get_otp_button) Button getOTPButton;
    @BindView(R.id.sign_in_activity_log_in_button) Button logInButton;
    @BindView(R.id.sign_in_activity_sign_up_button) Button signUpButton;
    @BindView(R.id.sign_in_activity_cancel_password) Button cancelSingIn;
    @BindView(R.id.activity_sign_in_phone_input_container) ConstraintLayout phoneInputLayout;
    @BindView(R.id.activity_sign_in_otp_input_container) ConstraintLayout otpInputLayout;
    @BindView(R.id.phone_selected) ImageView phoneSelected;
    @BindView(R.id.otp_selected) ImageView otpSelected;

    //Activity Specific References
    private Context context;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private WaitFragment otpFragment = null;
    static WaitFragment verificationFragment = null;
    static WaitFragment signInFragment = null;
    private boolean isOTPComplete = false;
    private String enteredOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        context = getApplicationContext();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                if (otpFragment != null) {
                    otpFragment.dismiss();
                    otpFragment = null;
                }
                if (signInFragment != null) {
                    signInFragment.dismiss();
                    signInFragment = null;
                }
                movePhoneInputOut();
                moveOTPInputIn();
                otpInput.setText(phoneAuthCredential.getSmsCode());
                verificationFragment = showWaitFragment("Conducting Automatic Verification . . .", "automatic_verification");
                SignInManager.signInWithOldCredentials(phoneAuthCredential, SignInActivity.this);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (otpFragment != null) {
                    otpFragment.dismiss();
                    otpFragment = null;
                }
                if (signInFragment != null) {
                    signInFragment.dismiss();
                    signInFragment = null;
                }
                Log.e("Callback Sign In", "onVerificationFailed called");
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(context, "Server Error, Try Again later", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                if (otpFragment != null) {
                    otpFragment.dismiss();
                    otpFragment = null;
                }
                mVerificationId = verificationId;
                movePhoneInputOut();
                moveOTPInputIn();
                phoneSelected.setImageDrawable(getDrawable(R.drawable.phone_otp_unselected));
                otpSelected.setImageDrawable(getDrawable(R.drawable.phone_otp_selected));
            }
        };

        getOTPButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        logInButton.setOnClickListener(SignInActivity.this);
        cancelSingIn.setOnClickListener(this);
        otpInput.setOnTextCompleteListener(this);
    }


    public void startSignUp(View view) {
    }

    private WaitFragment showWaitFragment(String s, String tag) {
        WaitFragment waitFragment = new WaitFragment();
        waitFragment.setData(s);
        waitFragment.show(getSupportFragmentManager(), tag);
        return waitFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_activity_log_in_button:
                if (!TextUtils.isEmpty(otpInput.getText().toString())) {
                    if (isOTPComplete) {
                        signInFragment = showWaitFragment("Signing In . . .", "sign_in_fragment");
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, enteredOTP);
                        SignInManager.signInWithOldCredentials(phoneAuthCredential, SignInActivity.this);
                    } else {
                        otpInput.setError("Please Enter a valid OTP!");
                    }
                } else
                    otpInput.setError("Please Input the sent OTP");
                break;
            case R.id.sign_in_activity_get_otp_button:
                if (!TextUtils.isEmpty(phoneNumberInput.getText().toString())) {
                    otpFragment = showWaitFragment("Waiting For OTP . . .", "otp_fragment");
                    DocumentReference registeredUsers = FirebaseFirestore.getInstance().collection("Registered Users").document(phoneNumberPrefix.getText().toString()+phoneNumberInput.getText().toString());
                    registeredUsers.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumberPrefix.getText().toString()+phoneNumberInput.getText().toString(), 60, TimeUnit.SECONDS, SignInActivity.this, mCallbacks);
                                } else {
                                    if (otpFragment != null) {
                                        otpFragment.dismiss();
                                        otpFragment = null;
                                    }
                                    Toast.makeText(context, "User doesn't exist. Please create an account", Toast.LENGTH_LONG).show();
                                    signUpButton.requestFocus();
                                }
                            } else {
                                if (otpFragment != null) {
                                    otpFragment.dismiss();
                                    otpFragment = null;
                                }
                                Toast.makeText(context, "Can't Connect Now, Please Try Again Later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else
                    phoneNumberInput.setError("Please input a Valid Phone Number");
                break;
            case R.id.sign_in_activity_sign_up_button:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.sign_in_activity_cancel_password:
                movePhoneInputIn();
                moveOTPInputOut();
                phoneSelected.setImageDrawable(getDrawable(R.drawable.phone_otp_selected));
                otpSelected.setImageDrawable(getDrawable(R.drawable.phone_otp_unselected));
                break;
        }
    }

    private void movePhoneInputIn() {
        phoneInputLayout.setVisibility(View.VISIBLE);
    }

    private void moveOTPInputOut() {
        otpInputLayout.setVisibility(View.GONE);
    }

    private void movePhoneInputOut() {
        phoneInputLayout.setVisibility(View.GONE);
    }

    private void moveOTPInputIn() {
        otpInputLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void fireIntentActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public boolean onTextComplete(String enteredText) {
        enteredOTP = enteredText;
        isOTPComplete = true;
        return false;
    }
}
