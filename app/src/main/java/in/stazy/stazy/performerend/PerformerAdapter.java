package in.stazy.stazy.performerend;

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
import in.stazy.stazy.datamanagercrossend.HotelData;

public class PerformerAdapter extends ArrayAdapter<HotelData> {
    private ArrayList<HotelData> prevPerformances;
    private Context context;
    private int resource;

    public PerformerAdapter(@NonNull Context context, int resource, @NonNull List<HotelData> prevPerformances) {
        super(context, resource, prevPerformances);
        this.prevPerformances = (ArrayList<HotelData>) prevPerformances;
        this.context = context;
        this.resource = R.layout.prev_performances_list_item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == convertView) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            HotelViewHolder holder = new HotelViewHolder(view, context);
            holder.setContents(prevPerformances.get(position), position);
            view.setTag(holder);
        } else {
            HotelViewHolder holder = (HotelViewHolder) view.getTag();
            holder.setContents(prevPerformances.get(position), position);
        }
        return view;
    }
}
