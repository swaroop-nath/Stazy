package in.stazy.stazy.performerend;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import in.stazy.stazy.R;

public class DialogMenu implements View.OnClickListener {
    private Dialog dialog;
    private PerformerDialogListener listener;

    public DialogMenu(Context context) {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_menu_performer);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        TextView account = dialog.findViewById(R.id.activity_main_performer_menu_item_profile);
        TextView signOut = dialog.findViewById(R.id.activity_main_performer_menu_item_sign_out);

        account.setOnClickListener(this);
        signOut.setOnClickListener(this);
    }

    public void attachPerformerDialogListener (PerformerDialogListener listener) {
        this.listener = listener;
    }

    public Dialog getDialog() {
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_performer_menu_item_profile:
                listener.openProfile();
                break;
            case R.id.activity_main_performer_menu_item_sign_out:
                listener.signOut();
                break;
        }
    }

    interface PerformerDialogListener {
        void openProfile();
        void signOut();
    }
}
