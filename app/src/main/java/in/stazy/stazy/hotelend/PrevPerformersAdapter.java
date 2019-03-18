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
import in.stazy.stazy.datamanagerhotel.PrevPerformers;

public class PrevPerformersAdapter extends ArrayAdapter {
    private ArrayList<PrevPerformers> prevPerformers;
    private Context context;
    private int resource;

    public PrevPerformersAdapter(@NonNull Context context, int resource, @NonNull List<PrevPerformers> objects) {
        super(context, resource, objects);
        this.context = context;
        this.prevPerformers = (ArrayList<PrevPerformers>) objects;
        this.resource = R.layout.prev_performers_hotel_item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, parent, false);
            PrevPerformersViewHolder holder = new PrevPerformersViewHolder(view, context);
            holder.setContents(prevPerformers.get(position));
            view.setTag(holder);
        } else {
            PrevPerformersViewHolder holder = (PrevPerformersViewHolder) view.getTag();
            holder.setContents(prevPerformers.get(position));
        }
        return view;
    }
}
