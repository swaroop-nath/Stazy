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
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import in.stazy.stazy.R;

public class SortDialog extends DialogFragment implements DialogInterface.OnShowListener, View.OnClickListener {
    private Context context;
    private AlertDialog dialog;
    private SortCommunication communication;
    private RadioGroup radioGroup;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.sort_options, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SortDialog.this.getDialog().cancel();
            }
        });
        builder.setPositiveButton("Apply", null);
        radioGroup = layout.findViewById(R.id.sorting_radio_group);

        dialog = builder.create();

        dialog.setOnShowListener(this);
        return dialog;
    }

    void attachSortSetListener(SortCommunication listener) {
        communication = listener;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Button apply = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        apply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = radioGroup.getCheckedRadioButtonId();
        if (id == -1)
            Toast.makeText(context, "Please check any sort type", Toast.LENGTH_SHORT).show();
        else {
            dialog.dismiss();
            communication.onSortSet(id);
        }
    }

    interface SortCommunication {
        void onSortSet(int id);
    }
}
