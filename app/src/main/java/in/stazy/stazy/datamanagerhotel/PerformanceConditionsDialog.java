package in.stazy.stazy.datamanagerhotel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import in.stazy.stazy.R;

public class PerformanceConditionsDialog extends DialogFragment {
    private AlertDialog dialog;
    private Context context;
    private ConditionsSetListener conditionsSetListener;

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
        return dialog;
    }

    void addOnConditionsSetListener(ConditionsSetListener listener) {
        conditionsSetListener = listener;
    }

    interface ConditionsSetListener {
        void onConditionsSet(String performanceTime, String performanceDuration);
    }
}
