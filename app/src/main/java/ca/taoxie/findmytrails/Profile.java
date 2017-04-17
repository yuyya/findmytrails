package ca.taoxie.findmytrails;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Profile extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 0;
    private TextView person_email;
    private TextView person_username;
    ImageView imagetoUpload;

    //new new
    private Button mSelectImage;
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDiaglog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        person_email = (TextView) findViewById(R.id.textView3);
        person_username = (TextView) findViewById(R.id.username);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        person_email.setText(user.getEmail());
        person_username.setText(user.getUid());
        imagetoUpload = (ImageView) findViewById(R.id.imageToUpload);


/*
        //newest video
        if(!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        */
        mStorage = FirebaseStorage.getInstance().getReference();

        mSelectImage = (Button)findViewById(R.id.bUpLoadImage);

        mSelectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        }
    );

        mProgressDiaglog = new ProgressDialog(this);

    }




        //for the new new now
    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data){
        super.onActivityResult(requestCode, resultcode, data);
        if (requestCode == GALLERY_INTENT && resultcode == RESULT_OK && data != null){

            mProgressDiaglog.setMessage("Uploading ... ");
            mProgressDiaglog.show();
            Uri uri = data.getData();
            StorageReference filePath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Profile.this, "Upload Done.", Toast.LENGTH_LONG).show();
                    mProgressDiaglog.dismiss();
                }
            });
            imagetoUpload.setImageURI(uri);

        }
    }
}
