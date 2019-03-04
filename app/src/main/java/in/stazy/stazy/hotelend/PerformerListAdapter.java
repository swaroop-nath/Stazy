package in.stazy.stazy.hotelend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import in.stazy.stazy.R;
import in.stazy.stazy.datamanagerhotel.DataManager;

public class PerformerListAdapter extends ArrayAdapter<DataManager> {
    private ArrayList<DataManager> dataset;
    private Context context;
    private int resource;

    public PerformerListAdapter(@NonNull Context context, int resource, @NonNull List<DataManager> objects) {
        super(context, resource, objects);
        this.context = context;
        dataset = (ArrayList<DataManager>) objects;
        this.resource = R.layout.view_all_performers_list_item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater;
        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            PerformerViewHolder holder = new PerformerViewHolder(view);
            view.setTag(holder);
        } else {
            PerformerViewHolder holder = (PerformerViewHolder) view.getTag();
            holder.setProfilePicture("Send UID here");
            DataManager performer = dataset.get(position);
            holder.setTexts(performer.getName(), performer.getLastRating(), performer.getRating(), performer.getPrice());
        }
        return view;
    }

}
