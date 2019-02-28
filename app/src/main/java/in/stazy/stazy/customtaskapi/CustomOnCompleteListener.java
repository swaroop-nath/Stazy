package in.stazy.stazy.customtaskapi;

import android.support.annotation.NonNull;
import com.google.firebase.firestore.QuerySnapshot;

public interface CustomOnCompleteListener {
    void onDataDownloaded(@NonNull QuerySnapshot querySnapshot, String typeChosen, String genreChosen);
}
