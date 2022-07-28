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
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.visanka.news.constants.ServerConstants;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.UUID;

public class addFile extends AppCompatActivity {

    ImageView imageView;
    TextInputEditText description;
    private Uri filepath=null;
    private static final int STORAGE_PERMISSION_CODE = 4665;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    Button uploadBtn;
    SharedPreferences sharedPreferences;
    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
    private String username;
    private String password;
    private String mediaPath;
    private boolean descriptionFlag = false;
    TextView pathOfImage;
    private String path;
//    http://10.0.2.2/newsapp/imageUpload.php
    private String apiUrl = ServerConstants.SERVER_BASE_URL +"imageUpload.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);

        requestStoragePermission();
        imageView = findViewById(R.id.imageView);
        uploadBtn = findViewById(R.id.uploadbtn);
        description = findViewById(R.id.textInputEditText);
        sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(Pref_Username,"");
        password = sharedPreferences.getString(Pref_Password,"");

        if(!sharedPreferences.contains(Pref_Username)){
            startActivity(new Intent(this,login.class));
            finish();
        }

        boolean photo_selected = false;

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Log.d("ADDFILE","89: "+description.getText().toString());

                    if(filepath == null && description.getText().toString().equals("")){
                        Log.d("ADDFILE","BOTH EMPTY");
                        Toast.makeText(getApplicationContext(),"please add some content",Toast.LENGTH_SHORT).show();
                    }
                    else if(filepath == null ){

                        if(description.getText().toString().length() <=150){
                            Log.d("ADDFILE","FILE EMPTY AND DESC NOT EMPTY");
                            uploadOnlyDescription();

                        }else{
                            Log.d("ADDFILE","FILE EMPTY AND DESC LIMIT REACHED");
                            Toast.makeText(getApplicationContext(), "description limit reached", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(filepath != null && !description.getText().toString().equals("")) {
                        if ((description.getText().toString().length()> 150)) {
                            Log.d("ADDFILE","FILE NOT EMPTY AND DESC LIMIT REACHED");
                            Toast.makeText(getApplicationContext(), "description limit reached", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("ADDFILE","FILE NOT EMPTY AND DESC NOT EMPTY AND UNDER LIMIT");
                            descriptionFlag = true;
                            uploadFiles();

                        }
                    }
                    else if(filepath != null && description.getText().toString().equals("")){
                        Log.d("ADDFILE","FILE NOT EMPTY AND DESC EMPTY");
                        uploadFiles();

                    }


            }
        });

    }



    private void updateDescription(){
        try {

            Handler handler = new Handler(Looper.myLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[3];
                    field[0] = "username";
                    field[1] = "DESC";
                    field[2] = "IMG_NAME";

                    String[] data = new String[3];
                    data[0] = username;
                    data[1] = description.getText().toString();
                    data[2] = new File(mediaPath).getName();
                    Log.d("ADDFILE","MEDIAPATH "+ Arrays.toString(data));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.d("ADDFILE","error catched");
                        e.printStackTrace();
                    }
                    PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"updateDescription.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Log.d("ADDFILE",result);
                        if(result.equals("upload successful")){
//
                           Toast.makeText(getApplicationContext(),"description upload success",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"description upload failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadOnlyDescription(){
        try {

            Handler handler = new Handler(Looper.myLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "username";
                    field[1] = "DESC";

                    String[] data = new String[2];
                    data[0] = username;
                    data[1] = description.getText().toString();

                    PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"uploadOnlyDescription.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        String result = putData.getResult();

                        if(result.equals("upload successful")){
                            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"upload failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getApplicationContext(),"Permisssion Declined",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this,MainActivity.class));
                finish();
            }
        }
    }
    private void ShowFIleChooser(){
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
            Log.d("ADDFILE","123:"+filepath.toString());

            Glide.with(imageView.getContext()).load(filepath).into(imageView);


        }
    }

    public  void selectImage(View view){
        ShowFIleChooser();
    }

    private void uploadFiles(){
        Log.d("ADDFILE",filepath.toString());
        if(filepath!=null) {
            mediaPath = getPath(filepath);
            Log.d("ADDFILE",mediaPath);
            try {
                String uploadId = UUID.randomUUID().toString();

                new MultipartUploadRequest(this, uploadId, ServerConstants.SERVER_BASE_URL + "userMedia.php")
                        .addFileToUpload(mediaPath, "upload").addParameter("username", username)
                        .startUpload();
                Toast.makeText(getApplicationContext(), "Uploaded successfully..", Toast.LENGTH_SHORT).show();
                uploadFilesToMediaFiles();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }



    }
    private void uploadFilesToMediaFiles(){
        mediaPath = getPath(filepath);

        try{
            String uploadId = UUID.randomUUID().toString();
            MultipartUploadRequest mp = new MultipartUploadRequest(this,uploadId,ServerConstants.SERVER_BASE_URL+"uploadInMediaFiles.php")
                    .addFileToUpload(mediaPath,"upload").addParameter("username",username);

            mp.startUpload();


            Toast.makeText(getApplicationContext(),"Uploaded To Media successfully..",Toast.LENGTH_SHORT).show();



            if(descriptionFlag) {
                Log.d("ADDFILE","description flag checked true");
                updateDescription();
                descriptionFlag = false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null
        );

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}