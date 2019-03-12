package in.stazy.stazy.hotelend;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;

public class ShortlistHiresViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private CircleImageView profilePicture;
    private TextView nameTextView;
    private ShortlistsHireCommunication communication;

    public ShortlistHiresViewHolder(View itemView) {
        super(itemView);
        profilePicture = itemView.findViewById(R.id.shortlist_hire_image_view);
        nameTextView = itemView.findViewById(R.id.shortlist_hire_name_view);
        itemView.setOnClickListener(this);
    }

    public CircleImageView getProfilePicture() {
        return profilePicture;
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    @Override
    public void onClick(View v) {
        //Open Performer activity
        communication.openPerformer(getAdapterPosition());
    }

    public void attachOpenPerformerListener(ShortlistsHireCommunication listener) {
        communication = listener;
    }

    interface ShortlistsHireCommunication {
        void openPerformer(int positionClicked);
    }
}
