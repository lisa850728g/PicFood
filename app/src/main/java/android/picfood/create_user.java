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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class create_user extends AppCompatActivity {
    FirebaseAuth auth_sign;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
    private InputMethodManager manager;
    String name = "";
    String email = "";
    String pw = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);
        auth_sign = FirebaseAuth.getInstance();
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NanumBrushScript-Regular.ttf");
        TextView topic = (TextView) findViewById(R.id.topic);
        TextView topic2 = (TextView) findViewById(R.id.topic2);
        topic.setTypeface(font);
        topic2.setTypeface(font);
        //font end

        Button signEnter = (Button) findViewById(R.id.signEnter);
        signEnter.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = ((EditText) findViewById(R.id.name)).getText().toString();
                email = ((EditText) findViewById(R.id.email)).getText().toString();
                pw = ((EditText) findViewById(R.id.pw)).getText().toString();
                String checkPw = ((EditText) findViewById(R.id.checkpw)).getText().toString();
                if (name.equals("") || email.equals("") || pw.equals("") || checkPw.equals("")) {
                    Toast.makeText(create_user.this, "Please fill in all spaces.", Toast.LENGTH_LONG).show();
                } else {
                    if (!checkPw.equals(pw)) {
                        Toast.makeText(create_user.this, "Please recheck the passwords.", Toast.LENGTH_LONG).show();
                    } else if (pw.length() < 6) {
                        Toast.makeText(create_user.this, "Password must be upper than 6.", Toast.LENGTH_LONG).show();
                    } else {
                        upInfo(name,email,pw);
                    }
                }
            }
        });

    }

    private void upInfo(final String name, final String email, final String pw) {
        myRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(create_user.this, "This name is already been used.", Toast.LENGTH_LONG).show();
                } else {
                    createUser(name, email, pw);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createUser(final String name, final String email, String password) {
        auth_sign.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("create.class", "createUser onCreate");
                                if (task.isSuccessful()) {
                                    new AlertDialog.Builder(create_user.this)
                                            .setTitle("ACCESS")
                                            .setMessage("Please sign in with this email")
                                            .setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent turnToLogIn = new Intent();
                                                            turnToLogIn.setClass(create_user.this, login.class);
                                                            startActivity(turnToLogIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                        }
                                                    })
                                            .show();
                                    myRef.child(name).child("name").setValue(name);
                                    myRef.child(name).child("email").setValue(email);
                                } else {
                                    if (!isEmailValid(email))
                                        Toast.makeText(create_user.this, "Invalid email address", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("create.class", e.getMessage());
                        Toast.makeText(create_user.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
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
