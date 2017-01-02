package android.picfood;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class write_article extends AppCompatActivity{
    private StorageReference imaStorage;
    ProgressDialog imgProgress;
    ImageView image;
    Uri imageUri;
    Bundle extras;
    Bitmap bmPic;
    Button release;
    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final static int CAMERA = 66 ;
    private final static int PHOTO = 99 ;
    private final int CROP_PIC = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_article);
        imaStorage = FirebaseStorage.getInstance().getReference(user);
        imgProgress = new ProgressDialog(this);
        image = (ImageView) findViewById(R.id.imageView);
        release =(Button) findViewById(R.id.button_release);

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

        release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirebase(bmPic);
            }
        });
    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if((requestCode == CAMERA || requestCode == PHOTO ) && data != null) {
                imageUri = data.getData();
                performCrop();
            }
            else if(requestCode == CROP_PIC){
                // get the returned data
                extras = data.getExtras();
                // get the cropped bitmap
                bmPic = extras.getParcelable("data");
                image.setImageBitmap(bmPic);
            }
        }
    }

    public void saveToFirebase(Bitmap bitmapPic) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapPic.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        String product = ((EditText) findViewById(R.id.product)).getText().toString();
        String store = ((EditText) findViewById(R.id.store)).getText().toString();
        String comment = ((EditText) findViewById(R.id.comment)).getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(user)
                .child("pics")
                .push();
        ref.child("imageUri").setValue(imageEncoded);
        ref.child("product").setValue(product);
        ref.child("store").setValue(store);
        ref.child("comment").setValue(comment);
    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(imageUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("outputFormat","JPEG");
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            String localTempImgDir="Hello";

            String localTempImgFileName = System.currentTimeMillis()+".jpg";
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            File f=new File(Environment.getExternalStorageDirectory() +"/"+localTempImgDir+"/"+localTempImgFileName);
            imageUri = Uri.fromFile(f);

            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException noAction) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }
}
