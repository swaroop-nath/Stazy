package in.stazy.stazy.hotelend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.stazy.stazy.R;

public class PrevPerformersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    //View references
    private ImageView displayImage;
    private TextView profileName;
    private PrevPerformersCommunication communication;

    PrevPerformersViewHolder(View itemView) {
        super(itemView);
        displayImage = itemView.findViewById(R.id.ahmpp_recycler_item_image_view);
        profileName = itemView.findViewById(R.id.ahmpp_recycler_item_text_view);
        itemView.setOnClickListener(this);
        
    }

    public ImageView getDisplayImage() {
        return displayImage;
    }

    public TextView getProfileName() {
        return profileName;
    }

    @Override
    public void onClick(View v) {
        //Open Performer activity
        communication.openPerformer(getAdapterPosition());
    }

    public void attachOpenPerformerListener(PrevPerformersCommunication listener) {
        communication = listener;
    }

    interface PrevPerformersCommunication {
        void openPerformer(int positionClicked);
    }
}
