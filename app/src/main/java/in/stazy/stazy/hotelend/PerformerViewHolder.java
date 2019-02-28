package in.stazy.stazy.hotelend;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import in.stazy.stazy.R;

class PerformerViewHolder {
    private ImageView profilePicture;
    private TextView nameTextView, extrasTextView, overallRatingTextView;
    private Button priceTag;
    private ImageButton shortlistCandidate;

    PerformerViewHolder(View view) {
        profilePicture = view.findViewById(R.id.vap_display_picture);
        nameTextView = view.findViewById(R.id.vap_performer_name_text_view);
        extrasTextView = view.findViewById(R.id.vap_performer_extras_text_view);
        overallRatingTextView = view.findViewById(R.id.vap_rating_text_view);
        priceTag = view.findViewById(R.id.vap_price_button);
    }

    void setProfilePicture(String uid) {
        //TODO: Use uid to parse profile picture from Cloud Storage
        //After 3rd March
    }

    void setTexts(String name, String lastRating, String overallRating, String price) {
        nameTextView.setText(name);
        extrasTextView.setText("Last Rating: " + lastRating);
        overallRatingTextView.setText(overallRating);
        priceTag.setText(price);
    }
}
