package com.visanka.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.visanka.news.Models.postDataModel;
import com.example.newsapp.R;
import com.visanka.news.constants.ServerConstants;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
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

public class userProfile extends AppCompatActivity {
    TextView Fullname,Username,emailID;

    private String strJson;
    private static final String apiUrl = ServerConstants.SERVER_BASE_URL+"imgForUserProfile.php";
    public static String profilePic_url = null;

//    private OkHttpClient client;
    private Response response;
    private RequestBody requestBody;
    private Request request;
    private String username;
    private String password;
//    private ProgressDialog progressDialog;
    public boolean userLoggedin = false;
    private ImageView HomePage,ProfilePage,AddFile,profilePic;
    private Button LogoutBtn;
    private RecyclerView lv;


    SharedPreferences sharedPreferences;
    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
    public static Context context;
    private static ArrayList<String> user_name;
    private  static ArrayList<String> img;
    private static ArrayList<String> img_names;

    private ArrayList<postDataModel> dataset = new ArrayList<>();


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


//        setUserTypeOnButtonClick();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Please wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
        Fullname = findViewById(R.id.fullname);
        Username = findViewById(R.id.username);
        emailID = findViewById(R.id.emailID);
        HomePage = findViewById(R.id.homePage1);
        ProfilePage = findViewById(R.id.ProfilePage1);
        LogoutBtn = findViewById(R.id.logoutbtn);
        AddFile = findViewById(R.id.AddFile);
        profilePic = findViewById(R.id.profilePic_imageView);
        lv = findViewById(R.id.rv);

//        Log.d("USERPROFILE","inside on create1");


            sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
            username = sharedPreferences.getString(Pref_Username,"");
            password = sharedPreferences.getString(Pref_Password,"");

//        Log.d("USERPROFILE","inside on create2");
                if (!sharedPreferences.contains(Pref_Username)) {

                    Intent i = new Intent(userProfile.this, login.class);
                    startActivity(i);
                    finish();
                }



        try {

//            Bundle extra = getIntent().getExtras();
//            if (extra != null) {
//                username = extra.getString("username");
//                password = extra.getString("password");


                  Handler handler = new Handler(Looper.myLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "password";
                            String[] data = new String[2];
                            data[0] = username;
                            data[1] = password;


                            PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"profileData.php", "POST", field, data);
                            putData.startPut();
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                updateUserData(result);
                            }

                        }
                    });
//                }

        }catch (Exception e){
            e.printStackTrace();
        }


        HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(userProfile.this,MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
//                Intent i = new Intent(getApplicationContext(),login.class);
//                startActivity(i);
                finish();

            }
        });
        AddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),addFile.class);
                startActivity(i);
                finish();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),updateProfilepic.class);
                startActivity(i);
                finish();
            }
        });
//        try {
//
//            Handler handler = new Handler(Looper.myLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    String[] field = new String[2];
//                    field[0] = "username";
//                    field[1] = "password";
//                    String[] data = new String[2];
//                    data[0] = username;
//                    data[1] = password;
//
//
//                    PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"imgForHome.php", "POST", field, data);
//                    putData.startPut();
//                    if (putData.onComplete()) {
//                        String result = putData.getResult();
//                        if(!result.equals("empty")) {
//                            Log.d("USERPROFILE","LINE 212 : "+result);
//
//                            fetchData(result);
//                        }else{
//                            Log.d("USERPROFILE","line 217"+result);
//                        }
//                    }
//
//                }
//            });
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        lv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lv.getContext(), RecyclerView.VERTICAL);
        lv.addItemDecoration(dividerItemDecoration);
        fetch_data_into_array(lv);
    }
    public void fetch_data_into_array(View view)
    {

        class  dbManager extends AsyncTask<String,Void,String>
        {
            protected void onPostExecute(String data)
            {
                Log.d("USERPROFILE","=>>"+data);
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
                    dataset.clear();
                    dataset.addAll(temp);

                    if(lv.getAdapter() == null) {
                        myadapter adptr = new myadapter(getApplicationContext(), dataset);
                        lv.setAdapter(adptr);
                    }else{
                        ((myadapter)lv.getAdapter()).setDataSet(dataset);
                    }


                } catch (Exception ex) {
                    Log.d("USERPROFILE",ex.getMessage());
//                    Toast.makeText(getApplicationContext(), "USERPROFILE 271"+ex.getMessage(), Toast.LENGTH_LONG).show();
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

                    Log.d("USERPROFILE","BEFORE Post Execute");
                    Log.d("USERPROFILE","===>>>"+data);
                    return data.toString();

                } catch (Exception ex) {
                    return ex.getMessage();
                }

            }

        }
        dbManager obj=new dbManager();
        obj.execute(apiUrl+"?username="+username);

    }

    //somehow result that server(fetchImg.php) sends has first element as null in the result
    //that is why all the trash is there in the below fetchData method
