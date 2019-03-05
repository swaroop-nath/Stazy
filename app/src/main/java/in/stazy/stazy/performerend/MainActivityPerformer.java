package in.stazy.stazy.performerend;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerperformer.PerformerData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class MainActivityPerformer extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<QuerySnapshot>, AdapterView.OnItemClickListener {

    //View References
    @BindView(R.id.activity_main_performer_list_view) ListView hiresList;
    @BindView(R.id.activity_main_performer_hires_fab) FloatingActionButton menuFab;
    @BindView(R.id.activity_main_performer_menu_container) CardView menuContainer;
    @BindView(R.id.activity_main_performer_menu_item_terms_and_conditions) TextView termsConditions;
    @BindView(R.id.activity_main_performer_menu_item_help) TextView help;
    @BindView(R.id.activity_main_performer_menu_item_profile) TextView myAccount;
    @BindView(R.id.activity_main_performer_menu_item_sign_out) TextView signOut;
    
    //Activity Specific References
    private int menuState = 0; //0 - not displayed, 1  - displayed
    private Context context;
    private float locationOnShow, locationOnRemove;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private PerformerAdapter adapter;

    //Constant field declarations
    private static final long ANIMATION_DURATION = 500;
    private static int SCREEN_HEIGHT = 0;
    private static int MENU_CONTAINER_HEIGHT = 0;
    private static float MENU_CONTAINER_UNDER_CUT = 0f;
    public static final String INTENT_HOTEL_OBJECT_KEY = "hotel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_performer);
        ButterKnife.bind(this);
        context = getApplicationContext();

        menuFab.setOnClickListener(this);

        SCREEN_HEIGHT = getScreenHeight();
        locationOnRemove = SCREEN_HEIGHT;
        MENU_CONTAINER_UNDER_CUT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        Log.e("MAIN_ACTIVITY_PERFORMER", PerformerManager.PERFORMER + "");
        if (PerformerManager.PERFORMER == null) {
            DocumentReference mapperReference = firebaseFirestore.collection("Mapper").document(FirebaseAuth.getInstance().getUid());
            mapperReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.e("MAIN_ACTIVITY_PERFORMER", "Mapper Downloaded");

                    DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                                .collection("type").document(PerformerManager.TYPE_VALUE)
                                .collection(PerformerManager.GENRE_VALUE).document(FirebaseAuth.getInstance().getUid());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Log.e("MAIN_ACTIVITY_PERFORMER", "Performer Downloaded");

                            PerformerManager.PERFORMER = PerformerData.setData(task.getResult());
                            getDataForListView();
                        }
                    });
                }
            });
        } else {
            getDataForListView();
        }

       adapter = new PerformerAdapter(context, 0, PerformerManager.PREV_HOTELS);
       hiresList.setAdapter(adapter);
       hiresList.setOnItemClickListener(this);
    }

    private void getDataForListView() {
        CollectionReference hotelsReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                                               .collection("hotels");
        Query selectedUIDHotels = hotelsReference.orderBy("name");
        selectedUIDHotels.get().addOnCompleteListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        MENU_CONTAINER_HEIGHT = menuContainer.getMeasuredHeight();
        locationOnShow = SCREEN_HEIGHT - MENU_CONTAINER_HEIGHT - MENU_CONTAINER_UNDER_CUT;
    }

    private int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_performer_hires_fab:
                if (menuState == 0) {
                    menuState = 1;
                    showAnimatedMenu();
                } else {
                    menuState = 0;
                    removeAnimatedMenu();
                }
        }
    }

    private void showAnimatedMenu() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(menuContainer, View.Y, locationOnShow);
        animation.setDuration(ANIMATION_DURATION);
        animation.start();
    }

    private void removeAnimatedMenu() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(menuContainer, View.Y, locationOnRemove);
        animation.setDuration(ANIMATION_DURATION);
        animation.start();
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        QuerySnapshot querySnapshot = task.getResult();
        for (DocumentSnapshot documentSnapshot : querySnapshot) {
            if (contains(documentSnapshot.get("uid").toString())) {
                PerformerManager.PREV_HOTELS.add(HotelData.setData(documentSnapshot));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private boolean contains(String uid) {
        String[] uidsAvailable = PerformerManager.PERFORMER.getPrevPerformances();
        for (String uidAvailable : uidsAvailable) {
            if (uidAvailable.equals(uid))
                return true;
        }
        return false;
    }

    public void openMyAccount(View view) {
        Intent intent = new Intent(this, PerformerProfile.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, Hotel.class);
        intent.putExtra(INTENT_HOTEL_OBJECT_KEY, position);
        startActivity(intent);
    }
}
