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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
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
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagerhotel.ComedianData;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.MucisianData;
import in.stazy.stazy.datamanagerhotel.Shortlists;
import in.stazy.stazy.datamanagerperformer.PerformerManager;

public class MainActivityHotel extends AppCompatActivity implements Adapter.ActivityCommunication {
    //View references
    //Explore Section ViewPager
    @BindView(R.id.activity_hotel_main_explore_view_pager) ViewPager exploreViewPager;
    @BindView(R.id.activity_hotel_main_shortlist_recycler_view) RecyclerView shortlistsList;
    @BindView(R.id.activity_hotel_main_hire_recycler_view) RecyclerView hiresList;
    @BindView(R.id.activity_hotel_main_shortlist_text_view) TextView shortlistText;
    @BindView(R.id.activity_hotel_main_hire_text_view) TextView hiresText;
    @BindView(R.id.activity_main_hotel_toolbar) Toolbar toolbar;

    //Activity specific declarations
    private Context context;
    private Intent exploreIntent, viewAllIntent;
    private CollectionReference shortlists;
    private ListenerRegistration shortlistListener;
    private ShortlistHiresAdapter shortlistAdapter, hiresAdapter;

    //Constant Field declarations
    public static final String EXPLORE_INTENT_EXTRA_KEY = "type";
    public static final String TYPE_VALUE_MUCISIANS = "Musicians";
    public static final String TYPE_VALUE_COMEDIANS = "Comedians";
    public static final String TYPE_VALUE_OTHERS = "Others";
    public static final String INDIVIDUAL_PERFORMER_OBJECT_KEY = "performer_object";
    public static final int FLAG_SHORTLIST = 1;
    public static final int FLAG_HIRE = 2;
    public static final String SHORTLIST_HIRE_INTENT_KEY = "shortlist_hire";

