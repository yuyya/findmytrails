package ca.taoxie.findmytrails;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by TaoX on 2016-10-29.
 */

public class FirebaseQuery {

    private DatabaseReference mDatabase;
    private List<Comment> cachedComments;

    private static final String TAG = "FirebaseQuery";

    public FirebaseQuery() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addComment(Comment comment) {
        DatabaseReference myRef = mDatabase.child("trails").child(comment.getTrailId()).child("comments").push();
        myRef.setValue(comment);
    }

    public List<Comment> getComments(String trailId) {

        cachedComments = new ArrayList<>();
        DatabaseReference myRef = mDatabase.child("trails").child(trailId).child("comments");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e(TAG, "" + dataSnapshot);
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    Comment c = iter.next().getValue(Comment.class);
                    cachedComments.add(c);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return cachedComments;
    }



    public void setRating(String trailId, float rating)
    {
        DatabaseReference myRef = mDatabase.child("trails").child(trailId).child("rating").push();
        myRef.setValue(rating);
    }

    public void getRating(String trailId) {

        DatabaseReference myRef = mDatabase.child("trails").child(trailId).child("rating");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int total = 0;
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    float f = iter.next().getValue(Float.class);
                    totalRating += (double) f;
                    total++;
                }
                float avg = totalRating/total;
                Log.e(TAG, "rating = " + avg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setFavoriteToTrue(Trail trail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = mDatabase.child("users").child(user.getUid()).child("favorite").push();
        myRef.setValue(trail);
    }

    public void setFavoriteToFalse(final Trail trail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = mDatabase.child("users").child(user.getUid()).child("favorite");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    DataSnapshot temp = iter.next();
                    Trail t = temp.getValue(Trail.class);
                    if (t.getUnique_id().equals(trail.getUnique_id()))
                        temp.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
