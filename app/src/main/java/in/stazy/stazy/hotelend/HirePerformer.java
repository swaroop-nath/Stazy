package in.stazy.stazy.hotelend;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.stazy.stazy.R;
import in.stazy.stazy.customtaskapi.CustomOnCompleteListener;
import in.stazy.stazy.customtaskapi.CustomTaskCompletioner;
import in.stazy.stazy.datamanagerhotel.ComedianData;
import in.stazy.stazy.datamanagerhotel.DataManager;
import in.stazy.stazy.datamanagerhotel.Manager;
import in.stazy.stazy.datamanagerhotel.MucisianData;
import in.stazy.stazy.datamanagerhotel.OtherData;

import static in.stazy.stazy.hotelend.MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_BAND;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_DANCER;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_DRAMATIST;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_GUITARIST;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_MAGICIAN;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_MOTIVATIONAL_SPEAKER;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_OTHERS;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_SAND_ARTIST;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_SHAYARI;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_SINGER;
import static in.stazy.stazy.hotelend.MainActivityHotel.GENRE_VALUE_STAND_UP;
import static in.stazy.stazy.hotelend.MainActivityHotel.INDIVIDUAL_PERFORMER_OBJECT_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_COMEDIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_MUCISIANS;
import static in.stazy.stazy.hotelend.MainActivityHotel.TYPE_VALUE_OTHERS;

public class HirePerformer extends AppCompatActivity implements CustomOnCompleteListener, AdapterView.OnItemClickListener {

    //View References
    @BindView(R.id.activity_hire_performer_spinner_type) Spinner type;
    @BindView(R.id.activity_hire_performer_spinner_genre) Spinner genre;
    @BindView(R.id.activity_hire_performer_spinner_sort) Spinner sort;
    @BindView(R.id.activity_hire_performer_list_view) ListView availablePerformers;

    //Activity Specific References
    private Context context;
    private static final String TAG = "HirePerformer";
    ArrayAdapter<String> adapterTypeMucisians, adapterTypeComedians, adapterTypeOthers;
    private Queue<String> genreQueue, typeQueue;
    String[] genreMucisians, genreComedians, genreOthers;
    private PerformerListAdapter adapter;
    private ArrayList<DataManager> dataset = new ArrayList<>();

    //Firebase References
    FirebaseFirestore firebaseFirestore;
    CollectionReference baseReference, childReference;
    
    //Constant Declaration
    public static int FLAG_SET = 1;
    public static int FLAG_UNSET = 0;

