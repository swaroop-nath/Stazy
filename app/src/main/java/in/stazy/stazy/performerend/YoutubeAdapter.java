package in.stazy.stazy.performerend;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.stazy.stazy.R;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeViewHolder> implements YoutubeViewHolder.YoutubeCommunication {
    private String[] links;
    private Context context;

    public YoutubeAdapter(String[] links, Context context) {
        this.links = links;
        this.context = context;
    }

    @Override
    public YoutubeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.youtube_links_item, parent, false);
        YoutubeViewHolder holder = new YoutubeViewHolder(layout);
        holder.attachYoutubeListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(YoutubeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return links.length;
    }

    @Override
    public void openYoutube(int position) {
        //Write code to open youtube intent
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + links[position]));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://youtube.com/watch?v=" + links[position]));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
