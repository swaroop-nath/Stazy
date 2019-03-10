package in.stazy.stazy.performerend;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.stazy.stazy.R;
import in.stazy.stazy.authflow.WaitFragment;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class DialogEarnPriority extends DialogFragment implements DialogInterface.OnShowListener, View.OnClickListener {
    private Context context;
    private AlertDialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_buy_priority, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);

        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogEarnPriority.this.getDialog().dismiss();
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Button OK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        OK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (PerformerManager.PERFORMER.getDoubleCredits() >= 1.5) {
            //Let him earn priority
            final WaitFragment waitFragment = new WaitFragment();
            waitFragment.setData("Please wait, upgrading your profile . . .");
            waitFragment.show(getFragmentManager(), "upgrade_priority");

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                    .collection("type").document(PerformerManager.TYPE_VALUE)
                    .collection(PerformerManager.GENRE_VALUE).document(FirebaseAuth.getInstance().getUid());
            Map<String, Object> priorityMap = new HashMap<>();
            priorityMap.put("priority", MainActivityPerformer.HIGH_PRIORITY);
            priorityMap.put("credits", PerformerManager.PERFORMER.getDoubleCredits() - 0.5);
            documentReference.update(priorityMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    waitFragment.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Priority Successfully Upgraded", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Couldn't complete operation now.\nPlease Try Again later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Toast.makeText(context, "You don't have enough credits, please buy some", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
