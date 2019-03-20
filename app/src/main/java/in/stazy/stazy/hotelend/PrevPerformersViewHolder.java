package in.stazy.stazy.hotelend;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagerhotel.PrevPerformers;

public class PrevPerformersViewHolder {
    private CircleImageView profilePhoto;
    private TextView name, datePerformed, ratingReceived;
    private Context context;

    public PrevPerformersViewHolder(View itemView, Context context) {
        profilePhoto = itemView.findViewById(R.id.prev_perform_profile_photo);
        name = itemView.findViewById(R.id.prev_perform_name);
        datePerformed = itemView.findViewById(R.id.prev_perform_date_performed);
        ratingReceived = itemView.findViewById(R.id.prev_perform_rating);
        this.context = context;
    }

    public void setContents(PrevPerformers performer) {
        name.setText(performer.getName());
        datePerformed.setText("Date: "+performer.getDatePerformed());
        ratingReceived.setText(String.valueOf(performer.getRatingReceived()));
        Glide.with(context).clear(profilePhoto);

        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(performer.getUID()+"/"+performer.getPicName());

        Glide.with(context).clear(profilePhoto);
        Glide.with(context).asBitmap().load(imageReference).into(profilePhoto);
    }
}