//    public void fetchData(String result){
//        try {
//            JSONArray ja = new JSONArray(result);
//            JSONObject jo = null;
//
//            dataset = new ArrayList<>();
//
//            postDataModel post;
//            for (int i = 0; i < ja.length(); i++) {
//                post = new postDataModel();
//                jo = ja.getJSONObject(i);
//
//                post.setUserName(jo.getString("username"));
//                post.setImg_url(ServerConstants.SERVER_BASE_URL+"images/" + jo.getString("photos"));;
//                post.setImg_name(jo.getString("photos"));
//                post.setImg_description(jo.getString("description"));
//            }
////            Log.d("USERPROFILE","244 line:"+user_name);
////            Log.d("USERPROFILE","245 line:"+img);
////            String[] temp_user_name = Arrays.copyOf(user_name.toArray(),user_name.size(),String[].class);
////            String[] temp_img = Arrays.copyOf(img.toArray(),img.size(),String[].class);
//
//            userProfile.myadapter adptr = new myadapter(getApplicationContext(),);
//            lv.setAdapter(adptr);
//
//        } catch (Exception ex) {
//            Log.d("USERPROFILE","LINE 252 "+ex.getMessage());
//        }
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
                vh.desc.setText(list.get(vh.getAdapterPosition()).getImg_description());
                vh.userName.setText(list.get(vh.getAdapterPosition()).getUserName());
                vh.shareButton.setImageResource(R.drawable.ic_baseline_share_24);
                vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);

                vh.shareButton.setOnClickListener(v->{
                    shareText(vh.desc.getText().toString());
                });

                if (context.getSharedPreferences(filename, MODE_PRIVATE).contains(Pref_Username)) {


                    //for like counts
                    if (MainActivity.likeCountCacheDesc.containsKey(list.get(vh.getAdapterPosition()).getImg_description())) {
                        if (MainActivity.likeCountCacheDesc.get(list.get(vh.getAdapterPosition()).getImg_description()) != null) {
                            Integer s = MainActivity.likeCountCacheDesc.get(list.get(vh.getAdapterPosition()).getImg_description());
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


            } else if (holder.getItemViewType() == VIEW_TYPE_2) {
                myViewHolder2 vh = (myViewHolder2) holder;
                Glide.with(context).load(list.get(vh.getAdapterPosition()).getImg_url()).into(vh.img);
                vh.userName.setText(list.get(vh.getAdapterPosition()).getUserName());
                vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                vh.shareButton.setImageResource(R.drawable.ic_baseline_share_24);

                vh.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable) vh.img.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        shareImage(bitmap);

                    }
                });

                if (context.getSharedPreferences(filename, MODE_PRIVATE).contains(Pref_Username)) {

                    //for like counts
                    if (MainActivity.likeCountCache.containsKey(list.get(vh.getAdapterPosition()).getImg_name())) {
                        if (MainActivity.likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name()) != null) {
                            Integer s = MainActivity.likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name());
                            assert s != null;
                            vh.likeCount.setText(String.valueOf(s.intValue()));
                        }
                    } else {
                        vh.likeCount.setText("0");
                    }
                } else {
                    vh.likeCount.setText("0");
                }
            } else {
                myViewHolder3 vh = (myViewHolder3) holder;
                Glide.with(context).load(list.get(vh.getAdapterPosition()).getImg_url()).into(vh.img);
                vh.desc.setText(list.get(vh.getAdapterPosition()).getImg_description());
                vh.userName.setText(list.get(vh.getAdapterPosition()).getUserName());
                vh.likeButton.setImageResource(R.drawable.ic_outline_thumb_up_24);
                vh.shareButton.setImageResource(R.drawable.ic_baseline_share_24);

                vh.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable) vh.img.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        shareImageandText(bitmap,vh.desc.getText().toString());

                    }
                });


                if (context.getSharedPreferences(filename, MODE_PRIVATE).contains(Pref_Username)) {


                    if (MainActivity.likeCountCache.containsKey(list.get(vh.getAdapterPosition()).getImg_name())) {
                        if (MainActivity.likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name()) != null) {
                            Integer s = MainActivity.likeCountCache.get(list.get(vh.getAdapterPosition()).getImg_name());
                            assert s != null;
                            vh.likeCount.setText(String.valueOf(s.intValue()));
                        }
                    } else {
                        vh.likeCount.setText("0");
                    }
                } else {
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



    private  void updateUserData(String strJson){
        Log.d("USERPROFILE","stringJSON="+strJson);
        try {
                JSONArray parent = new JSONArray(strJson);
                JSONObject child = parent.getJSONObject(0);

                String full_name = child.getString("fullname");
                String user_name = child.getString("username");
                String email_Id = child.getString("email");
                String profile_Pic = child.getString("profilePicture");

                Fullname.setText(full_name);
                Username.setText(user_name);
                emailID.setText(email_Id);

//                Log.d("USERPROFILE",profile_Pic);
                if(profile_Pic!=null)
                    uploadImg(profile_Pic);

//                progressDialog.hide();

        }catch (JSONException  e){
            Log.d("USERPROFILE","LINE 357: UPDATE_USER_DATA_JSONEXCEPTION");
            e.printStackTrace();
        }
    }
    private  void uploadImg(String result) {
        if(result.isEmpty())
            return;

        profilePic_url = ServerConstants.SERVER_BASE_URL+"images/"+result;
        context = getApplicationContext();
        Glide.with(userProfile.context).load(profilePic_url).into(profilePic);

    }


}