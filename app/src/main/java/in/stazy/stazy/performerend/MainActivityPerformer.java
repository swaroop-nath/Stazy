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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.authflow.MessageService;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.Shortlists;
import in.stazy.stazy.datamanagerperformer.HotelDataPerformerSide;
import in.stazy.stazy.datamanagerperformer.PerformerData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;
import in.stazy.stazy.hotelend.Performer;

public class MainActivityPerformer extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

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
    private ListenerRegistration listenerRegistration;
    private CollectionReference hotelsReference;

    //Constant field declarations
    private static final long ANIMATION_DURATION = 500;
    private static int SCREEN_HEIGHT = 0;
    private static int MENU_CONTAINER_HEIGHT = 0;
    private static float MENU_CONTAINER_UNDER_CUT = 0f;
    public static final String INTENT_HOTEL_OBJECT_KEY = "hotel";
    public static final int HIGH_PRIORITY = 5;

    @Override
    protected void onStart() {
        super.onStart();
        if (hotelsReference != null) {
            Query selectedUIDHotels = hotelsReference.orderBy("date");
            final SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");

            listenerRegistration = selectedUIDHotels.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    //Define Structure here
                    if (e != null) {
                        Log.e("SHORTLIST LISTENER", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.e("PREV", "ADDED");
                                final QueryDocumentSnapshot documentSnapshotAdded = dc.getDocument();
                                DocumentReference hotel = documentSnapshotAdded.getDocumentReference("hotel");
                                hotel.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            PerformerManager.PREV_HOTELS.add(HotelDataPerformerSide.setData(task.getResult(), documentSnapshotAdded.get("rating_received").toString(), formatter.format(documentSnapshotAdded.getTimestamp("date").toDate())));
                                            Log.e("PREV", "Name: " + PerformerManager.PREV_HOTELS.get(0).getName());
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Log.e("TAG", task.getException().getMessage());
                                        }
                                    }
                                });
                                break;
                            case MODIFIED:
                                Log.e("PREV", "MODIFIED");
                                QueryDocumentSnapshot documentSnapshotModified = dc.getDocument();
                                int index = findHotel(documentSnapshotModified.get("uid").toString());
                                if (index != -1) {
                                    PerformerManager.PREV_HOTELS.get(index).setRating(documentSnapshotModified.get("rating_received").toString());
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                }
            });
        }
    }

    private int findHotel(String uid) {
        int index = -1;
        for (int i = 0; i < PerformerManager.PREV_HOTELS.size(); i++) {
            if (PerformerManager.PREV_HOTELS.get(i).getUID().equals(uid)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerRegistration.remove();
        PerformerManager.PREV_HOTELS.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_performer);
        ButterKnife.bind(this);
        context = getApplicationContext();

        if (Manager.NEW_TOKEN_RECEIVED == 1)
            sendTokenToServer(Manager.FCM_TOKEN);

        menuFab.setOnClickListener(this);

        SCREEN_HEIGHT = getScreenHeight();
        locationOnRemove = SCREEN_HEIGHT;
        MENU_CONTAINER_UNDER_CUT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        if (PerformerManager.PERFORMER == null) {
            DocumentReference mapperReference = firebaseFirestore.collection("Mapper").document(FirebaseAuth.getInstance().getUid());
            mapperReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                                .collection("type").document(PerformerManager.TYPE_VALUE)
                                .collection(PerformerManager.GENRE_VALUE).document(FirebaseAuth.getInstance().getUid());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            PerformerManager.PERFORMER = PerformerData.setData(task.getResult());

                        }
                    });
                }
            });
        }
        PerformerManager.PREV_HOTELS.clear();
        hotelsReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE).collection("PreviousHotels")
                .document(FirebaseAuth.getInstance().getUid()).collection("List");
        adapter = new PerformerAdapter(context, 0, PerformerManager.PREV_HOTELS);
        hiresList.setAdapter(adapter);
        hiresList.setOnItemClickListener(this);
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
        intent.putExtra(MessageService.SHOW_EXTRA_CONTENT_PERFORMER_END, false);
        intent.putExtra(INTENT_HOTEL_OBJECT_KEY, position);
        startActivity(intent);
    }

    private void sendTokenToServer(String fcmToken) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", fcmToken);
        //Performer Logged in
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                    .collection("type").document(PerformerManager.TYPE_VALUE).collection(PerformerManager.GENRE_VALUE).document(FirebaseAuth.getInstance().getUid());
        documentReference.update(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
