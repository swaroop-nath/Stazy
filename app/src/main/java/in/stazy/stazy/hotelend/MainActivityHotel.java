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
import android.widget.ImageView;
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
import in.stazy.stazy.authflow.SignInActivity;
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
    @BindView(R.id.activity_hotel_main_accepted_recycler_view) RecyclerView acceptedList;
    @BindView(R.id.activity_hotel_main_shortlist_text_view) TextView shortlistText;
    @BindView(R.id.activity_hotel_main_hire_text_view) TextView hiresText;
    @BindView(R.id.activity_hotel_main_accepted_text_view) TextView acceptedText;
    @BindView(R.id.activity_main_hotel_toolbar) Toolbar toolbar;
    @BindView(R.id.view_pager_one_selected) ImageView indicatorOne;
    @BindView(R.id.view_pager_two_selected) ImageView indicatorTwo;
    @BindView(R.id.view_pager_three_selected) ImageView indicatorThree;

    //Activity specific declarations
    private Context context;
    private Intent exploreIntent, viewAllIntent;
    private CollectionReference shortlists;
    private ListenerRegistration shortlistListener;
    private ShortlistHiresAdapter shortlistAdapter, hiresAdapter, acceptedAdapter;
    public ImageView[] indicators = new ImageView[3];

    //Constant Field declarations
    public static final String EXPLORE_INTENT_EXTRA_KEY = "type";
    public static final String TYPE_VALUE_MUCISIANS = "Musicians";
    public static final String TYPE_VALUE_COMEDIANS = "Comedians";
    public static final String TYPE_VALUE_OTHERS = "Others";
    public static final String INDIVIDUAL_PERFORMER_OBJECT_KEY = "performer_object";
    public static final int FLAG_SHORTLIST = 1;
    public static final int FLAG_HIRE = 3;
    public static final int FLAG_ACCEPTED = 2;
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

        shortlistAdapter = new ShortlistHiresAdapter(Manager.SHORTLISTED_CANDIDATES, MainActivityHotel.this, FLAG_SHORTLIST);
        hiresAdapter = new ShortlistHiresAdapter(Manager.HIRED_CANDIDATES, MainActivityHotel.this, FLAG_HIRE);
        acceptedAdapter = new ShortlistHiresAdapter(Manager.ACCEPTED_SHORTLISTS, MainActivityHotel.this, FLAG_ACCEPTED);

        shortlistsList.setAdapter(shortlistAdapter);
        hiresList.setAdapter(hiresAdapter);
        acceptedList.setAdapter(acceptedAdapter);
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

                                                if (isAccepted == 1) {
                                                    Manager.ACCEPTED_SHORTLISTS.add(Shortlists.setData(task.getResult(), isHired, isAccepted, genre, type, tentativeDate));
                                                    acceptedAdapter.notifyDataSetChanged();
                                                    if (acceptedList.getVisibility() == View.GONE && acceptedText.getVisibility() == View.GONE) {
                                                        acceptedText.setVisibility(View.VISIBLE);
                                                        acceptedList.setVisibility(View.VISIBLE);
                                                    }
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
                                int indexShortlist = findShortlist(documentSnapshotModified.get("uid").toString());
                                int indexAccepted = findAccepted(documentSnapshotModified.get("uid").toString());
                                if (indexAccepted != -1) {
                                    Manager.SHORTLISTED_CANDIDATES.get(indexShortlist).setIsAccepted((long) documentSnapshotModified.get("isAccepted"));
                                    Manager.SHORTLISTED_CANDIDATES.get(indexShortlist).setIsHired((long) documentSnapshotModified.get("isHired"));
                                    Manager.ACCEPTED_SHORTLISTS.get(indexAccepted).setIsHired((long) documentSnapshotModified.get("isHired"));
                                    Manager.ACCEPTED_SHORTLISTS.get(indexAccepted).setIsAccepted((long) documentSnapshotModified.get("isAccepted"));

                                    if (Manager.SHORTLISTED_CANDIDATES.get(indexShortlist).getIsHired() == 1) {
                                        Manager.HIRED_CANDIDATES.add(Manager.SHORTLISTED_CANDIDATES.remove(indexShortlist));
                                        Manager.ACCEPTED_SHORTLISTS.remove(indexAccepted);
                                        shortlistAdapter.notifyDataSetChanged();
                                        hiresAdapter.notifyDataSetChanged();
                                        acceptedAdapter.notifyDataSetChanged();
                                        if (Manager.SHORTLISTED_CANDIDATES.size() == 0) {
                                            shortlistText.setVisibility(View.GONE);
                                            shortlistsList.setVisibility(View.GONE);
                                        }
                                        if (hiresList.getVisibility() == View.GONE && hiresText.getVisibility() == View.GONE) {
                                            hiresText.setVisibility(View.VISIBLE);
                                            hiresList.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    if (Manager.SHORTLISTED_CANDIDATES.get(indexShortlist).getIsAccepted() == 1) {
                                        Manager.ACCEPTED_SHORTLISTS.add(Manager.SHORTLISTED_CANDIDATES.get(indexShortlist));
                                        acceptedAdapter.notifyDataSetChanged();
                                        if (acceptedList.getVisibility() == View.GONE && acceptedText.getVisibility() == View.GONE) {
                                            acceptedText.setVisibility(View.VISIBLE);
                                            acceptedList.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                                break;
                            case REMOVED:
                                QueryDocumentSnapshot documentSnapshotRemoved = dc.getDocument();
                                int indexTwo = findShortlist(documentSnapshotRemoved.get("uid").toString());
                                int indexThree = findHire(documentSnapshotRemoved.get("uid").toString());
                                int indexFour = findAccepted(documentSnapshotRemoved.get("uid").toString());
                                if (indexFour != -1) {
                                    Manager.ACCEPTED_SHORTLISTS.remove(indexFour);
                                    acceptedAdapter.notifyDataSetChanged();
                                    if (Manager.ACCEPTED_SHORTLISTS.size() == 0) {
                                        acceptedList.setVisibility(View.GONE);
                                        acceptedText.setVisibility(View.GONE);
                                    }
                                }
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

    private int findAccepted(String uid) {
        int index = -1;
        for (int i = 0; i < Manager.ACCEPTED_SHORTLISTS.size(); i++) {
            if (Manager.ACCEPTED_SHORTLISTS.get(i).getUID().equals(uid)) {
                index = i;
                break;
            }
        }
        return index;
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
            Manager.ACCEPTED_SHORTLISTS.clear();
            if (shortlistAdapter != null)
                shortlistAdapter.notifyDataSetChanged();
            if (hiresAdapter != null)
                hiresAdapter.notifyDataSetChanged();
            if (acceptedAdapter != null)
                acceptedAdapter.notifyDataSetChanged();
            shortlistText.setVisibility(View.GONE);
            shortlistsList.setVisibility(View.GONE);
            hiresText.setVisibility(View.GONE);
            hiresList.setVisibility(View.GONE);
            acceptedText.setVisibility(View.GONE);
            acceptedList.setVisibility(View.GONE);
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
                Intent intent = new Intent(MainActivityHotel.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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

        indicators[0] = indicatorOne;
        indicators[1] = indicatorTwo;
        indicators[2] = indicatorThree;

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
            LinearLayoutManager horizontalLayoutAccepted = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            acceptedList.setLayoutManager(horizontalLayoutAccepted);


        }

        exploreViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indicators.length; i++) {
                    if (i == position)
                        indicators[i].setImageResource(R.drawable.scroll_item_selected);
                    else
                        indicators[i].setImageResource(R.drawable.scroll_item_unselected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


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
