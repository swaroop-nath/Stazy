package in.stazy.stazy.hotelend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import in.stazy.stazy.R;

public class PerformanceConditionsDialog extends DialogFragment implements TimePicker.OnTimeChangedListener, AdapterView.OnItemSelectedListener, DialogInterface.OnShowListener, View.OnClickListener {
    private AlertDialog dialog;
    private Context context;
    private ConditionsSetListener conditionsSetListener;
    private Spinner durationHours, durationMins;
    private String performanceTime;
    private String durationHoursSet, durationMinsSet;
    private TimePicker performanceTimePicker;
    private DatePicker perfomanceDatePicker;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.performance_conditions_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        builder.setPositiveButton("Confirm",null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PerformanceConditionsDialog.this.getDialog().cancel();
            }
        });

        performanceTimePicker = layout.findViewById(R.id.ap_conditions_dialog_performance_time_picker);
        durationHours = layout.findViewById(R.id.ap_conditions_dialog_duration_child_hours_spinner);
        durationMins = layout.findViewById(R.id.ap_conditions_dialog_duration_child_mins_spinner);
        perfomanceDatePicker = layout.findViewById(R.id.date_of_performance_setter);

        performanceTimePicker.setOnTimeChangedListener(this);
        durationHours.setOnItemSelectedListener(this);
        durationMins.setOnItemSelectedListener(this);

        dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }

    void addOnConditionsSetListener(ConditionsSetListener listener) {
        conditionsSetListener = listener;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        String AMPM = "AM";
        if (hourOfDay >= 12) {
            AMPM = "PM";
            hourOfDay = hourOfDay - 12;
        }
        performanceTime = hourOfDay + ":" + minute + " " + AMPM;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.ap_conditions_dialog_duration_child_hours_spinner:
                durationHoursSet = (String) durationHours.getSelectedItem();
                break;
            case R.id.ap_conditions_dialog_duration_child_mins_spinner:
                durationMinsSet = (String) durationMins.getSelectedItem();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Button confirm = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String durationTime = "";
        if (durationMins.getSelectedItem().toString().equals("00"))
            durationTime = durationHours.getSelectedItem().toString() + " hours.";
        else
            durationTime = durationHours.getSelectedItem().toString() + " hours " + durationMins.getSelectedItem().toString() + " minutes.";
        dialog.dismiss();
        if (performanceTime == null) {
            int hourOfDay = performanceTimePicker.getCurrentHour();
            int minute = performanceTimePicker.getCurrentMinute();
            String AMPM = "AM";
            if (hourOfDay >= 12) {
                AMPM = "PM";
                if (hourOfDay >= 13)
                    hourOfDay = hourOfDay - 12;
            }
            performanceTime = hourOfDay + ":" + minute + " " + AMPM;
        }
        Date performanceDate = getDateFromDatePicker(perfomanceDatePicker);
        double payment = (Integer.valueOf(durationHours.getSelectedItem().toString()) + Integer.valueOf(durationMins.getSelectedItem().toString())/60);
        conditionsSetListener.onConditionsSet(performanceTime, durationTime, payment, performanceDate);
    }

    interface ConditionsSetListener {
        void onConditionsSet(String performanceTime, String performanceDuration, double payment, Date performanceDate);
    }

    private Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }
}
