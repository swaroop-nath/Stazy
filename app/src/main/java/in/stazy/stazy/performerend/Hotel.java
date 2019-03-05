package in.stazy.stazy.performerend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class Hotel extends AppCompatActivity {

    //View References
    @BindView(R.id.activity_hotel_display_picture) ImageView profilePicture;
    @BindView(R.id.activity_hotel_hotel_name_text_view) TextView hotelNameTextView;
    @BindView(R.id.activity_hotel_hotel_location_text_view) TextView hotelCityTextView;
    @BindView(R.id.activity_hotel_hotel_description) TextView hotelDescriptionTextView;

    //Activity Specific References
    private int receivedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        receivedPosition = intent.getIntExtra(MainActivityPerformer.INTENT_HOTEL_OBJECT_KEY, 0);
        setContentsOfViews();

    }

    private void setContentsOfViews() {
        profilePicture.setImageBitmap(PerformerManager.PREV_HOTELS.get(receivedPosition).getProfilePictureHigh());
        hotelNameTextView.setText(PerformerManager.PREV_HOTELS.get(receivedPosition).getName());
        hotelCityTextView.setText(PerformerManager.PREV_HOTELS.get(receivedPosition).getCity());
        hotelDescriptionTextView.setText(PerformerManager.PREV_HOTELS.get(receivedPosition).getDescription());
    }
}
