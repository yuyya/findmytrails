package ca.taoxie.findmytrails;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

  private FirebaseAuth mAuth;
  private EditText email_editText;
  private EditText password_editText;
  private Button signIn_button;
  private Button signUp_button;
  private FirebaseAuth.AuthStateListener mAuthListener;
  protected String currentUserEmail = "";
  protected String currentUserUid;

  protected static final int GET_NEW_USERNAME_FROM_SIGNUP = 0;
  private static final String TAG = "SignInActivity";

  @Override
  protected void onResume() {
    super.onResume();
    //password_editText.setText("");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    mAuth = FirebaseAuth.getInstance();
    email_editText = (EditText) findViewById(R.id.email_editText);
    password_editText = (EditText) findViewById(R.id.password_editText);
    signIn_button = (Button) findViewById(R.id.signIn_button);
    signUp_button = (Button) findViewById(R.id.signUp_button);
    //password_editText.setText("");

    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
          currentUserEmail = user.getEmail();
          currentUserUid = user.getUid();
        } else {
          Log.d(TAG, "onAuthStateChanged:signed_out");
        }
      }
    };

    signIn_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = email_editText.getText().toString();
        String password = password_editText.getText().toString();
        if (email != null && password != null && !email.equals("") && !password.equals(""))
          signIn(email, password);
        else
          Toast.makeText(getApplicationContext(), "Incomplete user info", Toast.LENGTH_LONG).show();
      }
    });

    signUp_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
        startActivityForResult(intent, GET_NEW_USERNAME_FROM_SIGNUP);
        finish();
      }
    });

  }

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  private void signIn(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
        if (!task.isSuccessful()) {
          Toast.makeText(getApplicationContext(), "Incorrect credential entered", Toast.LENGTH_SHORT).show();
          Log.w(TAG, "signInWithEmail:failed", task.getException());
        } else {
          Intent intent = new Intent(getBaseContext(), MainActivity.class);
          intent.putExtra("currentUser", currentUserEmail);
          intent.putExtra("currentUserUid", currentUserUid);
          startActivity(intent);
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == GET_NEW_USERNAME_FROM_SIGNUP) {
      if(resultCode == Activity.RESULT_OK){
        String newUserEmail = data.getStringExtra("newUserEmail");
        email_editText.setText(newUserEmail);
        password_editText.requestFocus();
      }
      if (resultCode == Activity.RESULT_CANCELED) {
        email_editText.setText(email_editText.getText().toString());
        password_editText.setText("");
      }
    }
  }

}
