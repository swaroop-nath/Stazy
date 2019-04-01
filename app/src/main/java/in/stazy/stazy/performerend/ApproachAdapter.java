package in.stazy.stazy.performerend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import in.stazy.stazy.R;
import in.stazy.stazy.datamanagerperformer.HotelDataPerformerSide;

public class ApproachAdapter extends RecyclerView.Adapter<ApproachViewHolder> implements ApproachViewHolder.ItemClickListener {
    public static final String SHOW_EXTRA_CONTENT_PERFORMER_END_TWO = "show";
    public static final String POSITION = "position";
    private ArrayList<HotelDataPerformerSide> approaches;
    private Context context;

    public ApproachAdapter(ArrayList<HotelDataPerformerSide> approaches, Context context) {
        this.approaches = approaches;
        this.context = context;
    }

    @Override
    public ApproachViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.hotel_approaches_list_item, null);
        ApproachViewHolder holder = new ApproachViewHolder(itemView);
        holder.attachClickListener(ApproachAdapter.this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ApproachViewHolder holder, int position) {
        Glide.with(context).clear(holder.getProfilePicture());
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(approaches.get(position).getUID()+"/"+approaches.get(position).getPicName());
        Glide.with(context).asBitmap().load(imageRef).into(holder.getProfilePicture());
        holder.getHotelName().setText(approaches.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return approaches.size();
    }


    @Override
    public void onItemClick(int position) {
        //Start the Hotel activity
        Intent intent = new Intent(context, Hotel.class);
        intent.putExtra(SHOW_EXTRA_CONTENT_PERFORMER_END_TWO, true);
        intent.putExtra(POSITION, position);
        context.startActivity(intent);
    }
}
