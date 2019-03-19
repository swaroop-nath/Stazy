package in.stazy.stazy.performerend;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class YoutubeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private YoutubeCommunication communication;

    public YoutubeViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void attachYoutubeListener(YoutubeCommunication listener) {
        communication = listener;
    }

    @Override
    public void onClick(View v) {
        communication.openYoutube(getAdapterPosition());
    }

    interface YoutubeCommunication {
        void openYoutube(int position);
    }
}
