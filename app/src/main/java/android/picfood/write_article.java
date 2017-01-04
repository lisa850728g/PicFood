package android.picfood;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class write_article extends AppCompatActivity{
    Uri imageUri;
    ImageView image;
    Button release;
    String product;
    String store;
    String comment;
    StorageReference imaStorage;
    private final static int CAMERA = 66 ;
    private final static int PHOTO = 99 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_article);
        image = (ImageView) findViewById(R.id.imageView);
        release = (Button) findViewById(R.id.button_release);
        imaStorage = FirebaseStorage.getInstance().getReference();

        //選取圖片來源，手機相簿或拍照
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(write_article.this)
                        .setMessage("Get the food's photo")
                        .setPositiveButton("camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //open the phone's camera
                                try{
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, CAMERA);
                                } catch (ActivityNotFoundException anfe) {
                                    Toast.makeText(write_article.this, "This device doesn't support the camera action!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //open the phone's gallery
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, PHOTO);
                            }
                        })
                        .show();
            }
        });

        //按下發佈按鈕
        release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirebase();
            }
        });
    }

    public void saveToFirebase() {
        product = ((EditText) findViewById(R.id.product)).getText().toString();
        store = ((EditText) findViewById(R.id.store)).getText().toString();
        comment = ((EditText) findViewById(R.id.comment)).getText().toString();
        StorageReference picPath = imaStorage.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(imageUri.getLastPathSegment());
        picPath.putFile(imageUri)
                //圖片上傳成功
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //確認使用者將資訊完整輸入完畢
                        if(product.equals("") || store.equals("") || comment.equals(""))
                            Toast.makeText(write_article.this, "Please fill in all blanks",Toast.LENGTH_SHORT).show();
                        else{
                            Uri picUri = taskSnapshot.getDownloadUrl();
                            Toast.makeText(write_article.this, "Success",Toast.LENGTH_SHORT).show();
                            //將資訊上傳至database
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("pics").push();
                            ref.child("imageUri").setValue(picUri.toString());
                            ref.child("product").setValue(product);
                            ref.child("store").setValue(store);
                            ref.child("comment").setValue(comment);
                            Intent ToShow = new Intent();
                            ToShow.setClass(write_article.this,show_in_square.class);
                            startActivity(ToShow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    }
                })
                //圖片上傳失敗
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("class.name",e.getMessage());
                        Toast.makeText(write_article.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //拍照或從相簿選取後執行
            if((requestCode == CAMERA || requestCode == PHOTO ) && data != null) {
                imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }
            //將裁切完之圖片，放入imageView
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri= result.getUri();
                Picasso.with(getApplicationContext()).load(imageUri).into(image);
            }
        }
    }
}
