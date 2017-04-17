package ca.taoxie.findmytrails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static ca.taoxie.findmytrails.SignInActivity.GET_NEW_USERNAME_FROM_SIGNUP;

/**
 * Created by TaoX on 2016-09-24.
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText email_editText;
    private EditText password_editText;
    private EditText password_again_editText;
    private Button sign_me_up;
    private Button signIn_button;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_editText = (EditText) findViewById(R.id.email_editText);
        password_editText = (EditText) findViewById(R.id.password_editText);

        sign_me_up = (Button) findViewById(R.id.sign_me_up);
        signIn_button = (Button) findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();

        sign_me_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_editText.getText().toString();
                String pass = password_editText.getText().toString();

                signUp(email, pass);
            }
        });

        signIn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                startActivityForResult(intent, GET_NEW_USERNAME_FROM_SIGNUP);
                finish();
            }
        });
    }

    private void signUp(final String email, String pass) {
        if (email == null || email.equals(""))
            Toast.makeText(getApplicationContext(), "Email address cannot be empty", Toast.LENGTH_SHORT).show();

        else {
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Sign up failed. password length < 6 or duplicated email", Toast.LENGTH_SHORT).show();
                }else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("newUserEmail", email);
                    setResult(Activity.RESULT_OK, returnIntent);
                    database = FirebaseDatabase.getInstance();
                    String currentUserUid = task.getResult().getUser().getUid();
                    String currentUserEmail = task.getResult().getUser().getEmail();
                    myRef = database.getReference().child("users").child(currentUserUid).push();
                    myRef.setValue(currentUserUid);
                    myRef = database.getReference().child("users").child(currentUserUid).push();
                    myRef.setValue(currentUserEmail);
                    finish();
                }
            }});
        }
    }


}
