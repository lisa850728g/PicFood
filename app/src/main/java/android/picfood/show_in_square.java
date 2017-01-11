package android.picfood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class show_in_square extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_in_square);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NanumBrushScript-Regular.ttf");

        TextView textView = (TextView)findViewById(R.id.textView);
        TextView topic = (TextView)findViewById(R.id.topic);
        TextView topic2 = (TextView)findViewById(R.id.topic2);
        topic.setTypeface(font);
        topic2.setTypeface(font);
        textView.setTypeface(font);

        final TextView t = (TextView)findViewById(R.id.textView);

        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String showName = (String) dataSnapshot.getValue();
                        t.setText("Hello !   "+showName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.show_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //handle presses on the action bar items
        switch(item.getItemId()){
            case R.id.write:
                startActivity(new Intent(this,write_article.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
