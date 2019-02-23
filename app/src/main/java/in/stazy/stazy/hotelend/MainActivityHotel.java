package in.stazy.stazy.hotelend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanager.MucisianData;

public class MainActivityHotel extends AppCompatActivity {
    //View references
    //Explore Section Card Views
    @BindView(R.id.activity_hotel_main_explore_card_view_musicians) CardView exploreMucisians;
    @BindView(R.id.activity_hotel_main_explore_card_view_comedians) CardView exploreComedians;
    @BindView(R.id.activity_hotel_main_explore_card_view_others) CardView exploreOthers;
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
    public static final String EXPLORE_INTENT_VALUE_MUCISIANS = "mucisians";
    public static final String EXPLORE_INTENT_VALUE_COMEDIANS = "comedians";
    public static final String EXPLORE_INTENT_VALUE_OTHERS = "others";
    public static final String VIEW_ALL_INTENT_EXTRA_KEY = "view_all_type";
    public static final String VIEW_ALL_INTENT_VALUE_MUCISIANS = "view_all_mucisians";
    public static final String VIEW_ALL_INTENT_VALUE_COMEDIANS = "view_all_comedians";
    public static final String VIEW_ALL_INTENT_VALUE_OTHERS = "view_all_others";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hotel);

        context = getApplicationContext();

        //Setting Intents for Explore children
        exploreIntent = new Intent(context, HirePerformer.class);

        //Setting Intent for view all buttons in prev performances
        viewAllIntent = new Intent(context, ViewAllPerformers.class);

        //Making the recycler views horizontal
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context);
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        prevMucisians.setLayoutManager(horizontalLayoutManager);
        prevComedians.setLayoutManager(horizontalLayoutManager);
        prevOthers.setLayoutManager(horizontalLayoutManager);

        /*
        Download data using the DataManager class' method in the authflow classes and keep it in a
        relevant array (MucisianData or ComedianData). Then retrieve the data here and use it in Recycler View Adapter
         */

        ArrayList<MucisianData> mucisians = new ArrayList<>(1); // A ruse mucisian that shall be retrieved from firestore and used for debugging
        mucisians.get(0).setName("Swaroop");
        mucisians.get(0).setProfilePictureHigh(BitmapFactory.decodeResource(getResources(), R.mipmap.performer_display_picture_placeholder));
        PrevPerformersAdapter<MucisianData> mucisianAdapter = new PrevPerformersAdapter<>(mucisians, context);
        prevMucisians.setAdapter(mucisianAdapter);

    }

    /**
     * Following three callbacks refer to the three card-views in explore section.
     * @param view
     * Set intent extra in these methods and start activity.
     */
    public void hireMucisiansIntent(View view) {
        exploreIntent.putExtra(EXPLORE_INTENT_EXTRA_KEY, EXPLORE_INTENT_VALUE_MUCISIANS);
        startActivity(exploreIntent);
    }
    public void hireComediansIntent(View view) {
        exploreIntent.putExtra(EXPLORE_INTENT_EXTRA_KEY, EXPLORE_INTENT_VALUE_COMEDIANS);
        startActivity(exploreIntent);
    }
    public void hireOthersIntent(View view) {
        exploreIntent.putExtra(EXPLORE_INTENT_EXTRA_KEY, EXPLORE_INTENT_VALUE_OTHERS);
        startActivity(exploreIntent);
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
}
