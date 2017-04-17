package ca.taoxie.findmytrails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by TaoX on 2016-10-29.
 */

public class WriteCommentActivity extends AppCompatActivity {

    private EditText write_comment_editText;
    private Button submit_button;
    private Button cancel_write_button;
    private String trailId = "";
    private FirebaseUser user;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        user = FirebaseAuth.getInstance().getCurrentUser();
        trailId = getIntent().getExtras().getString("trailId");
        write_comment_editText = (EditText) findViewById(R.id.write_comment_editText);
        submit_button = (Button) findViewById(R.id.submit_button);
        cancel_write_button = (Button) findViewById(R.id.cancel_write_button);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = write_comment_editText.getText().toString();
                BadWordFilter badWordFilter = new BadWordFilter(comment);
                String finalComment = badWordFilter.wordCrunch();
                if(ratingBar.getRating()!=0){
                    submitRatingToFirebase(ratingBar.getRating());
                }
                if (comment != null && comment.length() > 0) {
                    Comment c = new Comment(finalComment, trailId, user.getEmail());
                    FirebaseQuery firebaseQuery = new FirebaseQuery();
                    firebaseQuery.addComment(c);
                    //finish();
                }
                if(ratingBar.getRating()!=0||(comment != null && comment.length() > 0))
                    finish();
            }
        });
        cancel_write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void submitRatingToFirebase(float rating) {
        FirebaseQuery firebaseQuery = new FirebaseQuery();
        firebaseQuery.setRating(trailId, rating);

    }
}
