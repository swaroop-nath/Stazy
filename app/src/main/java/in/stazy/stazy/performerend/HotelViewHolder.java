package in.stazy.stazy.performerend;

import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;

public class HotelViewHolder {
    private CircleImageView displayPicture;
    private TextView hotelName;

    public HotelViewHolder(View view) {
        displayPicture = view.findViewById(R.id.amp_list_item_hotel_profile_picture);
        hotelName = view.findViewById(R.id.amp_list_item_hotel_name_text_view);
    }

    public void setContents(HotelData prevPerformance) {
        //TODO: Download image, set it to HotelData object
        hotelName.setText(prevPerformance.getName());
    }
}
