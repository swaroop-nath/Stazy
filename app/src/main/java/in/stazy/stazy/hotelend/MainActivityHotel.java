package in.stazy.stazy.hotelend;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagerhotel.ComedianData;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.MucisianData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class MainActivityHotel extends AppCompatActivity {
    //View references
    //Explore Section ViewPager
    @BindView(R.id.activity_hotel_main_explore_view_pager) ViewPager exploreViewPager;
    //Prev Performances Section Recycler Views
    @BindView(R.id.activity_hotel_main_prev_performances_child_mucisians_recycler_view) RecyclerView prevMucisians;
    @BindView(R.id.activity_hotel_main_prev_performances_child_comedians_recycler_view) RecyclerView prevComedians;
    @BindView(R.id.activity_hotel_main_prev_performances_child_others_recycler_view) RecyclerView prevOthers;
    //Prev Performances Section "View all" Buttons
    @BindView(R.id.activity_hotel_main_prev_performances_child_view_header_musicians_view_all_button) Button mucisiansViewAllButton;
    @BindView(R.id.activity_hotel_main_prev_performances_child_view_header_comedians_view_all_button) Button comediansViewAllButton;
    @BindView(R.id.activity_hotel_main_prev_performances_child_view_header_others_view_all_button) Button othersViewAllButton;

    //Activity specific declarations
    private Context context;
    private Intent exploreIntent, viewAllIntent;

    //Constant Field declarations
    public static final String EXPLORE_INTENT_EXTRA_KEY = "type";
    public static final String TYPE_VALUE_MUCISIANS = "musicians";
    public static final String TYPE_VALUE_COMEDIANS = "comedians";
    public static final String TYPE_VALUE_OTHERS = "others";
    public static final String VIEW_ALL_INTENT_EXTRA_KEY = "view_all_type";
    public static final String VIEW_ALL_INTENT_VALUE_MUCISIANS = "view_all_mucisians";
    public static final String VIEW_ALL_INTENT_VALUE_COMEDIANS = "view_all_comedians";
    public static final String VIEW_ALL_INTENT_VALUE_OTHERS = "view_all_others";
    public static final String INDIVIDUAL_PERFORMER_OBJECT_KEY = "performer_object";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hotel);
        ButterKnife.bind(this);

        ArrayList<ExploreCardModel> models = new ArrayList<>();
        models.add(new ExploreCardModel(R.drawable.musicians));
        models.add(new ExploreCardModel(R.drawable.comedians));
        models.add(new ExploreCardModel(R.drawable.others));

        Adapter adapter = new Adapter(models, getApplicationContext());

        exploreViewPager.setAdapter(adapter);

        if (Manager.NEW_TOKEN_RECEIVED == 1)
            sendTokenToServer(Manager.FCM_TOKEN);

        context = getApplicationContext();
        FirebaseApp.initializeApp(context);

        //Setting Intents for Explore children
        exploreIntent = new Intent(context, HirePerformer.class);

        //Setting Intent for view all buttons in prev performances
        viewAllIntent = new Intent(context, ViewAllPerformers.class);

        //Making the recycler views horizontal
        LinearLayoutManager horizontalLayoutManagerMucisians = new LinearLayoutManager(context);
        LinearLayoutManager horizontalLayoutManagerComedians = new LinearLayoutManager(context);
        LinearLayoutManager horizontalLayoutManagerOthers = new LinearLayoutManager(context);
        horizontalLayoutManagerMucisians.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalLayoutManagerComedians.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalLayoutManagerOthers.setOrientation(LinearLayoutManager.HORIZONTAL);
        prevMucisians.setLayoutManager(horizontalLayoutManagerMucisians);
        prevComedians.setLayoutManager(horizontalLayoutManagerComedians);
        prevOthers.setLayoutManager(horizontalLayoutManagerOthers);

        /*
        TODO: Change the data structure of HotelData to incorporate previous performers
         */

        if (Manager.HOTEL_DATA == null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                                                    .collection("hotels").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //Data Downloaded Successfully
                        Manager.HOTEL_DATA = HotelData.setData(task.getResult());
                    } else {
                        Log.e("HOTEL_DATA_DOWNLOAD", task.getException().getMessage());
                        Toast.makeText(context, "Can't Download Data Right Now, Try Again Later!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        ArrayList<MucisianData> mucisians = Manager.PREVIOUS_MUCISIANS; // A ruse mucisian that shall be retrieved from firestore and used for debugging
        PrevPerformersAdapter<MucisianData> mucisianAdapter = new PrevPerformersAdapter<>(mucisians, context, TYPE_VALUE_MUCISIANS);
        prevMucisians.setAdapter(mucisianAdapter);

        ArrayList<ComedianData> comedians = Manager.PREVIOUS_COMEDIANS;
        PrevPerformersAdapter<ComedianData> comedianAdapter = new PrevPerformersAdapter<>(comedians, context, TYPE_VALUE_COMEDIANS);
        prevComedians.setAdapter(comedianAdapter);

        ArrayList<ComedianData> others = Manager.PREVIOUS_COMEDIANS;
        PrevPerformersAdapter<ComedianData> otherAdapter = new PrevPerformersAdapter<>(others, context, TYPE_VALUE_OTHERS);
        prevOthers.setAdapter(otherAdapter);

    }

    /**
     * Following three callbacks refer to the three "View All" buttons in prev performances section.
     * @param view
     * Set intent extra in these methods and start activity.
     */
    public void viewAllMucisians(View view) {
        viewAllIntent.putExtra(VIEW_ALL_INTENT_EXTRA_KEY, VIEW_ALL_INTENT_VALUE_MUCISIANS);
        startActivity(viewAllIntent);
    }
    public void viewAllComedians(View view) {
        viewAllIntent.putExtra(VIEW_ALL_INTENT_EXTRA_KEY, VIEW_ALL_INTENT_VALUE_COMEDIANS);
        startActivity(viewAllIntent);
    }
    public void viewAllOthers(View view) {
        viewAllIntent.putExtra(VIEW_ALL_INTENT_EXTRA_KEY, VIEW_ALL_INTENT_VALUE_OTHERS);
        startActivity(viewAllIntent);
    }

    private void sendTokenToServer(String fcmToken) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", fcmToken);
        //Hotel Logged In
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firestore.collection("COLLECTION").document(Manager.CITY_VALUE)
                    .collection("hotels").document(FirebaseAuth.getInstance().getUid());
        reference.update(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.e("TOKEN ADDITION","TOKEN added to server");
                else
                    Log.e("TOKEN ADDITION", task.getException().getMessage());
            }
        });
    }
}
