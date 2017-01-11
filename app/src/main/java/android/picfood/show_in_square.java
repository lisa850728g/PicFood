package android.picfood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class show_in_square extends AppCompatActivity{
    private ListView mListView;
    private MyAdapter mAdapter;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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

        dbRef.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String showName = (String) dataSnapshot.getValue();
                t.setText("Hello !   "+showName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(show_in_square.this)
                        .setTitle("want to delele?")
                        .setMessage("Want to delete " + position + " item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.removeItem(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return false;
            }
        });
    }

    private ArrayList<pics> list = new ArrayList<pics>();

    private void setupFirebase() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("news");
        dbRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                pics n = new pics();
                n.setProduct((String) dataSnapshot.child("product").getValue());
                n.setImageUri((String) dataSnapshot.child("imageUri").getValue());
                n.setStore((String) dataSnapshot.child("store").getValue());

                list.add(0,n);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        private ArrayList<Integer> mList;

        public MyAdapter(){
            mList = new ArrayList<>();
        }

        public void addItem(Integer i){
            mList.add(i);
        }

        public void removeItem(int index){
            mList.remove(index);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            MyAdapter.Holder holder;
            if(v == null){
                v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pics, null);
                holder = new MyAdapter.Holder();
                holder.product = (TextView) v.findViewById(R.id.share_product);
                holder.store = (TextView) v.findViewById(R.id.share_store);
                v.setTag(holder);
            } else{
                holder = (MyAdapter.Holder) v.getTag();
            }

            holder.product.setText("item" + position);
            /*pics p = new pics();

            holder.product.setText(p.getProduct());
            holder.store.setText(p.getStore());*/

            return v;
        }
        class Holder{
            TextView product;
            TextView store;
            ImageView image;
        }
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
            case R.id.add://add item
                mAdapter.addItem(mAdapter.getCount() + 1);
                mAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
