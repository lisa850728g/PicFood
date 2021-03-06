package android.picfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class show_in_square extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseRecyclerAdapter<pics, PhotoViewHolder> adapter;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
    LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_in_square);

        /*字型設定*/
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NanumBrushScript-Regular.ttf");

        TextView textView = (TextView)findViewById(R.id.textView);
        TextView topic = (TextView)findViewById(R.id.topic);
        TextView topic2 = (TextView)findViewById(R.id.topic2);
        topic.setTypeface(font);
        topic2.setTypeface(font);
        textView.setTypeface(font);

        final TextView t = (TextView)findViewById(R.id.textView);

        /*取得使用者名稱，並顯示*/
        dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String showName = (String) dataSnapshot.getValue();
                t.setText("Hello !   "+showName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView= (RecyclerView) findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        //讓最新資料從上方進入
        mLayoutManager.setReverseLayout(true);
        //Recycler view從頂部開始顯示
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        DatabaseReference picRef = FirebaseDatabase.getInstance().getReference("profiles");
        picRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                    pics photo = msgSnapshot.getValue(pics.class);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Photo", "failed: " + databaseError.getMessage());
            }
        });

        adapter = new FirebaseRecyclerAdapter<pics, PhotoViewHolder>(pics.class, R.layout.pics, PhotoViewHolder.class, picRef) {
            @Override
            protected void populateViewHolder(PhotoViewHolder viewHolder, pics model, int position) {
                viewHolder.setPhoto(model);//將資料傳入
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users").child(user.getUid());
            ref.child("Email").setValue(user.getEmail());
            ref.child("Name").setValue(user.getDisplayName());
        } else {
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView content;

        /*設定資料放置位置*/
        public PhotoViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.share_image);
            title = (TextView) itemView.findViewById(R.id.share_product);
            content = (TextView) itemView.findViewById(R.id.share_store);
        }

        /*將資料放入*/
        public void setPhoto(pics photo) {
            title.setText(photo.getProduct());
            content.setText(photo.getStore());
            Glide.with(image.getContext())
                    .load(photo.getImageUri())
                    .into(image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.show_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        /*handle presses on the action bar items*/
        switch(item.getItemId()){
            //按下加號
            case R.id.write:
                startActivity(new Intent(this,write_article.class));
                return true;
            //按下門
            case R.id.signOut:
                new AlertDialog.Builder(show_in_square.this)
                        .setMessage("Want to sign out?")
                        .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent turnToCreate = new Intent();
                                        turnToCreate.setClass(show_in_square.this, login.class);
                                        startActivity(turnToCreate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    }
                                }

                        )
                        .setNeutralButton("Cancel", null)
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
