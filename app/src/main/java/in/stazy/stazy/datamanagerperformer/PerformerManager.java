package in.stazy.stazy.datamanagerperformer;

import java.util.ArrayList;

import in.stazy.stazy.datamanagercrossend.HotelData;

public class PerformerManager {

    //This stores the type of the currently logged in Performer
    public static final String TYPE_VALUE = null;

    //This stores the genre of the currently logged in Performer
    public static final String GENRE_VALUE = null;

    //This stores the performer object
    public static PerformerData PERFORMER = null;

    //This stores the previous hotels that the performer performed in
    public static ArrayList<HotelData> PREV_HOTELS = new ArrayList<>();
}
