package in.stazy.stazy.hotelend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerhotel.Shortlists;

public class ShortlistHiresAdapter extends RecyclerView.Adapter<ShortlistHiresViewHolder> implements ShortlistHiresViewHolder.ShortlistsHireCommunication {
    public static final String SELECTED_FLAG = "selected_flag";
    private ArrayList<Shortlists> shortlists;
    private Context context;
    private int flag;

    public static final String CLICKED_POSITION = "clicked_position";

    public ShortlistHiresAdapter(ArrayList<Shortlists> shortlists, Context context, int flag) {
        this.shortlists = shortlists;
        this.context = context;
        this.flag = flag;
    }

    @Override
    public ShortlistHiresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shortlist_item_layout, parent, false);
        ShortlistHiresViewHolder holder = new ShortlistHiresViewHolder(view);
        holder.attachOpenPerformerListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ShortlistHiresViewHolder holder, final int position) {
        holder.getNameTextView().setText(shortlists.get(position).getName());

        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(shortlists.get(position).getUID()+"/"+shortlists.get(position).getPicName());

        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_glide_placeholder)
                .error(R.drawable.ic_glide_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();
        Glide.with(context).clear(holder.getProfilePicture());
        Glide.with(context).asBitmap().apply(requestOptions).load(imageReference).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (flag == MainActivityHotel.FLAG_SHORTLIST && position < Manager.SHORTLISTED_CANDIDATES.size())
                    Manager.SHORTLISTED_CANDIDATES.get(position).setProfilePictureHigh(resource);
                else if (flag == MainActivityHotel.FLAG_HIRE && position < Manager.HIRED_CANDIDATES.size())
                    Manager.HIRED_CANDIDATES.get(position).setProfilePictureHigh(resource);
                else if (flag == MainActivityHotel.FLAG_ACCEPTED && position < Manager.ACCEPTED_SHORTLISTS.size())
                    Manager.ACCEPTED_SHORTLISTS.get(position).setProfilePictureHigh(resource);
                holder.getProfilePicture().setImageBitmap(resource);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shortlists.size();
    }

    @Override
    public void openPerformer(int positionClicked) {
        Intent intent = new Intent(context, Performer.class);
        intent.putExtra(MainActivityHotel.SHORTLIST_HIRE_INTENT_KEY, true);
        intent.putExtra(CLICKED_POSITION, positionClicked);
        intent.putExtra(SELECTED_FLAG, flag);
        Log.e("OPEN", flag + ", opem");
        context.startActivity(intent);
    }
}
