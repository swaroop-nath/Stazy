package in.stazy.stazy.authflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import in.stazy.stazy.R;

public class WaitFragment extends DialogFragment {
    private Context context;
    private String toBeShown;

    public void setData(String s) {
        toBeShown = s;
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
        View layout = inflater.inflate(R.layout.wait_fragment, null);
        TextView toBeShownInfo = layout.findViewById(R.id.wait_fragment_text);
        toBeShownInfo.setText(toBeShown);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
