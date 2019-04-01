package in.stazy.stazy.performerend;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;

public class ApproachViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private CircleImageView profilePicture;
    private TextView hotelName;
    private ItemClickListener clickListener;

    public ApproachViewHolder(View itemView) {
        super(itemView);
        profilePicture = itemView.findViewById(R.id.approaches_profile_picture);
        hotelName = itemView.findViewById(R.id.approaches_name);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clickListener.onItemClick(getAdapterPosition());
    }

    public CircleImageView getProfilePicture() {
        return profilePicture;
    }

    public TextView getHotelName() {
        return hotelName;
    }

    void attachClickListener(ItemClickListener listener) {
        this.clickListener = listener;
    }

    interface ItemClickListener {
        void onItemClick(int position);
    }
}
