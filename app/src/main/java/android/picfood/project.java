package android.picfood;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class project extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NanumBrushScript-Regular.ttf");

        TextView topic = (TextView)findViewById(R.id.topic);
        TextView topic2 = (TextView)findViewById(R.id.topic2);
        topic.setTypeface(font);
        topic2.setTypeface(font);

        Button login=(Button) findViewById(R.id.login);
        Button fb=(Button) findViewById(R.id.guest);
        login.setTypeface(font);
        fb.setTypeface(font);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(project.this,p2.class);
                startActivity(intent);
            }
        });
    }


}
