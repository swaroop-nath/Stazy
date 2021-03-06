package in.stazy.stazy.performerend;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagerperformer.HotelDataPerformerSide;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class HotelViewHolder {
    private CircleImageView displayPicture;
    private TextView hotelName;
    private TextView performanceDate;
    private TextView ratingText;
    private CardView ratingCard;
    private ImageView presence;
    private Context context;

    public HotelViewHolder(View view, Context context) {
        displayPicture = view.findViewById(R.id.amp_list_item_hotel_profile_picture);
        hotelName = view.findViewById(R.id.amp_list_item_hotel_name_text_view);
        performanceDate = view.findViewById(R.id.amp_list_item_hotel_perf_date_text_view);
        ratingText = view.findViewById(R.id.amp_list_item_hotel_rating_received);
        ratingCard = view.findViewById(R.id.amp_list_item_hotel_rating_card);
        presence = view.findViewById(R.id.amp_list_item_current_hotel);
        this.context = context;
    }

    public void setContents(HotelDataPerformerSide prevPerformance, final int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageReference = storage.getReference().child(prevPerformance.getUID()+"/"+prevPerformance.getPicName());
        if (prevPerformance.getRating().equals("-1")) {
            ratingCard.setVisibility(View.GONE);
            presence.setVisibility(View.VISIBLE);
        } else {
            ratingCard.setVisibility(View.VISIBLE);
            presence.setVisibility(View.GONE);
        }

        hotelName.setText(prevPerformance.getName());
        performanceDate.setText("Performance Date: "+prevPerformance.getDate());
        ratingText.setText(prevPerformance.getRating());

        if (prevPerformance.getProfilePictureHigh() == null) {
            Glide.with(context).clear(displayPicture);
            Glide.with(context).asBitmap().load(imageReference).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    PerformerManager.PREV_HOTELS.get(position).setProfilePictureHigh(resource);
                    displayPicture.setImageBitmap(resource);
                }
            });
        } else
            displayPicture.setImageBitmap(prevPerformance.getProfilePictureHigh());
    }
}
