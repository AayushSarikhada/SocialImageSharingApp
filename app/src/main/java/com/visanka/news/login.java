package com.visanka.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.visanka.news.constants.ServerConstants;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class login extends AppCompatActivity {
    TextInputEditText textInputEditTextusername,textInputEditTextpassword;
    Button LoginButton;
    TextView textviewsignup,forgotPasswordtxt;
    ProgressBar ProgressBar;
    SharedPreferences sharedPreferences;
//    private static Context mContext;
//    private String ip = mContext.getString(R.string.ipAddress);

    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
//    public static final Boolean Is_Loggedin= ;


//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (getSharedPreferences.contains(Pref_Username)){
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LOGIN","INSIDE LOGIN.JAVA");
        setContentView(R.layout.activity_login);

        textInputEditTextusername = findViewById(R.id.username);
        textInputEditTextpassword = findViewById(R.id.password);
        LoginButton = findViewById(R.id.loginbutton);
        ProgressBar = findViewById(R.id.progress);
        textviewsignup = findViewById(R.id.textviewsignup);
        forgotPasswordtxt = findViewById(R.id.forgotPasswordtxt);

        sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);




        textviewsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),signup.class);
                startActivity(intent);
                finish();
            }
        });
        forgotPasswordtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),forgotPassword.class);
                startActivity(i);
            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;

                username = textInputEditTextusername.getText().toString().trim();
                Log.d("LOGIN",username);

                password = textInputEditTextpassword.getText().toString().trim();
                Log.d("LOGIN",password);
                if (!username.equals("") &&  !password.equals("")) {
                    ProgressBar.setVisibility(View.VISIBLE);
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

//                            Log.d("LOGIN",data[0]);
//                            Log.d("LOGIN",data[1]);
                            PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"login.php", "POST", field, data);
//                            PutData putData1 = new PutData("http://192.168.43.196/newsapp/userData.php", "POST", field, data);
//                            putData1.startPut();
//                            if(putData1.onComplete()){
//                                Toast.makeText(getApplicationContext(),"data uploded",Toast.LENGTH_SHORT).show();
//                            }
                            if (putData.startPut()){

                                if (putData.onComplete()){
                                    ProgressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();

                                    if(result.equals("Login Success")) {


                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(Pref_Username,username);
                                        editor.putString(Pref_Password,password);
//                                        editor.putBoolean("Is_Loggedin",true);
                                        editor.apply();

                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        intent.putExtra("username",username);
                                        intent.putExtra("password",password);
//                                        intent.putExtra("userStatus",userLoggedin);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        }
                    });

                }else {
                    Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}