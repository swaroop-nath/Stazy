package in.stazy.stazy.datamanager;

import android.content.Context;

public class DataManager {
    private DataCommunication dataCommunication;
    public DataManager() {
    }
    public DataManager(Context context) {
        dataCommunication = (DataCommunication) context;
    }

    public void downloadData(){
        //Set this method to download the data from Database
        //Store the data in the sub-classes : MucisianData, ComedianData, OtherData
    }

    interface DataCommunication {
        void sendData(DataManager dataFiles);
    }
}