    private static final String GENRE_VALUE_SINGER = "Singer";
    private static final String GENRE_VALUE_GUITARIST = "Guitarist";
    private static final String GENRE_VALUE_BAND = "Band";
    private static final String GENRE_VALUE_STAND_UP = "Stand-Ups";
    private static final String GENRE_VALUE_SHAYARI = "Shayari";
    private static final String GENRE_VALUE_MAGICIAN = "Magician";
    private static final String GENRE_VALUE_MOTIVATIONAL_SPEAKER = "Motivational Speaker";
    private static final String GENRE_VALUE_DJ = "DJ";
    private static final String GENRE_VALUE_OTHERS = "Others";
    public static final String PREV_PERFORMERS_GENRE_KEY = "prev_genre";
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG", "Resume called");
        shortlists = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("Shortlists")
                .document(FirebaseAuth.getInstance().getUid()).collection("List");
        if (shortlists != null) {
            Query shortlistsQuery = shortlists.whereGreaterThan("isHired", -1);
            shortlistListener = shortlistsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e("SHORTLIST LISTENER", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                QueryDocumentSnapshot documentSnapshotAdded = dc.getDocument();
                                DocumentReference performer = documentSnapshotAdded.getDocumentReference("performer");
                                final long isHired = (long) documentSnapshotAdded.get("isHired");
                                final long isAccepted = (long) documentSnapshotAdded.get("isAccepted");
                                final String type = documentSnapshotAdded.get("type").toString();
                                final String genre = documentSnapshotAdded.get("genre").toString();
                                final Date tentativeDate = documentSnapshotAdded.getTimestamp("date").toDate();
                                performer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (isHired == 0) {
                                                Manager.SHORTLISTED_CANDIDATES.add(Shortlists.setData(task.getResult(), isHired, isAccepted, genre, type, tentativeDate));
                                                shortlistAdapter.notifyDataSetChanged();
                                                if (shortlistText.getVisibility() == View.GONE && shortlistsList.getVisibility() == View.GONE) {
                                                    shortlistText.setVisibility(View.VISIBLE);
                                                    shortlistsList.setVisibility(View.VISIBLE);
                                                }
                                            } else if (isHired == 1) {
                                                Manager.HIRED_CANDIDATES.add(Shortlists.setData(task.getResult(), isHired, isAccepted, genre, type, tentativeDate));
                                                hiresAdapter.notifyDataSetChanged();
                                                if (hiresText.getVisibility() == View.GONE && hiresList.getVisibility() == View.GONE) {
                                                    hiresText.setVisibility(View.VISIBLE);
                                                    hiresList.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                });
                                break;
                            case MODIFIED:
                                QueryDocumentSnapshot documentSnapshotModified = dc.getDocument();
                                int index = findShortlist(documentSnapshotModified.get("uid").toString());
                                if (index != -1) {
                                    Manager.SHORTLISTED_CANDIDATES.get(index).setIsAccepted((long) documentSnapshotModified.get("isAccepted"));
                                    Manager.SHORTLISTED_CANDIDATES.get(index).setIsHired((long) documentSnapshotModified.get("isHired"));
                                    if (Manager.SHORTLISTED_CANDIDATES.get(index).getIsHired() == 1) {
                                        Manager.HIRED_CANDIDATES.add(Manager.SHORTLISTED_CANDIDATES.remove(index));
                                        shortlistAdapter.notifyDataSetChanged();
                                        hiresAdapter.notifyDataSetChanged();
                                        if (Manager.SHORTLISTED_CANDIDATES.size() == 0) {
                                            shortlistText.setVisibility(View.GONE);
                                            shortlistsList.setVisibility(View.GONE);
                                        }
                                        if (hiresList.getVisibility() == View.GONE && hiresText.getVisibility() == View.GONE) {
                                            hiresText.setVisibility(View.VISIBLE);
                                            hiresList.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                                break;
                            case REMOVED:
                                QueryDocumentSnapshot documentSnapshotRemoved = dc.getDocument();
                                int indexTwo = findShortlist(documentSnapshotRemoved.get("uid").toString());
                                int indexThree = findHire(documentSnapshotRemoved.get("uid").toString());
                                if (indexTwo != -1) {
                                    Manager.SHORTLISTED_CANDIDATES.remove(indexTwo);
                                    shortlistAdapter.notifyDataSetChanged();
                                    if (Manager.SHORTLISTED_CANDIDATES.size() == 0) {
                                        shortlistText.setVisibility(View.GONE);
                                        shortlistsList.setVisibility(View.GONE);
                                    }
                                } else if (indexThree != -1) {
                                    Manager.HIRED_CANDIDATES.remove(indexThree);
                                    if (Manager.HIRED_CANDIDATES.size() == 0) {
                                        hiresText.setVisibility(View.GONE);
                                        hiresList.setVisibility(View.GONE);
                                    }
                                }
                                break;
                        }
                    }
                }
            });
        }
    }

    private int findHire(String uid) {
        int index = -1;
        for (int i = 0; i < Manager.HIRED_CANDIDATES.size(); i++) {
            if (Manager.HIRED_CANDIDATES.get(i).getUID().equals(uid)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int findShortlist(String uid) {
        int index = -1;
        for (int i = 0; i < Manager.SHORTLISTED_CANDIDATES.size(); i++) {
            if (Manager.SHORTLISTED_CANDIDATES.get(i).getUID().equals(uid)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TAG", "Pause called");
        if (shortlistListener != null) {
            shortlistListener.remove();
            Manager.SHORTLISTED_CANDIDATES.clear();
            Manager.HIRED_CANDIDATES.clear();
            shortlistAdapter.notifyDataSetChanged();
            hiresAdapter.notifyDataSetChanged();
            shortlistText.setVisibility(View.GONE);
            shortlistsList.setVisibility(View.GONE);
            hiresText.setVisibility(View.GONE);
            hiresList.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_hotel_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_button:
                Intent accountIntent = new Intent(MainActivityHotel.this, HotelProfile.class);
                startActivity(accountIntent);
                break;
            case R.id.sign_out_button:
                FirebaseAuth.getInstance().signOut();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hotel);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ArrayList<ExploreCardModel> models = new ArrayList<>();
        models.add(new ExploreCardModel(R.drawable.musicians));
        models.add(new ExploreCardModel(R.drawable.comedians));
        models.add(new ExploreCardModel(R.drawable.others));

        Adapter adapter = new Adapter(models, MainActivityHotel.this);

        exploreViewPager.setAdapter(adapter);

        if (Manager.NEW_TOKEN_RECEIVED == 1)
            sendTokenToServer(Manager.FCM_TOKEN);

        context = getApplicationContext();
        FirebaseApp.initializeApp(context);

        //Setting Intents for Explore children
        exploreIntent = new Intent(context, HirePerformer.class);

        //Setting Intent for view all buttons in prev performances
        viewAllIntent = new Intent(context, ViewAllPerformers.class);


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
                        //TODO: Write code here to download previous and shortlists and hires.
                    } else {
                        Log.e("HOTEL_DATA_DOWNLOAD", task.getException().getMessage());
                        Toast.makeText(context, "Can't Download Data Right Now, Try Again Later!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            LinearLayoutManager horizontalLayoutShortlist = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            shortlistsList.setLayoutManager(horizontalLayoutShortlist);
            LinearLayoutManager horizontalLayoutHire = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            hiresList.setLayoutManager(horizontalLayoutHire);

            shortlistAdapter = new ShortlistHiresAdapter(Manager.SHORTLISTED_CANDIDATES, MainActivityHotel.this, FLAG_SHORTLIST);
            hiresAdapter = new ShortlistHiresAdapter(Manager.HIRED_CANDIDATES, MainActivityHotel.this, FLAG_HIRE);

            shortlistsList.setAdapter(shortlistAdapter);
            hiresList.setAdapter(hiresAdapter);
        }


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

    @Override
    public void onViewPagerClick(Intent intent) {
        startActivity(intent);
    }

    public void openPrevSingers(View view) {
        showPrevPerformers(GENRE_VALUE_SINGER);
    }

    public void openPrevGuitarist(View view) {
        showPrevPerformers(GENRE_VALUE_GUITARIST);
    }

    public void openPrevBand(View view) {
        showPrevPerformers(GENRE_VALUE_BAND);
    }

    public void openPrevStandUPs(View view) {
        showPrevPerformers(GENRE_VALUE_STAND_UP);
    }

    public void openPrevShayari(View view) {
        showPrevPerformers(GENRE_VALUE_SHAYARI);
    }

    public void openPrevMagicians(View view) {
        showPrevPerformers(GENRE_VALUE_MAGICIAN);
    }

    public void openPrevDJs(View view) {
        showPrevPerformers(GENRE_VALUE_DJ);
    }

    public void openPrevSpeakers(View view) {
        showPrevPerformers(GENRE_VALUE_MOTIVATIONAL_SPEAKER);
    }

    public void openPrevOthers(View view) {
        showPrevPerformers(GENRE_VALUE_OTHERS);
    }

    private void showPrevPerformers(String genre) {
        Intent intent = new Intent(this, ViewAllPerformers.class);
        intent.putExtra(PREV_PERFORMERS_GENRE_KEY, genre);
        startActivity(intent);
    }
}
