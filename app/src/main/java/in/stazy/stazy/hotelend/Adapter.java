package in.stazy.stazy.hotelend;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import in.stazy.stazy.R;

public class Adapter extends PagerAdapter {

    private ArrayList<ExploreCardModel> models;
    private LayoutInflater inflater;
    private Context context;
    private ActivityCommunication communication;

    public Adapter(ArrayList<ExploreCardModel> models, Context context) {
        this.models = models;
        this.context = context;
        communication = (ActivityCommunication) context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int pos = position;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_pager_item, container, false);
        ImageView imageView = view.findViewById(R.id.view_pager_item_image_view);

        imageView.setImageResource(models.get(position).getImage());

        container.addView(view, 0);
        Log.e("ADAPTER","called");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HirePerformer.class);
                switch (pos) {
                    case 0:
                        //Musicians clicked
                        intent.putExtra(MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY, MainActivityHotel.TYPE_VALUE_MUCISIANS);
                        break;
                    case 1:
                        //Comedians clicked
                        intent.putExtra(MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY, MainActivityHotel.TYPE_VALUE_COMEDIANS);
                        break;
                    case 2:
                        //Others clicked
                        intent.putExtra(MainActivityHotel.EXPLORE_INTENT_EXTRA_KEY, MainActivityHotel.TYPE_VALUE_OTHERS);
                        break;
                }
                communication.onViewPagerClick(intent);
            }
        });

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    interface ActivityCommunication {
        void onViewPagerClick(Intent intent);
    }
}

