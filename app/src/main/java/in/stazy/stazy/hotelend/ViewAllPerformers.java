package in.stazy.stazy.hotelend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.PrevPerformers;

public class ViewAllPerformers extends AppCompatActivity implements OnCompleteListener<QuerySnapshot> {

    @BindView(R.id.activity_view_all_performers_list_view) ListView prevPerformersList;
    @BindView(R.id.activity_view_all_performers_toolbar) Toolbar toolbar;

    private ArrayList<PrevPerformers> prevPerformers = new ArrayList<>();
    private PrevPerformersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_performers);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String genre = intent.getStringExtra(MainActivityHotel.PREV_PERFORMERS_GENRE_KEY);

        CollectionReference prevPerformances = FirebaseFirestore.getInstance().collection("Cities").document(Manager.CITY_VALUE).collection("PreviousPerformances")
                .document(FirebaseAuth.getInstance().getUid()).collection(genre);

        if (prevPerformances != null) {
            prevPerformances.get().addOnCompleteListener(this);
        }

        adapter = new PrevPerformersAdapter(ViewAllPerformers.this, 0, prevPerformers);
        prevPerformersList.setAdapter(adapter);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        final SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
        QuerySnapshot queryDocumentSnapshots = task.getResult();
        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
            snapshot.getDocumentReference("performer").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    prevPerformers.add(PrevPerformers.setData(task.getResult(), Double.valueOf(snapshot.get("rating_received").toString()), formatter.format(snapshot.getTimestamp("date").toDate()), Manager.CITY_VALUE)) ;
                    adapter.notifyDataSetChanged();
                }
            });
        }

    }
}
