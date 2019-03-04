package in.stazy.stazy.performerend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;

public class Hotel extends AppCompatActivity {

    //View References
    @BindView(R.id.activity_hotel_display_picture) ImageView profilePicture;
    @BindView(R.id.activity_hotel_hotel_name_text_view) TextView hotelNameTextView;
    @BindView(R.id.activity_hotel_hotel_location_text_view) TextView hotelCityTextView;
    @BindView(R.id.activity_hotel_hotel_description) TextView hotelDescriptionTextView;

    //Activity Specific References
    private HotelData receivedHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        Intent intent = getIntent();
        receivedHotel = (HotelData) intent.getSerializableExtra(MainActivityPerformer.INTENT_HOTEL_OBJECT_KEY);
        setContentsOfViews();

    }

    private void setContentsOfViews() {
        //TODO: Write Code to download Image and set it to the profilePicture

        hotelNameTextView.setText(receivedHotel.getName());
        hotelCityTextView.setText(receivedHotel.getCity());
        hotelDescriptionTextView.setText(receivedHotel.getDescription());
    }
}
