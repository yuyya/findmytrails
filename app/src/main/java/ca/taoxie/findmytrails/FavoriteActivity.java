package ca.taoxie.findmytrails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
 * Created by TaoX on 2016-11-16.
 */

public class FavoriteActivity extends AppCompatActivity {

    private ListView favorite_ListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        favorite_ListView = (ListView) findViewById(R.id.favorite_ListView);
        final List<Trail> trails = new ArrayList<>();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = mDatabase.child("users").child(user.getUid()).child("favorite");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    final Trail t = iter.next().getValue(Trail.class);
                    trails.add(t);
                }
                FavoriteTrailAdapter favoriteTrailAdapter = new FavoriteTrailAdapter(getApplicationContext(), favorite_ListView, trails);
                favorite_ListView.setAdapter(favoriteTrailAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        favorite_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
