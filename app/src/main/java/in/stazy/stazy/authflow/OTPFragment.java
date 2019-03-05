package in.stazy.stazy.authflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import in.stazy.stazy.R;

public class OTPFragment extends DialogFragment implements DialogInterface.OnShowListener, View.OnClickListener {
    private Context context;
    private AlertDialog dialog;
    private OTPCommunication communication;
    private TextInputEditText otpInput;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.otp_fragment, null);
        otpInput = layout.findViewById(R.id.otp_fragment_input);


        AlertDialog.Builder builder = new AlertDialog.Builder(this.context).setView(layout);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OTPFragment.this.getDialog().cancel();
            }
        });
        builder.setPositiveButton("Verify",null);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    void attachCommunicationListener(OTPCommunication listener) {
        this.communication = listener;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Button verify = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        verify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.communication.sendOTP(otpInput.getText().toString());
        dialog.dismiss();
    }

    interface OTPCommunication {
        void sendOTP(String otp);
    }
}
