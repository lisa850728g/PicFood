package android.picfood;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    FirebaseAuth auth_log;
    FirebaseAuth.AuthStateListener authListener;
    String userUID;
    private InputMethodManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //
        auth_log = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(
                    @NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null) {
                    Log.d("onAuthStateChanged", "登入:" + user.getUid());
                    userUID =  user.getUid();
                }
            }
        };

        //font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NanumBrushScript-Regular.ttf");

        TextView topic = (TextView)findViewById(R.id.topic);
        TextView topic2 = (TextView)findViewById(R.id.topic2);
        topic.setTypeface(font);
        topic2.setTypeface(font);

        Button btn=(Button) findViewById(R.id.button);

        btn.setTypeface(font);

        //font end

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = ((EditText) findViewById(R.id.account)).getText().toString();
                final String password = ((EditText) findViewById(R.id.password)).getText().toString();
                Log.d("AUTH", account + "/" + password);
                if (account.equals("") || password.equals("")) {
                    Toast.makeText(login.this, "Please enter email and password.", Toast.LENGTH_LONG).show();
                }else{
                    auth_log.signInWithEmailAndPassword(account, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(login.this, "Access", Toast.LENGTH_LONG).show();
                                    Intent toShow = new Intent();
                                    toShow.setClass(login.this,show_in_square.class);
                                    startActivity(toShow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FirebaseUser user = auth_log.getCurrentUser();
                                    if (account.equals(user.getEmail().trim())) {
                                        new AlertDialog.Builder(login.this)
                                                .setMessage("Wrong password")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    } else {
                                        new AlertDialog.Builder(login.this)
                                                .setMessage("This email doesn't exist.Please sign up!")
                                                .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent turnToCreate = new Intent();
                                                        turnToCreate.setClass(login.this,create_user.class);
                                                        startActivity(turnToCreate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth_log.addAuthStateListener(authListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        auth_log.removeAuthStateListener(authListener);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }
}