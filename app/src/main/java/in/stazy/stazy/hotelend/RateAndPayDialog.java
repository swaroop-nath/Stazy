package in.stazy.stazy.hotelend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import in.stazy.stazy.R;

public class RateAndPayDialog extends DialogFragment implements DialogInterface.OnShowListener, View.OnClickListener {
    private String payment;
    private double rating, price;
    private AlertDialog dialog;
    private Context context;
    private AppCompatRatingBar hirerRating, customerOneRating, customerTwoRating;
    private TextInputEditText customerOne, customerTwo;
    private RateCommunication communication = null;
    private TextView paymentBody;
    private String name;

    public void setData(String payment, double rating, String price, String name) {
        this.payment = payment;
        this.rating = rating;
        this.price = Double.valueOf(price);
        this.name = name;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.rate_and_pay_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);

        hirerRating = layout.findViewById(R.id.hirer_rating);
        customerOneRating = layout.findViewById(R.id.customer_one_rating);
        customerTwoRating = layout.findViewById(R.id.customer_two_rating);
        customerOne = layout.findViewById(R.id.customer_rating_email_input_one);
        customerTwo = layout.findViewById(R.id.customer_rating_email_input_two);
        paymentBody = layout.findViewById(R.id.payment_body_text);

//        paymentBody.setText("Please pay " + this.name + " an amount of: Rs." + this.price);
        paymentBody.setText("Please pay " + this.name + " the stipulated amount.");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RateAndPayDialog.this.getDialog().cancel();
            }
        });

        builder.setPositiveButton("Rate", null);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(this);
        return dialog;
    }

    public void attachRateListener(RateCommunication listener) {
        communication = listener;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Button rate = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        rate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(customerOne.getText().toString())) {
            customerOne.setError("Please Input an Email Address");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(customerOne.getText().toString()).matches()) {
            customerOne.setError("Please Input a valid Email Address");
            return;
        }
        if (TextUtils.isEmpty(customerTwo.getText().toString())) {
            customerOne.setError("Please Input an Email Address");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(customerTwo.getText().toString()).matches()) {
            customerOne.setError("Please Input a valid Email Address");
            return;
        }

        String emailOne = customerOne.getText().toString();
        String emailTwo = customerTwo.getText().toString();

        double ratingGiven = (hirerRating.getRating()*10 + customerOneRating.getRating() + customerTwoRating.getRating())/12;

        DecimalFormat df = new DecimalFormat("#.##");
        this.rating = Double.valueOf(df.format(this.rating + 0.07 * (ratingGiven - this.rating)));
        this.price = Double.valueOf(df.format(this.price + 0.07 * (ratingGiven - this.rating) * this.price / (5 - this.rating)));
        ratingGiven = Double.valueOf(df.format(ratingGiven));

        RateAndPayDialog.this.dialog.dismiss();
        communication.onRatingSet(this.rating, ratingGiven, this.price, emailOne, emailTwo);
    }

    public interface RateCommunication {
        void onRatingSet(double rating, double ratingReceived, double price, String emailOne, String emailTwo);
    }
}
