package com.visanka.news;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.visanka.news.Models.postDataModel;
import com.example.newsapp.R;
import com.visanka.news.constants.ServerConstants;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
//172.20.10.3
    private static final int CONTACT_PERMISSION_CODE = 1;

    private static final String apiUrl = ServerConstants.SERVER_BASE_URL+"imgForHome.php";
    ImageView HomePage,ProfilePage,AddFile,img01;
    private String username,password;
    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
    public Context context;

    RecyclerView lv;
    private ArrayList<postDataModel> postDataModels = new ArrayList<>();

//    private static String user_name[];
//    private  static String img[];
//    private static String img_name[];
//    private static String img_desc[];

    public ArrayList<String> likeCache = new ArrayList<>();
    public ArrayList<String> likeCacheDesc = new ArrayList<>();
    public  static  HashMap<String,Integer> likeCountCacheDesc = new HashMap<>();
    public static HashMap<String,Integer> likeCountCache = new HashMap<>();
    private String likeStatus = null;





    private void getContacts() throws JSONException {

        //check permissions for contacs
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},CONTACT_PERMISSION_CODE);
        }

        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri,null,"",null,null);




        if(cursor.getCount() > 0){

            int i=0;
            String[] data;
            String[] fields = {"DATA"};
            JSONObject obj = new JSONObject();
            while (cursor.moveToNext()){
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String contactName = cursor.getString(nameIndex);
                String contactNumber = cursor.getString(numberIndex);


                obj.put(contactName,contactNumber);


//                Log.d("MAINACTIVITY","Name: "++"\n Number: "+contacts.get(i).getNumber()+"\n");
                i++;
            }
//            Log.d("MAINACTIVITY","TOTAL # OF CONTACTS: "+obj.length());
            data = new String[1];
            data[0] = "";

            Iterator iterator = obj.keys();
            while(iterator.hasNext()){
                String key = iterator.next().toString();

                data[0] += key+"="+obj.getString(key)+":";

            }

            PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"addContacts.php","POST",fields,data);
            putData.startPut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MAINACTIVITY","=======>onStart<======");
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(Pref_Username)) {
            fetchLiked();
            fetchLikedDesc();
        }
        if (!sharedPreferences.contains(Pref_Username)) {
            likeCacheDesc.clear();
            likeCountCacheDesc.clear();
            likeCache.clear();
            likeCountCache.clear();
            Intent i = new Intent(MainActivity.this, login.class);
            startActivity(i);
            finish();
        }
        fetch_data_into_array(lv);
        Log.d("MAINACTIVITY","likeCache -> "+likeCache);
        Log.d("MAINACTIVITY","likCountCache -> "+likeCountCache);
        Log.d("MAINACTIVITY","likecacheCountDesc -> "+likeCountCacheDesc);
        Log.d("MAINACTIVITY","likeCacheDesc -> "+likeCacheDesc);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.d("MAINACTIVITY","=======>onCreate<======");
        try {
            getContacts();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HomePage = findViewById(R.id.homePage);
        ProfilePage = findViewById(R.id.ProfilePage);
        AddFile = findViewById(R.id.AddFile);

        lv=  findViewById(R.id.recyclerView);
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
        try {
            username = sharedPreferences.getString(Pref_Username, "");
            password = sharedPreferences.getString(Pref_Password, "");
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        ProfilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,userProfile.class);
                startActivity(intent);

            }
        });
        AddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),addFile.class);
                startActivity(i);
            }
        });


        lv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lv.getContext(), RecyclerView.VERTICAL);
        lv.addItemDecoration(dividerItemDecoration);


    }

    public void fetch_data_into_array(View view)
    {

        class  dbManager extends AsyncTask<String,Void,String>
        {
            protected void onPostExecute(String data)
            {
                Log.d("MAINACTIVITY","=>>"+data);
                try {
                    JSONArray ja = new JSONArray(data);
                    JSONObject jo = null;

                    ArrayList<postDataModel> temp = new ArrayList<>();
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);

                        postDataModel d = new postDataModel();
                        d.setImg_name(jo.getString("photos"));
                        d.setImg_description(jo.getString("description"));
                        d.setImg_url(ServerConstants.SERVER_BASE_URL+"images/" + jo.getString("photos"));
                        d.setUserName(jo.getString("username"));

                        temp.add(d);


                    }
                    postDataModels.clear();
                    postDataModels.addAll(temp);

                    if(lv.getAdapter() == null) {
                        myadapter adptr = new myadapter(getApplicationContext(), postDataModels);
                        lv.setAdapter(adptr);
                    }else{
                        ((myadapter)lv.getAdapter()).setDataSet(postDataModels);
                        Log.d("MAINACTIVITY", String.valueOf(postDataModels.size()));
                        ((myadapter)lv.getAdapter()).notifyDataSetChanged();


                    }


                } catch (Exception ex) {
                    Log.d("MAINACTIVITY",ex.getMessage());
                    Toast.makeText(getApplicationContext(), "MAINACTIVITY 271"+ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... strings)
            {
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuffer data = new StringBuffer();
                    String line;

                    while ((line = br.readLine()) != null) {
                        data.append(line).append("\n");
                    }
                    br.close();
                    if(data.toString().equals("")){
                        Log.d("MAINACTIVITY","empty");
                    }else{
                        Log.d("MAINACTIVITY","no empty");
                    }
                    Log.d("MAINACTIVITY","BEFORE Post Execute");
                    return data.toString();

                } catch (Exception ex) {
                    return ex.getMessage();
                }

            }

        }
        dbManager obj=new dbManager();
        obj.execute(apiUrl);

    }

    public void isAdmin(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(ServerConstants.SERVER_BASE_URL + "checkAdmin.php?username=" + username);
                    Log.d("MAINACTIVITY1", url.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    Log.d("MAINACTIVITY2", String.valueOf(conn.getResponseCode()));
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    Log.d("MAINACTIVITY3", url.toString());
                    StringBuffer data = new StringBuffer();
                    String line;
                    Log.d("MAINACTIVITY4", url.toString());
                    while ((line = br.readLine()) != null) {
                        data.append(line);
                    }
                    data.trimToSize();
                    br.close();
                    Log.d("MAINACTIVITY5", "hello"+data.toString()+"hello");

                    Log.d("MAINACTIVITY6", String.valueOf(data.toString().equals("true")));
                    if (data.toString().equals("true")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createDialog();
                            }
                        });
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();


    }

    public void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle("Admin Tab");

        View customLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.custome_dialog,null);
        builder.setView(customLayout);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = customLayout.findViewById(R.id.custom_edit_text);
                Toast.makeText(MainActivity.this,editText.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public class myadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int VIEW_TYPE_1 = 1; //only desc
        private static final int VIEW_TYPE_2 = 2; // only img
        private static final int VIEW_TYPE_3 = 3; //both

        private Context context;
        private ArrayList<postDataModel> list = null;

        public void setDataSet(ArrayList<postDataModel> a){
            list = a;
        }

        public myadapter(Context context, ArrayList<postDataModel> postDataModels) {
            this.context = context;
            this.list = postDataModels;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_1){
                return new myViewHolder1(LayoutInflater.from(context).inflate(R.layout.row_only_description,parent,false));
            }else if(viewType == VIEW_TYPE_2){
                return new myViewHolder2(LayoutInflater.from(context).inflate(R.layout.row_only_image,parent,false));
            }
            return new myViewHolder3(LayoutInflater.from(context).inflate(R.layout.row_image_and_desc,parent,false));
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder.getItemViewType() == VIEW_TYPE_1) {
                myViewHolder1 vh = (myViewHolder1) holder;

                vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        isAdmin();
                        return true;
                    }

                });

                vh.desc.setText(list.get(vh.getAdapterPosition()).getImg_description());
                vh.userName.setText(list.get(vh.getAdapterPosition()).getUserName());
                vh.shareButton.setImageResource(R.drawable.ic_baseline_share_24);

                vh.shareButton.setOnClickListener(v->{
                    shareText(vh.desc.getText().toString());
                });

                if (context.getSharedPreferences(filename, MODE_PRIVATE).contains(Pref_Username)) {

                    if (likeCacheDesc.contains(list.get(vh.getAdapterPosition()).getImg_description())) {
                        vh.likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    } else {
                        vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                    }
                    //for like counts
                    if (likeCountCacheDesc.containsKey(list.get(vh.getAdapterPosition()).getImg_description())) {
                        if (likeCountCacheDesc.get(list.get(vh.getAdapterPosition()).getImg_description()) != null) {
                            Integer s = likeCountCacheDesc.get(list.get(vh.getAdapterPosition()).getImg_description());
                            assert s != null;
                            vh.likeCount.setText(String.valueOf(s.intValue()));
                        }
                    } else {
                        vh.likeCount.setText("0");
                    }
                } else {
                    vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                    vh.likeCount.setText("0");
                }

                vh.likeButton.setOnClickListener(v -> {
//                    Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences(filename,MODE_PRIVATE);
                    if(!sharedPreferences.contains(Pref_Username)){
                        Toast.makeText(context,"Please Login To like!",Toast.LENGTH_LONG).show();
                    }else{
                        if (!likeCacheDesc.contains(list.get(vh.getAdapterPosition()).getImg_description())) {
                            vh.likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                            likeStatus = "liked";
                            likeCacheDesc.add(list.get(vh.getAdapterPosition()).getImg_description());
                            postDescLike(list.get(vh.getAdapterPosition()).getImg_description(), likeStatus);
                            int i = Integer.parseInt((String) vh.likeCount.getText());
                            i++;
                            likeCountCacheDesc.put(list.get(vh.getAdapterPosition()).getImg_description(), i);
                            vh.likeCount.setText(String.valueOf(i));

                        } else {
                            postUnlikeDesc(list.get(vh.getAdapterPosition()).getImg_description());
                            vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                            int i = Integer.parseInt((String) vh.likeCount.getText());
                            i--;
                            Log.d("MAINACTIVITY","DISLIKED HERE "+list.get(vh.getAdapterPosition()).getImg_description());
                            likeCountCacheDesc.put(list.get(vh.getAdapterPosition()).getImg_description(), i);
                            vh.likeCount.setText(String.valueOf(i));

                        }
                    }
                });
            } else if (holder.getItemViewType() == VIEW_TYPE_2) {
                myViewHolder2 vh = (myViewHolder2) holder;

                vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        isAdmin();

                        return true;
                    }

                });

                Glide.with(context).load(list.get(vh.getAdapterPosition()).getImg_url()).into(vh.img);
                vh.userName.setText(list.get(vh.getAdapterPosition()).getUserName());

                vh.shareButton.setImageResource(R.drawable.ic_baseline_share_24);

                vh.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable) vh.img.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        shareImage(bitmap);

                    }
                });


                vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                vh.likeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //change start
                        //added if block
                        Log.d("MAINACTIVITY","IN_LIKEBTN_CLICKlISTENER");

                        SharedPreferences sharedPreferences = getSharedPreferences(filename,MODE_PRIVATE);
                        if(!sharedPreferences.contains(Pref_Username)){
                            Toast.makeText(context,"Please Login To like!",Toast.LENGTH_LONG).show();
                        }else{
                                if (!likeCache.contains(list.get(vh.getAdapterPosition()).getImg_name())) {
                                    vh.likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                                    likeStatus = "liked";
                                    likeCache.add(list.get(vh.getAdapterPosition()).getImg_name());
                                    postLike(list.get(vh.getAdapterPosition()).getImg_name(), likeStatus);


                                    int i = Integer.parseInt((String) vh.likeCount.getText());
                                    i++;
                                    likeCountCache.put(list.get(vh.getAdapterPosition()).getImg_name(), i);
                                    vh.likeCount.setText(String.valueOf(i));

                                } else {
                                    postUnlike(list.get(vh.getAdapterPosition()).getImg_name());
                                    vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                                    int i = Integer.parseInt((String) vh.likeCount.getText());
                                    i--;
                                    likeCountCache.put(list.get(vh.getAdapterPosition()).getImg_name(), i);
                                    vh.likeCount.setText(String.valueOf(i));

                                }
                        }
                    }
                });
                if (context.getSharedPreferences(filename, MODE_PRIVATE).contains(Pref_Username)) {

                    if (likeCache.contains(list.get(vh.getAdapterPosition()).getImg_name())) {
                        vh.likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    } else {
                        vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                    }
                    //for like counts
                    if (likeCountCache.containsKey(list.get(vh.getAdapterPosition()).getImg_name())) {
                        if (likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name()) != null) {
                            Integer s = likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name());
                            assert s != null;
                            vh.likeCount.setText(String.valueOf(s.intValue()));
                        }
                    } else {
                        vh.likeCount.setText("0");
                    }
                } else {
                    vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                    vh.likeCount.setText("0");
                }
            } else {
                myViewHolder3 vh = (myViewHolder3) holder;
                vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        isAdmin();

                        return true;
                    }

                });
                Glide.with(context).load(list.get(vh.getAdapterPosition()).getImg_url()).into(vh.img);
                vh.desc.setText(list.get(vh.getAdapterPosition()).getImg_description());
                vh.userName.setText(list.get(vh.getAdapterPosition()).getUserName());

                vh.shareButton.setImageResource(R.drawable.ic_baseline_share_24);

                vh.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) vh.img.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageandText(bitmap,vh.desc.getText().toString());

                }
            });

                vh.likeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //change start


                        SharedPreferences sharedPreferences = getSharedPreferences(filename,MODE_PRIVATE);
                        if(!sharedPreferences.contains(Pref_Username)){
                            Toast.makeText(context,"Please Login To like!",Toast.LENGTH_LONG).show();
                        }else{
                            if (!likeCache.contains(list.get(vh.getAdapterPosition()).getImg_name())) {
                                vh.likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                                likeStatus = "liked";
                                likeCache.add(list.get(vh.getAdapterPosition()).getImg_name());
                                postLike(list.get(vh.getAdapterPosition()).getImg_name(), likeStatus);


                                int i = Integer.parseInt((String) vh.likeCount.getText());
                                i++;
                                likeCountCache.put(list.get(vh.getAdapterPosition()).getImg_name(), i);
                                vh.likeCount.setText(String.valueOf(i));

                            } else {
                                postUnlike(list.get(vh.getAdapterPosition()).getImg_name());
                                vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                                int i = Integer.parseInt((String) vh.likeCount.getText());
                                i--;
                                likeCountCache.put(list.get(vh.getAdapterPosition()).getImg_name(), i);
                                vh.likeCount.setText(String.valueOf(i));

                            }
                        }
                    }
                });
                if (context.getSharedPreferences(filename, MODE_PRIVATE).contains(Pref_Username)) {

                    if (likeCache.contains(list.get(position).getImg_name())) {
                        vh.likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    } else {
                        vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                    }


                    if (likeCountCache.containsKey(list.get(vh.getAdapterPosition()).getImg_name())) {
                        if (likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name()) != null) {
                            Integer s = likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name());
                            assert s != null;
                            vh.likeCount.setText(String.valueOf(s.intValue()));
                        }
                    } else {
                        vh.likeCount.setText("0");
                    }
                } else {
                    vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                    vh.likeCount.setText("0");
                }
            }
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            int result;
            if(list.get(position).getImg_description().equals("")){
                result = 2;
            }else if(list.get(position).getImg_name().equals("")){
                result = 1;
            }else{
                result = 3;
            }
            return result;
        }

        class myViewHolder1 extends RecyclerView.ViewHolder{
            public TextView userName;
            public TextView desc;
            public ImageView likeButton;
            public TextView likeCount;
            public ImageView shareButton;
            public myViewHolder1(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.tv1);
                desc = itemView.findViewById(R.id.descriptionTextView);
                likeButton = itemView.findViewById(R.id.likeBtn);
                likeCount = itemView.findViewById(R.id.likeCount);
                shareButton = itemView.findViewById(R.id.shareBtn);

            }
        }
        class myViewHolder2 extends RecyclerView.ViewHolder{
            public TextView userName;
            public ImageView img;
            public ImageView likeButton;
            public TextView likeCount;
            public ImageView shareButton;
            public myViewHolder2(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.tv1);
                img = itemView.findViewById(R.id.img1);
                likeButton = itemView.findViewById(R.id.likeBtn);
                likeCount = itemView.findViewById(R.id.likeCount);
                shareButton = itemView.findViewById(R.id.shareBtn);

            }
        }
        class myViewHolder3 extends RecyclerView.ViewHolder{
            public TextView userName;
            public ImageView img;
            public TextView desc;
            public ImageView likeButton;
            public TextView likeCount;
            public ImageView shareButton;
            public myViewHolder3(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.tv1);
                img = itemView.findViewById(R.id.img1);
                desc = itemView.findViewById(R.id.descriptionTextView);
                likeButton = itemView.findViewById(R.id.likeBtn);
                likeCount = itemView.findViewById(R.id.likeCount);
                shareButton = itemView.findViewById(R.id.shareBtn);

            }
        }
    }

