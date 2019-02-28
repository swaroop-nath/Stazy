package in.stazy.stazy.datamanagerhotel;

import java.util.ArrayList;

import static in.stazy.stazy.hotelend.HirePerformer.FLAG_UNSET;

public class Manager {
    /*
    This class is responsible for storing the relevant data references.
    It is also responsible for minimizing operations cost on data.
     */
    //This stores the list of previously hired mucisians, for the local process.
    public static final ArrayList<MucisianData> PREVIOUS_MUCISIANS = new ArrayList<>(0);

    //This stores the list of previously hired comedians, for the local process.
    public static final ArrayList<ComedianData> PREVIOUS_COMEDIANS = new ArrayList<>(0);

    //This stores the list of previously hired others, for the local process.
    public static final ArrayList<OtherData> PREVIOUS_OTHERS = new ArrayList<>(0);

    //This stores the list of recently fetched available mucisians.
    public static final ArrayList<MucisianData> AVAILABLE_MUCISIANS = new ArrayList<>(0);

    //This stores the list of recently fetched available comedians.
    public static final ArrayList<ComedianData> AVAILABLE_COMEDIANS = new ArrayList<>(0);

    //This stores the list of recently fetched available others.
    public static final ArrayList<OtherData> AVAILABLE_OTHERS = new ArrayList<>(0);


    public static int AVAILABLE_SINGERS_START_INDEX = 0;


    public static int AVAILABLE_GUITARIST_START_INDEX = 0;


    public static int AVAILABLE_DANCER_START_INDEX = 0;


    public static int AVAILABLE_BAND_START_INDEX = 0;


    public static int AVAILABLE_STAND_UP_START_INDEX = 0;


    public static int AVAILABLE_SHAYARI_START_INDEX = 0;


    public static int AVAILABLE_MAGICIAN_START_INDEX = 0;


    public static int AVAILABLE_SAND_ARTIST_START_INDEX = 0;


    public static int AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX = 0;


    public static int AVAILABLE_DRAMATIST_START_INDEX = 0;


    public static int AVAILABLE_OTHERS_START_INDEX = 0;


    public static int AVAILABLE_SINGERS_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_GUITARIST_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_DANCER_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_BAND_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_STAND_UP_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_SHAYARI_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_MAGICIAN_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_SAND_ARTIST_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_MOTIVATIONAL_SPEAKER_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_DRAMATIST_START_INDEX_SET = FLAG_UNSET;


    public static int AVAILABLE_OTHERS_START_INDEX_SET = FLAG_UNSET;

}
