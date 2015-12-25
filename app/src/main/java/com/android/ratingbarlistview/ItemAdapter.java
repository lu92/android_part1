package com.android.ratingbarlistview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;


public class ItemAdapter extends ArrayAdapter<Item> {

    private AppCompatActivity activity;
    private List<Item> movieList;
    private ListenerActivity listenerActivity;

    private static final String PREFERENCES_NAME = "myPreferences";
    private static final String PREFERENCES_APP_NAME_FIELD = "textField";
    private final SharedPreferences sharedPreferences;

    public ItemAdapter(AppCompatActivity context, int resource, List<Item> objects, ListenerActivity listenerActivity) {
        super(context, resource, objects);
        this.activity = context;
        this.movieList = objects;
        this.listenerActivity = listenerActivity;
        this.sharedPreferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Item getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            //holder.ratingBar.getTag(position);
        }

        holder.ratingBar.setOnRatingBarChangeListener(onRatingChangedListener(holder, position));

        holder.ratingBar.setTag(position);
//        holder.ratingBar.setRating(getItem(position).getRatingStar());
        holder.ratingBar.setRating(sharedPreferences.getFloat(getItem(position).getAppName(), 0));
        holder.appName.setText(getItem(position).getAppName());
        holder.appImageView.setImageDrawable(getItem(position).getApplicationInfo().loadIcon(activity.getPackageManager()));
        holder.appImageView.setOnClickListener(onClickListener(holder, position));

        return convertView;
    }

    private RatingBar.OnRatingBarChangeListener onRatingChangedListener(final ViewHolder holder, final int position) {
        return new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Item item = getItem(position);
                item.setRatingStar(v);
                Log.i("Adapter", "star: " + v + "\t on position " + position);
                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                preferencesEditor.putFloat(item.getAppName(), item.getRatingStar());
                preferencesEditor.commit();
            }
        };
    }

    private View.OnClickListener onClickListener(final ViewHolder holder, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Adapter", "imageView on position " + position);
                listenerActivity.runSelectedApp(position);
            }
        };
    }

    private static class ViewHolder {
        private RatingBar ratingBar;
        private TextView appName;
        private ImageView appImageView;

        public ViewHolder(View view) {
            ratingBar = (RatingBar) view.findViewById(R.id.app_rating);
            appName = (TextView) view.findViewById(R.id.app_name);
            appImageView = (ImageView) view.findViewById(R.id.app_icon);
        }
    }
}
