package com.visanka.news;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.visanka.news.constants.ServerConstants;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.UUID;

public class updateProfilepic extends AppCompatActivity {
    ImageView ProfilePic;
    Button uploadBtn,choosePicbtn;
    private static final int STORAGE_PERMISSION_CODE = 4665;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filepath;
    private Bitmap bitmap;
    private String path;
    private String mediaPath;
    SharedPreferences sharedPreferences;
    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profilepic);
        ProfilePic = findViewById(R.id.imageView2);
        uploadBtn = findViewById(R.id.uploadbtn);
        choosePicbtn = findViewById(R.id.choosePicbtn);
        sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(Pref_Username,"");
        password = sharedPreferences.getString(Pref_Password,"");

        if(userProfile.profilePic_url != null)
            Glide.with(this).load(userProfile.profilePic_url).into(ProfilePic);


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFiles();
            }
        });
    }
    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ShowFIleChooser();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();
                ShowFIleChooser();

            }else {
                Toast.makeText(getApplicationContext(),"Permisssion Declined",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this,MainActivity.class));
//                finish();
            }
        }
    }
    public  void selectImage(View view){
        requestStoragePermission();

    }

    private void ShowFIleChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Picture"),PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data!=null && data.getData() !=null){
            filepath = data.getData();
            Log.d("UPDATEPROFILEPIC","File path -> "+filepath.toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
//                ProfilePic.setImageBitmap(Util.getResizedBitmap(bitmap,300));
                Glide.with(ProfilePic.getContext()).load(filepath).into(ProfilePic);
//                pathOfImage.setText(filepath.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    private void uploadFiles(){
        Log.d("UPDATEPROFILEPIC","filepath before getPath() -> "+filepath);
        mediaPath = getPath(filepath);
        Log.d("UPDATEPROFILEPIC","mediapath after getPath() -> "+mediaPath);
        try{

            File f = new File(String.valueOf(Uri.parse(mediaPath)));
            Log.d("UPDATEPROFILEPIC",String.valueOf(f.isFile()));

            String uploadId = UUID.randomUUID().toString();
            MultipartUploadRequest request = new MultipartUploadRequest(getApplicationContext(),uploadId, ServerConstants.SERVER_BASE_URL +"profilePic.php")
                    .setMethod("POST")
                    .addFileToUpload(mediaPath,"upload").addParameter("username",username);

            Log.d("UPDATEPROFILEPIC",String.valueOf(f.isFile()));
            request.startUpload();
            Log.d("UPDATEPROFILEPIC",String.valueOf(f.isFile()));
            Toast.makeText(getApplicationContext(),"Uploaded successfully..",Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getApplicationContext(),userProfile.class);
            startActivity(i);
            finish();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private String getPath(Uri uri) {
        String[] proj = new String[]{

                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATA,



        };
//
//        new String[]{document_id}

        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        cursor.moveToFirst();
        Log.d("UPDATEPROFILEPIC","count -> "+cursor.getCount());
        Log.d("UPDATEPROFILEPIC","col names -> "+ Arrays.toString(cursor.getColumnNames()));

        int name_id  = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
        String display_name = cursor.getString(name_id);

//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);

        Log.d("UPDATEPROFILEPIC","Dis_name -> "+display_name);

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,MediaStore.Images.Media.DISPLAY_NAME + "=?", new String[]{display_name}, null
        );


//        MediaStore.Images.Media._ID + "=?"
//        new String[]{document_id}

//        while(cursor.moveToNext()){
////            int id_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
//            int data_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//
////            Log.d("UPDATEPROFILEPIC","id index -> "+id_index);
////            Log.d("UPDATEPROFILEPIC","ID -> "+ cursor.getInt(id_index));
//            Log.d("UPDATEPROFILEPIC","dat index -> "+data_index);
//            Log.d("UPDATEPROFILEPIC","DATA -> "+cursor.getString(data_index));
//
//        }
        Log.d("UPDATEPROFILEPIC","path----> "+path);

        if(cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        }
        Log.d("UPDATEPROFILEPIC","path----> "+path);
        cursor.close();
        return path;
    }

}