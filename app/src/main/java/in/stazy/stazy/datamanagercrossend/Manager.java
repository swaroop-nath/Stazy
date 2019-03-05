package in.stazy.stazy.datamanagercrossend;

import java.util.ArrayList;

import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagerhotel.ComedianData;
import in.stazy.stazy.datamanagerhotel.MucisianData;
import in.stazy.stazy.datamanagerhotel.OtherData;

import static in.stazy.stazy.hotelend.HirePerformer.FLAG_UNSET;

public class Manager {
    /*
    This class is responsible for storing the relevant data references.
    It is also responsible for minimizing operations cost on data.
     */
    //This stores the list of previously hired mucisians, for the local process.
    public static ArrayList<MucisianData> PREVIOUS_MUCISIANS = new ArrayList<>(0);

    //This stores the list of previously hired comedians, for the local process.
    public static ArrayList<ComedianData> PREVIOUS_COMEDIANS = new ArrayList<>(0);

    //This stores the list of previously hired others, for the local process.
    public static ArrayList<OtherData> PREVIOUS_OTHERS = new ArrayList<>(0);

    //This stores the list of recently fetched available mucisians.
    public static ArrayList<MucisianData> AVAILABLE_MUCISIANS = new ArrayList<>(0);

    //This stores the list of recently fetched available comedians.
    public static ArrayList<ComedianData> AVAILABLE_COMEDIANS = new ArrayList<>(0);

    //This stores the list of recently fetched available others.
    public static ArrayList<OtherData> AVAILABLE_OTHERS = new ArrayList<>(0);

    //This stores the data of the hotel in consideration.
    public static HotelData HOTEL_DATA = null;


    public static String CITY_VALUE;


    public static final int PAYMENT_ID = R.string.PAYMENT_ID;


    public static final int CREDIT_RATE = 50;


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
