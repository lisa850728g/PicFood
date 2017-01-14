package android.picfood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /*字型設定*/
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NanumBrushScript-Regular.ttf");

        TextView topic = (TextView)findViewById(R.id.topic);
        TextView topic2 = (TextView)findViewById(R.id.topic2);
        TextView name = (TextView)findViewById(R.id.name);

        topic.setTypeface(font);
        topic2.setTypeface(font);
        name.setTypeface(font);

        Button logIn=(Button) findViewById(R.id.login);
        Button signUp=(Button) findViewById(R.id.guest);
        logIn.setTypeface(font);
        signUp.setTypeface(font);

        /*跳至log in頁面*/
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ToLogIn = new Intent();
                ToLogIn.setClass(main.this,login.class);
                startActivity(ToLogIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        /*跳至sign up頁面*/
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ToCreateUser = new Intent();
                ToCreateUser.setClass(main.this,create_user.class);
                startActivity(ToCreateUser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }
}
