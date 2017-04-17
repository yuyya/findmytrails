package ca.taoxie.findmytrails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;

/**
 * Created by TaoX on 2016-11-16.
 */

public class FavoriteTrailAdapter extends ArrayAdapter<Trail> {

    private List<Trail> trails;
    private ListView favorite_ListView;
    private Context context;

    public static final String TAG = "FavoriteTrailAdapter";

    private static class ViewHolder {
        TextView trail_name;
        TextView trail_location;
        ImageView rating;
        ImageView thumbnail;

    }

    public FavoriteTrailAdapter(Context context, ListView listView, List<Trail> trails) {
        super(context, R.layout.favorite_trail_sample_item, trails);
        this.trails = trails;
        this.favorite_ListView = listView;
        this.context =context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = null;
        View row = convertView;
        final FavoriteTrailAdapter.ViewHolder viewHolder;

        if (row == null) {
            viewHolder = new FavoriteTrailAdapter.ViewHolder();
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.favorite_trail_sample_item, null);
            viewHolder.trail_name = (TextView) row.findViewById(R.id.trail_name);
            viewHolder.trail_name.setText(trails.get(position).getName());
            viewHolder.trail_location = (TextView) row.findViewById(R.id.trail_location);
            viewHolder.trail_location.setText(trails.get(position).getCity() + ", " + trails.get(position).getState());
            viewHolder.rating = (ImageView)row.findViewById(R.id.fav_tri_rating) ;
            viewHolder.thumbnail= (ImageView)row.findViewById(R.id.trail_thumbnnail);


            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("trails").child(trails.get(position).getUnique_id()).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Log.e("trail_raing","current trail id :"+t.getUnique_id());
                    float totalRating = 0;
                    int total = 0;
                    Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                    while (iter.hasNext()) {
                        float f = iter.next().getValue(Float.class);
                        totalRating += (double) f;
                        total++;
                    }
                    float avg = totalRating/total;
                    Log.e(TAG,"rating:"+ avg);
                    if(avg >=5)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_5);
                    else if(avg >=4.5)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_4_half);
                    else if(avg >=4)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_4);
                    else if(avg >=3.5)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_3_half);
                    else if(avg >=3)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_3);
                    else if(avg >=2.5)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_2_half);
                    else if(avg >=2)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_2);
                    else if(avg >=0)
                        viewHolder.rating.setImageResource(R.drawable.stars_large_1_half);
                    //Log.e("trail_raing","rating is :"+ avg[0]);
                    //t.setAvg_rating(avg);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mDatabase.child("trails").child(trails.get(position).getUnique_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Log.e("trail_image","current trail id :"+t.getUnique_id());
                    Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                    if (iter.hasNext()) {
                        Image img = iter.next().getValue(Image.class);
                        Log.e("trail_image","current image url :"+img.getImgUrl());
                        String url = img.getImgUrl();

                        if(url!=null)
                            Picasso.with(context).load(url)
                                    .error(R.drawable.placeholder)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder)
                                    .into(viewHolder.thumbnail);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });








        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        this.notifyDataSetChanged();
        return row;
    }
}
