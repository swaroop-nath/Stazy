package in.stazy.stazy.hotelend;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import in.stazy.stazy.R;
import in.stazy.stazy.datamanagerhotel.DataManager;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

class PerformerViewHolder {
    private ImageView profilePicture;
    private TextView nameTextView, extrasTextView, overallRatingTextView;
    private Button priceTag;
    private Context context;

    PerformerViewHolder(View view, Context context) {
        profilePicture = view.findViewById(R.id.vap_display_picture);
        nameTextView = view.findViewById(R.id.vap_performer_name_text_view);
        extrasTextView = view.findViewById(R.id.vap_performer_extras_text_view);
        overallRatingTextView = view.findViewById(R.id.vap_rating_text_view);
        priceTag = view.findViewById(R.id.vap_price_button);
        this.context = context;
    }

    void setProfilePicture(DataManager performer) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageReference = storage.getReference().child(performer.getUID()+"/"+performer.getPicName());
        Glide.with(context).asBitmap().load(imageReference).into(profilePicture);
    }

    void setContents(DataManager performer) {
        nameTextView.setText(performer.getName());
        extrasTextView.setText("# Performances: " + performer.getNumPerformances());
        overallRatingTextView.setText(performer.getRating());
        priceTag.setText(performer.getPrice() + "/hr");
        Glide.with(context).clear(profilePicture);
        setProfilePicture(performer);
    }
}
