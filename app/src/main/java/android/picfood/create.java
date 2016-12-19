package android.picfood;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by USER on 2016/12/19.
 */
public class create extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);
        //font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NanumBrushScript-Regular.ttf");

        TextView topic = (TextView)findViewById(R.id.topic);
        TextView topic2 = (TextView)findViewById(R.id.topic2);
        topic.setTypeface(font);
        topic2.setTypeface(font);


        //font end

    }
}
