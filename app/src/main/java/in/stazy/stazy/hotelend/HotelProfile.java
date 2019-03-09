package in.stazy.stazy.hotelend;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagercrossend.Manager;

public class HotelProfile extends AppCompatActivity implements OnCompleteListener<DocumentSnapshot> {

    //View References
    @BindView(R.id.activity_hotel_profile_display_picture) ImageView profilePicture;
    @BindView(R.id.activity_hotel_profile_hotel_name_text_view) TextView hotelName;
    @BindView(R.id.activity_hotel_profile_hotel_location_text_view) TextView hotelCity;
    @BindView(R.id.activity_hotel_profile_hotel_description) TextView hotelDescription;

    //Activity Specific References

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_profile);
        ButterKnife.bind(this);
        if (Manager.HOTEL_DATA == null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                    .collection("hotels").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(this);
        } else
            setContentsOfViews();


    }

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.getResult() != null) {
            Manager.HOTEL_DATA = HotelData.setData(task.getResult());
            setContentsOfViews();
        }
        else
            Log.e("Hotel Profile", task.getException().getMessage());
    }

    private void setContentsOfViews() {
        profilePicture.setImageBitmap(Manager.HOTEL_DATA.getProfilePictureHigh());
        hotelName.setText(Manager.HOTEL_DATA.getName());
        hotelCity.setText(Manager.HOTEL_DATA.getCity());
        hotelDescription.setText("        Description: " + Manager.HOTEL_DATA.getDescription());
    }
}
