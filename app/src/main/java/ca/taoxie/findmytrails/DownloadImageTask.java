package ca.taoxie.findmytrails;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by TaoX on 2016-10-23.
 */

public class DownloadImageTask extends AsyncTask<Trail, Void, List<Image>> {

    //public AsyncImagesResponse delegate = null;
    private List<ImageView> imageViews;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private DatabaseReference mDatabase;
    private List<Image> images;
    private boolean isFinished = false;
    private View view;
    private List<Bitmap> bitmaps;
    private LinearLayout mGallery;
    private LayoutInflater mInflater;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private static final String STORAGE_URL = "gs://findmytrails.appspot.com";
    private static final String TAG = "DownloadImageTask";


    @Override
    protected List<Image> doInBackground(Trail... params) {

        String trialId = params[0].getUnique_id();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("trails").child(trialId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                images = new ArrayList<>();
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    Image img = iter.next().getValue(Image.class);
                    images.add(img);
                }


                recyclerView = (RecyclerView) mainActivity.findViewById(R.id.gallery2);
                recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
                GalleryRecycleViewAdapter galleryRecycleViewAdapter = new GalleryRecycleViewAdapter(images, mainActivity);
                recyclerView.setAdapter(galleryRecycleViewAdapter);

                isFinished = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        while (!isFinished) {
        }
        return images;
    }

    @Override
    protected void onPostExecute(List<Image> images) {
        Log.e(TAG, "results" + images);
        //delegate.processFinish(images);
    }

    public void downloadImages(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        execute(mainActivity.currentTrail);
    }


}
