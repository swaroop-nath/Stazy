package in.stazy.stazy.authflow;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    //View References
    @BindView(R.id.activity_sign_in_parent) ConstraintLayout parent;
    @BindView(R.id.sign_in_activity_welcome_text_view) TextView introductionTextView;
    @BindView(R.id.activity_sign_in_phone_prefix) TextInputEditText phoneNumberPrefix;
    @BindView(R.id.sign_in_activity_phone_number) TextInputEditText phoneNumberInput;
    @BindView(R.id.sign_in_activity_password) TextInputEditText otpInput;
    @BindView(R.id.sign_in_activity_get_otp_button) CardView getOTPButton;
    @BindView(R.id.sign_in_activity_log_in_button) CardView logInButton;

    //Activity Specific References
    private Context context;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        context = getApplicationContext();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //TODO: Show some dialogs
                SignInManager.signInWithOldCredentials(phoneAuthCredential, context);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e("Callback Sign In", "onVerificationFailed called");
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(parent, "Please Enter a Valid Phone Number", Snackbar.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(parent, "Server Error, Try Again later", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = verificationId;
                getOTPButton.setVisibility(View.GONE);
                logInButton.setVisibility(View.VISIBLE);
                logInButton.setOnClickListener(SignInActivity.this);
            }
        };

        getOTPButton.setOnClickListener(this);
    }


    public void startSignUp(View view) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_activity_log_in_button:
                if (!TextUtils.isEmpty(otpInput.getText().toString())) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, otpInput.getText().toString());
                    SignInManager.signInWithOldCredentials(phoneAuthCredential, context);
                } else
                    otpInput.setError("Please Input the sent OTP");
                break;
            case R.id.sign_in_activity_get_otp_button:
                if (!TextUtils.isEmpty(phoneNumberInput.getText().toString())) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumberPrefix.getText().toString()+phoneNumberInput.getText().toString(), 60, TimeUnit.SECONDS, this, mCallbacks);
                } else
                    phoneNumberInput.setError("Please input a Valid Phone Number");
                break;
        }
    }
}
