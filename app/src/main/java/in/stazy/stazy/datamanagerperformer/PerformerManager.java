package in.stazy.stazy.datamanagerperformer;

import java.util.ArrayList;
import java.util.Date;

import in.stazy.stazy.datamanagercrossend.HotelData;

public class PerformerManager {

    //This stores the type of the currently logged in Performer
    public static String TYPE_VALUE = null;

    //This stores the genre of the currently logged in Performer
    public static String GENRE_VALUE = null;

    //This stores the performer object
    public static PerformerData PERFORMER = null;

    //This stores the previous hotels that the performer performed in
    public static ArrayList<HotelDataPerformerSide> PREV_HOTELS = new ArrayList<>();


    public static HotelData SHORTLIST_HOTEL = null;


    public static ArrayList<HotelDataPerformerSide> APPROACHES = new ArrayList<>();
}