//    public class myadapter extends RecyclerView<myadapter.ViewHolder>
//    {
//        Context context;
//        String[] ttl;
//        String[] dsc;
//        String[] rimg;
//        String[] img_name;
//
//        myadapter(Context c, String[] ttl, String[] rimg, String[] img_name,String[] description)
//        {
//            super(c,R.layout.row_only_image,R.id.tv1,ttl);
//            context=c;
//            this.ttl=ttl;
//            this.dsc=description;
//            this.rimg=rimg;
//            this.img_name = img_name;
//        }
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
//        {
//
//            //0 means only image,1 means only desc and 2 means both
//            int type = 0;
//
//            View row = convertView;
//
//            if(row == null){
//                if(img_desc[position].equals("")){
//                    row = LayoutInflater.from(getContext()).inflate(R.layout.row_only_image,parent,false);
//                    type = 0;
//                    Log.d("MAINACTIVITY","here 1 "+type);
//                }else if(img_name[position].equals("")){
//                    row = LayoutInflater.from(getContext()).inflate(R.layout.row_only_description,parent,false);
//                    type = 1;
//                    Log.d("MAINACTIVITY","here 2 "+type);
//                }else{
//                    row = LayoutInflater.from(getContext()).inflate(R.layout.row_image_and_desc,parent,false);
//                    type =2;
//                    Log.d("MAINACTIVITY","here 3 "+type);
//                }
//            }
//                if(img_desc[position].equals("")){
//                    if(type!=0){
//                        type = 0;
//                        row = LayoutInflater.from(getContext()).inflate(R.layout.row_only_image,parent,false);
//                        Log.d("MAINACTIVITY","here again 1 "+type);
//                    }
//                }else if(img_name[position].equals("")){
//                    if(type !=1){
//                    type = 1;
//                    row = LayoutInflater.from(getContext()).inflate(R.layout.row_only_description,parent,false);
//                        Log.d("MAINACTIVITY","here again 2 "+type);
//                    }
//                }else{
//                    if(type !=2) {
//                        type = 2;
//                        row = LayoutInflater.from(getContext()).inflate(R.layout.row_image_and_desc, parent, false);
//                        Log.d("MAINACTIVITY","here again 3 "+type);
//                    }
//                }
//
////            LayoutInflater inflater = getLayoutInflater();
////            View row=inflater.inflate(R.layout.row,parent,false);
//
//            ImageView img = null;
//            TextView desc = null;
//            String url = null;
//
//            if(type == 0){
//                img=row.findViewById(R.id.img1);
//            }else if(type == 1){
//                desc = row.findViewById(R.id.descriptionTextView);
//
//            }else{
//                img=row.findViewById(R.id.img1);
//                desc = row.findViewById(R.id.descriptionTextView);
//            }
//            if(type != 0){
//                desc.setText(img_desc[position]);
//            }
//
//            TextView tv1=row.findViewById(R.id.tv1);
//            ImageView shareBtn =row.findViewById(R.id.shareBtn);
//            ImageView likeBtn = row.findViewById(R.id.likeBtn);
//            TextView likeBtnCount = row.findViewById(R.id.likeCount);
//
//
//            tv1.setText(ttl[position]);
//
//            if(type != 1) {
//                url = rimg[position];
//                assert img != null;
//                Glide.with(img.getContext()).load(url).into(img);
//            }
//
//            ImageView finalImg = img;
//            shareBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    BitmapDrawable bitmapDrawable = (BitmapDrawable) finalImg.getDrawable();
//                    Bitmap bitmap = bitmapDrawable.getBitmap();
//                    shareImageandText(bitmap);
//
//                }
//            });
//            int finalType = type;
//            likeBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    //change start
//                    //added if block
//                    Log.d("MAINACTIVITY","IN_LIKEBTN_CLICKlISTENER");
//
//                    /*
//                    * if(checklike()
//                    *
//                    * */
//                    SharedPreferences sharedPreferences = getSharedPreferences(filename,MODE_PRIVATE);
//                    if(!sharedPreferences.contains(Pref_Username)){
//                        Toast.makeText(context,"Please Login To like!",Toast.LENGTH_LONG).show();
//                    }else{
//                        if(finalType != 1)
//                        {
//                            if (!likeCache.contains(img_name[position])) {
//                                likeBtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
//                                likeStatus = "liked";
//                                likeCache.add(img_name[position]);
//                                postLike(img_name[position], likeStatus);
//
//
//                                int i = Integer.parseInt((String) likeBtnCount.getText());
//                                i++;
//                                likeCountCache.put(img_name[position], i);
//                                likeBtnCount.setText(String.valueOf(i));
//                            } else {
//                                postUnlike(img_name[position]);
//                                likeBtn.setImageResource(R.drawable.ic_outline_thumb_up_24);
//                                int i = Integer.parseInt((String) likeBtnCount.getText());
//                                i--;
//                                likeCountCache.put(img_name[position], i);
//                                likeBtnCount.setText(String.valueOf(i));
//                            }
//                        }
//                    }
//                }
//            });
//
//            if(context.getSharedPreferences(filename,MODE_PRIVATE).contains(Pref_Username)) {
//                if(type != 1)
//                {
//                    if (likeCache.contains(img_name[position])) {
//                        likeBtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
//                    } else {
//                        likeBtn.setImageResource(R.drawable.ic_outline_thumb_up_24);
//                    }
//                    //for like counts
//                    if (likeCountCache.containsKey(img_name[position])) {
//                        if (likeCountCache.get(img_name[position]) != null) {
//                            Integer s = likeCountCache.get(img_name[position]);
//                            assert s != null;
//                            likeBtnCount.setText(String.valueOf(s.intValue()));
//                        }
//                    } else {
//                        likeBtnCount.setText("0");
//                    }
//                }
//            }else {
//                likeBtn.setImageResource(R.drawable.ic_outline_thumb_up_24);
//                likeBtnCount.setText("0");
//            }
//
//            return row;
//        }
//
//    }

    private void postUnlikeDesc(String desc_name){
        try {

            Handler handler = new Handler(Looper.myLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "username";
                    field[1] = "desc_name";

                    String[] data = new String[2];
                    data[0] = username;
                    data[1] = desc_name;


//                    http://10.0.2.2/newsapp/likePost.php
                    PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"unlikePostDesc.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        String result = putData.getResult();
//                        Log.d("MAINACTIVITY","LINE 467: "+result);
                        if(result.equals("successful")){
//                            Log.d("MAINACTIVITY","LINE 468: "+img_name);
//                            Log.d("MAINACTIVITY","BEFORE LIKECACHE REMOVE 505: "+likeCache.toString());
                            likeCacheDesc.remove(desc_name);
//                            Log.d("MAINACTIVITY","AFTER LIKECACHE REMOVE 507: "+likeCache.toString());
//                            for(String s:likeCache){
//                                Log.d("MAINACTIVITY","LINE 471: "+s);
//                            }
                        }
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void postUnlike(String img_name){
        try {

            Handler handler = new Handler(Looper.myLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "username";
                    field[1] = "img_name";

                    String[] data = new String[2];
                    data[0] = username;
                    data[1] = img_name;


//                    http://10.0.2.2/newsapp/likePost.php
                    PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"unlikePost.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        String result = putData.getResult();
//                        Log.d("MAINACTIVITY","LINE 467: "+result);
                        if(result.equals("successful")){
//                            Log.d("MAINACTIVITY","LINE 468: "+img_name);
                            Log.d("MAINACTIVITY","BEFORE LIKECACHE REMOVE 505: "+likeCache.toString());
                            likeCache.remove(img_name);
                            Log.d("MAINACTIVITY","AFTER LIKECACHE REMOVE 507: "+likeCache.toString());
//                            for(String s:likeCache){
//                                Log.d("MAINACTIVITY","LINE 471: "+s);
//                            }
                        }
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void postDescLike(String desc_name,String likeStatus){
        try{
            new Handler(Looper.myLooper()).post(() -> {
                String[] field = new String[3];
                field[0] = "username";
                field[1] = "desc_name";
                field[2] = "likeStatus";
                String[] data = new String[3];
                data[0] = username;
                data[1] = desc_name;
                data[2] = likeStatus;

//                    http://10.0.2.2/newsapp/likePost.php
                PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"likeDescription.php", "POST", field, data);
                putData.startPut();
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void postLike(String img_name,String likeStatus){

        try {

            Handler handler = new Handler(Looper.myLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[3];
                    field[0] = "username";
                    field[1] = "img_name";
                    field[2] = "likeStatus";
                    String[] data = new String[3];
                    data[0] = username;
                    data[1] = img_name;
                    data[2] = likeStatus;

//                    http://10.0.2.2/newsapp/likePost.php
                    PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"likePost.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void checkLike(String img_name, String username ,int postion){
//
//        try {
//            cpostion = postion;
//            HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
//            handlerThread.start();
//            Looper looper = handlerThread.getLooper();
//            Handler handler = new Handler(looper);
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//
//                            String[] field = new String[2];
//                            field[0] = "username";
//                            field[1] = "img_name";
//                            String[] data = new String[2];
//                            data[0] = username;
//                            data[1] = img_name;
//
////                    http://10.0.2.2/newsapp/checkLike.php
//                            PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"checkLike.php", "POST", field, data);
//                            putData.startPut();
//                            if (putData.onComplete()) {
//                                String result = putData.getResult();
//                                Log.d("MAINACTIVITY","IN_CHEKCKLIKE"+result);
////                                Toast.makeText(getApplicationContext(),"IN CHECKLIKE"+result,Toast.LENGTH_SHORT).show();
//                                if(result.equals("liked")){
//                                    isliked = true;
//                                    likeCache[cpostion] = true;
//
//                                }
//                            }
//
//                }
//            });
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
////        return isliked;
//    }

    private void shareText(String text){
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_TEXT,text);

        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void shareImageandText(Bitmap bitmap,String text){
        Uri uri = getmageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT,text);

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Image Shared with text");

        // setting type to image
        intent.setType("image/jpg");

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void shareImage(Bitmap bitmap) {
        Uri uri = getmageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        // setting type to image
        intent.setType("image/jpg");

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"));
    }
    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
                uri = FileProvider.getUriForFile(this, "com.limxtop.research.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }


//    private void fetchImg() {
//        Handler handler = new Handler(Looper.myLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                String[] field = new String[2];
//                field[0] = "username";
//                field[1] = "password";
//                String[] data = new String[2];
//                data[0] = username;
//                data[1] = password;
//
//
//                PutData putData = new PutData("http://172.20.10.3/newsapp/fetchImg.php", "POST", field, data);
//                putData.startPut();
//                if (putData.onComplete()) {
//                    String result = putData.getResult();
//                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
//                    uploadImg(result);
//
//                }
//            }
//        });
//
//    }
//
//    private  void uploadImg(String result) {
//        String img = "http://172.20.10.3/newsapp/images/"+result;
//        context = getApplicationContext();
//        Glide.with(MainActivity.context).load(img).into(img01);
//
//    }
void fetchLikedDesc(){
    Handler handler = new Handler(Looper.myLooper());
    handler.post(new Runnable() {
        @Override
        public void run() {
            String[] field = new String[1];
            field[0] = "username";
            String[] data = new String[1];
            data[0] = username;

            ArrayList<String> temp_array = new ArrayList<>();
            PutData putData = new PutData(ServerConstants.SERVER_BASE_URL + "fetchAllLikedDesc.php", "POST", field, data);
            putData.startPut();
            if (putData.onComplete()) {
                String result = putData.getResult();

                if (!result.equals("empty")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            temp_array.add(jsonObject.getString("description"));
                        }
                        likeCacheDesc.clear();
                        likeCacheDesc.addAll(temp_array);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //fetching number of likes on each post by default each photo being displayed have zero likes but after fetch those who have likes
                //will be updated in temp_lik_count array. and then using this array we will update our list view.
                HashMap<String, Integer> temp_like_count = new HashMap<>();
                putData = new PutData(ServerConstants.SERVER_BASE_URL + "fetchCountOfLikesDesc.php", "POST", field, data);
                putData.startPut();
                if (putData.onComplete()) {
                    result = putData.getResult();

                    if (!result.equals("empty")) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject jsonObject = null;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                temp_like_count.put(jsonObject.getString("description"),Integer.parseInt(jsonObject.getString("count")));
                            }

                            likeCountCacheDesc.clear();
                            likeCountCacheDesc.putAll(temp_like_count);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }



                }
            }
        }
    });
}


    void fetchLiked(){
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "username";
                String[] data = new String[1];
                data[0] = username;

                ArrayList<String> temp_array = new ArrayList<>();
                PutData putData = new PutData(ServerConstants.SERVER_BASE_URL + "fetchAllLiked.php", "POST", field, data);
                putData.startPut();
                if (putData.onComplete()) {
                    String result = putData.getResult();

                    if (!result.equals("empty")) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject jsonObject = null;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                temp_array.add(jsonObject.getString("img_name"));
                            }
                            likeCache.clear();
                            likeCache.addAll(temp_array);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //fetching number of likes on each post by default each photo being displayed have zero likes but after fetch those who have likes
                    //will be updated in temp_lik_count array. and then using this array we will update our list view.
                    HashMap<String, Integer> temp_like_count = new HashMap<>();
                    putData = new PutData(ServerConstants.SERVER_BASE_URL + "fetchCountOfLikes.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        result = putData.getResult();

                        if (!result.equals("empty")) {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                JSONObject jsonObject = null;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    temp_like_count.put(jsonObject.getString("img_name"),Integer.parseInt(jsonObject.getString("count")));
                                }
                                likeCountCache.clear();
                                likeCountCache.putAll(temp_like_count);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }



                    }
                }
            }
        });
    }
}