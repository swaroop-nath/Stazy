package in.stazy.stazy.performerend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.Manager;

public class PaymentDialog extends DialogFragment implements DialogInterface.OnShowListener, View.OnClickListener {
    private Context context;
    private AlertDialog dialog;
    private String payeeAddress;
    private String currencyUnit = "INR";
    private String amount;
    private TextInputEditText creditsReqText;
    private String payeeName = "UMANG GUPTA";
    private String messagePayment = "PAYMENT FOR CREDITS"; //TODO: Tell performer to write this as message in Payment Message.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        payeeAddress = context.getResources().getString(Manager.PAYMENT_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.payment_dialog, null);
        creditsReqText = layout.findViewById(R.id.payment_dialog_input_credits);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);

        builder.setPositiveButton("Buy", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PaymentDialog.this.getDialog().cancel();
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Button buy = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //TODO: Write UPI code
        String creditsReq = String.valueOf(creditsReqText.getText());
        if (TextUtils.isEmpty(creditsReq))
            creditsReqText.setError("Please input a valid value");
        else {
            amount = String.valueOf(Integer.parseInt(creditsReq) * Manager.CREDIT_RATE);
            Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress+ "&pn="+ payeeName + "&am=" + amount + "&cu=" + currencyUnit);
            Log.e("DIALOG", "onClick: uri: " + uri);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            PaymentDialog.this.getDialog().dismiss();
        }
    }
}
