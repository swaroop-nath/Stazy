package in.stazy.stazy.customtaskapi;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class CustomTaskCompletioner implements OnCompleteListener<QuerySnapshot> {
    private CustomOnCompleteListener customOnCompleteListener;
    private String genreChosen, typeChosen;
    public CustomTaskCompletioner(CustomOnCompleteListener listener, String typeChosen, String genreChosen) {
        this.customOnCompleteListener = listener;
        this.typeChosen = typeChosen;
        this.genreChosen = genreChosen;
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        QuerySnapshot querySnapshot = task.getResult();
        customOnCompleteListener.onDataDownloaded(querySnapshot, typeChosen, genreChosen);
    }
}