    /*
    TODO: Define the characteristic of sort spinner.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_performer);
        ButterKnife.bind(this);
        context = getApplicationContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        baseReference = firebaseFirestore.collection("Cities").document("prayagraj").
                collection("type");

        Intent receivedIntent = getIntent();
        String typeReceived = receivedIntent.getStringExtra(EXPLORE_INTENT_EXTRA_KEY);
        setSpinnerTypeSelection(typeReceived);

        Resources resources = getResources();
        genreMucisians = resources.getStringArray(R.array.spinner_genre_mucisians_entries);
        genreComedians = resources.getStringArray(R.array.spinner_genre_comedians_entries);
        genreOthers = resources.getStringArray(R.array.spinner_genre_others_entries);

        adapterTypeMucisians = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genreMucisians);
        adapterTypeComedians = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genreComedians);
        adapterTypeOthers = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, genreOthers);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        genre.setAdapter(adapterTypeMucisians);
                        setData();
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        genre.setAdapter(adapterTypeComedians);
                        setData();
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        genre.setAdapter(adapterTypeOthers);
                        setData();
                        adapter.notifyDataSetChanged();
                        break;
                    default: break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setData();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        typeQueue = new PriorityQueue<>();
        genreQueue = new PriorityQueue<>();

        switch (typeReceived) {
            case TYPE_VALUE_MUCISIANS:
                putElementsInGenreQueue(genreMucisians);
                putElementsInGenreQueue(genreComedians);
                putElementsInGenreQueue(genreOthers);
                putElementsInTypeQueue(TYPE_VALUE_MUCISIANS, TYPE_VALUE_COMEDIANS, TYPE_VALUE_OTHERS);
                break;
            case TYPE_VALUE_COMEDIANS:
                putElementsInGenreQueue(genreComedians);
                putElementsInGenreQueue(genreOthers);
                putElementsInGenreQueue(genreMucisians);
                putElementsInTypeQueue(TYPE_VALUE_COMEDIANS, TYPE_VALUE_OTHERS, TYPE_VALUE_MUCISIANS);
                break;
            case TYPE_VALUE_OTHERS:
                putElementsInGenreQueue(genreOthers);
                putElementsInGenreQueue(genreMucisians);
                putElementsInGenreQueue(genreComedians);
                putElementsInTypeQueue(TYPE_VALUE_OTHERS, TYPE_VALUE_MUCISIANS, TYPE_VALUE_COMEDIANS);
                break;
        }

        downloadData();
        setData();
        adapter = new PerformerListAdapter(context, R.layout.view_all_performers_list_item, dataset);
        availablePerformers.setAdapter(adapter);
        availablePerformers.setOnItemClickListener(this);
    }

    private void downloadData() {
        Queue<String> restStart = new PriorityQueue<>();
        switch (typeQueue.peek()) {
            case TYPE_VALUE_MUCISIANS:
                restStart.offer(genreComedians[0]);
                restStart.offer(genreOthers[0]);
                break;
            case TYPE_VALUE_COMEDIANS:
                restStart.offer(genreOthers[0]);
                restStart.offer(genreMucisians[0]);
                break;
            case TYPE_VALUE_OTHERS:
                restStart.offer(genreMucisians[0]);
                restStart.offer(genreComedians[0]);
        }

        while (!genreQueue.isEmpty()) {
            downloadDataNow(typeQueue.peek(), genreQueue.poll());
            if (genreQueue.peek().equals(restStart.peek())) {
                typeQueue.poll();
                restStart.poll();
            }
        }
    }

    private void downloadDataNow(String typeChosen, String genreChosen) {
        //TODO: Change the signature of the method to incorporate city value!
        childReference = baseReference.document(typeChosen).collection(genreChosen);
        Query alphabeticalQuery = childReference.orderBy("name");
        alphabeticalQuery.get().addOnCompleteListener(new CustomTaskCompletioner(this, typeChosen, genreChosen));
    }

    private void putElementsInTypeQueue(String typeValueOne, String typeValueTwo, String typeValueThree) {
        typeQueue.offer(typeValueOne);
        typeQueue.offer(typeValueTwo);
        typeQueue.offer(typeValueThree);
    }

    private void putElementsInGenreQueue(String[] genrePassed) {
        for (String genre : genrePassed)
            genreQueue.offer(genre);
    }

    private void setData() {
        String setType = type.getSelectedItem().toString();
        String setGenre = genre.getSelectedItem().toString();

        switch (setType) {
            case TYPE_VALUE_MUCISIANS:
                switch (setGenre) {
                    case GENRE_VALUE_SINGER:
                        fillDataSet(Manager.AVAILABLE_MUCISIANS, Manager.AVAILABLE_SINGERS_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_GUITARIST:
                        fillDataSet(Manager.AVAILABLE_MUCISIANS, Manager.AVAILABLE_GUITARIST_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_DANCER:
                        fillDataSet(Manager.AVAILABLE_MUCISIANS, Manager.AVAILABLE_DANCER_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_BAND:
                        fillDataSet(Manager.AVAILABLE_MUCISIANS, Manager.AVAILABLE_BAND_START_INDEX, setGenre);
                        break;
                }
                break;
            case TYPE_VALUE_COMEDIANS:
                switch (setGenre) {
                    case GENRE_VALUE_STAND_UP:
                        fillDataSet(Manager.AVAILABLE_COMEDIANS, Manager.AVAILABLE_STAND_UP_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_SHAYARI:
                        fillDataSet(Manager.AVAILABLE_COMEDIANS, Manager.AVAILABLE_SHAYARI_START_INDEX, setGenre);
                        break;
                }
                break;
            case TYPE_VALUE_OTHERS:
                switch (setGenre) {
                    case GENRE_VALUE_MAGICIAN:
                        fillDataSet(Manager.AVAILABLE_OTHERS, Manager.AVAILABLE_MAGICIAN_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_SAND_ARTIST:
                        fillDataSet(Manager.AVAILABLE_OTHERS, Manager.AVAILABLE_SAND_ARTIST_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_MOTIVATIONAL_SPEAKER:
                        fillDataSet(Manager.AVAILABLE_OTHERS, Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_DRAMATIST:
                        fillDataSet(Manager.AVAILABLE_OTHERS, Manager.AVAILABLE_DRAMATIST_START_INDEX, setGenre);
                        break;
                    case GENRE_VALUE_OTHERS:
                        fillDataSet(Manager.AVAILABLE_OTHERS, Manager.AVAILABLE_OTHERS_START_INDEX, setGenre);
                        break;
                }
                break;
        }
    }

    private void fillDataSet(ArrayList<? extends DataManager> dataSource, int startIndex, String chosenGenre) {
        while (dataSource.get(startIndex).getGenre().equals(chosenGenre)) {
            dataset.add(dataSource.get(startIndex));
            startIndex++;
        }
    }

    private void notifyChangesToAdapter(String chosenType, String chosenGenre) {
        String typeFocused = type.getSelectedItem().toString();
        String genreFocused = type.getSelectedItem().toString();
        if (typeFocused.equals(chosenType) && genreFocused.equals(chosenGenre)) {
            adapter.notifyDataSetChanged();
            return;
        }
    }

    private void setSpinnerTypeSelection(String typeSelection) {
        switch (typeSelection) {
            case TYPE_VALUE_MUCISIANS:
                type.setSelection(0, true);
                genre.setAdapter(adapterTypeMucisians);
                break;
            case TYPE_VALUE_COMEDIANS:
                type.setSelection(1, true);
                genre.setAdapter(adapterTypeComedians);
                break;
            case TYPE_VALUE_OTHERS:
                type.setSelection(2, true);
                genre.setAdapter(adapterTypeOthers);
                break;
            default:
                Log.e(TAG, typeSelection);
                break;
        }
    }

    @Override
    public void onDataDownloaded(@NonNull QuerySnapshot querySnapshot, String typeChosen, String genreChosen) {
        if (!querySnapshot.isEmpty()) {
            List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
            switch (typeChosen) {
                case TYPE_VALUE_MUCISIANS:
                    Manager.AVAILABLE_MUCISIANS.addAll(MucisianData.fetchMucisians(documentSnapshots, "prayagraj", genreChosen)); //After signature is changed to incorporate the city, send city to this method
                    switch (genreChosen) {
                        case GENRE_VALUE_SINGER:
                            Manager.AVAILABLE_SINGERS_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_GUITARIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_GUITARIST_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_BAND_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_BAND_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_DANCER_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_DANCER_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                            break;
                        case GENRE_VALUE_GUITARIST:
                            Manager.AVAILABLE_GUITARIST_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_SINGERS_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SINGERS_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_BAND_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_BAND_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_DANCER_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_DANCER_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                            break;
                        case GENRE_VALUE_DANCER:
                            Manager.AVAILABLE_DANCER_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_GUITARIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_GUITARIST_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_BAND_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_BAND_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_SINGERS_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SINGERS_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                            break;
                        case GENRE_VALUE_BAND:
                            Manager.AVAILABLE_BAND_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_GUITARIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_GUITARIST_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_SINGERS_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SINGERS_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            if (Manager.AVAILABLE_DANCER_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_DANCER_START_INDEX = Manager.AVAILABLE_MUCISIANS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                            break;
                    }
                    break;
                case TYPE_VALUE_COMEDIANS:
                    Manager.AVAILABLE_COMEDIANS.addAll(ComedianData.fetchComedians(documentSnapshots, "prayagraj", genreChosen));
                    switch (genreChosen) {
                        case GENRE_VALUE_STAND_UP:
                            Manager.AVAILABLE_STAND_UP_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_SHAYARI_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SHAYARI_START_INDEX = Manager.AVAILABLE_COMEDIANS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                            break;
                        case GENRE_VALUE_SHAYARI:
                            Manager.AVAILABLE_SHAYARI_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_STAND_UP_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_STAND_UP_START_INDEX = Manager.AVAILABLE_COMEDIANS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                            break;                         
                    }
                    break;
                case TYPE_VALUE_OTHERS:
                    Manager.AVAILABLE_OTHERS.addAll(OtherData.fetchOthers(documentSnapshots, "prayagraj", genreChosen));
                    switch (genreChosen) {
                        case GENRE_VALUE_MAGICIAN:
                            Manager.AVAILABLE_MAGICIAN_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_SAND_ARTIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SAND_ARTIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_DRAMATIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_DRAMATIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_OTHERS_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_OTHERS_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                        case GENRE_VALUE_SAND_ARTIST:
                            Manager.AVAILABLE_SAND_ARTIST_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_MAGICIAN_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MAGICIAN_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_DRAMATIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_DRAMATIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_OTHERS_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_OTHERS_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                        case GENRE_VALUE_MOTIVATIONAL_SPEAKER:
                            Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_SAND_ARTIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SAND_ARTIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_MAGICIAN_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MAGICIAN_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_DRAMATIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_DRAMATIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_OTHERS_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_OTHERS_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                        case GENRE_VALUE_DRAMATIST:
                            Manager.AVAILABLE_DRAMATIST_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_SAND_ARTIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SAND_ARTIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_MAGICIAN_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MAGICIAN_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_OTHERS_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_OTHERS_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                        case GENRE_VALUE_OTHERS:
                            Manager.AVAILABLE_OTHERS_START_INDEX_SET = FLAG_SET;
                            if (Manager.AVAILABLE_SAND_ARTIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_SAND_ARTIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_DRAMATIST_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_DRAMATIST_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            if (Manager.AVAILABLE_MAGICIAN_START_INDEX_SET == FLAG_UNSET)
                                Manager.AVAILABLE_MAGICIAN_START_INDEX = Manager.AVAILABLE_OTHERS.size();
                            notifyChangesToAdapter(typeChosen, genreChosen);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, Performer.class);
        String chosenType = null;
        switch (type.getSelectedItemPosition()) {
            case 0:
                chosenType = TYPE_VALUE_MUCISIANS;
                break;
            case 1:
                chosenType = TYPE_VALUE_COMEDIANS;
                break;
            case 2:
                chosenType = TYPE_VALUE_OTHERS;
                break;
        }

        intent.putExtra(EXPLORE_INTENT_EXTRA_KEY, chosenType);
        intent.putExtra(INDIVIDUAL_PERFORMER_OBJECT_KEY, dataset.get(position));
        startActivity(intent);
    }
}
