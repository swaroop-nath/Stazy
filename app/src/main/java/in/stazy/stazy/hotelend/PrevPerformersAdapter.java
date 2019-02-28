package in.stazy.stazy.hotelend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import in.stazy.stazy.R;
import in.stazy.stazy.datamanagerhotel.DataManager;

import static in.stazy.stazy.hotelend.MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY;
import static in.stazy.stazy.hotelend.MainActivityHotel.INDIVIDUAL_PERFORMER_OBJECT_KEY;

public class PrevPerformersAdapter<T extends DataManager> extends RecyclerView.Adapter<PrevPerformersViewHolder> implements PrevPerformersViewHolder.PrevPerformersCommunication {

    private ArrayList<T> performerList;
    private Context context;
    private String type;

    public PrevPerformersAdapter(ArrayList<T> data, Context context, String type) {
        performerList = data;
        this.context = context;
        this.type = type;
    }

    @Override
    public PrevPerformersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.prev_performers_child_recyler_item_view, parent, false);
        PrevPerformersViewHolder holder = new PrevPerformersViewHolder(view);
        holder.attachOpenPerformerListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(PrevPerformersViewHolder holder, int position) {
        holder.getDisplayImage().setImageBitmap(performerList.get(position).getProfilePictureHigh());
        holder.getProfileName().setText(performerList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return performerList.size();
    }

    @Override
    public void openPerformer(int positionClicked) {
        Intent intent = new Intent(context, Performer.class);
        intent.putExtra(EXPLORE_INTENT_EXTRA_KEY, type);
        intent.putExtra(INDIVIDUAL_PERFORMER_OBJECT_KEY, performerList.get(positionClicked));
        context.startActivity(intent);
    }
}