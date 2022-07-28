package com.visanka.news;

import android.content.Intent;
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

public class signup extends AppCompatActivity {
    TextInputEditText textInputEditTextfullname,textInputEditTextusername,textInputEditTextemail,textInputEditTextpassword;
    Button SignupButton;
    TextView textviewlogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        textInputEditTextfullname = findViewById(R.id.fullname);
        textInputEditTextusername = findViewById(R.id.username);
        textInputEditTextemail = findViewById(R.id.email);
        textInputEditTextpassword = findViewById(R.id.password);
        textviewlogin = findViewById(R.id.textviewlogin);
        progressBar = findViewById(R.id.progress);
        SignupButton = findViewById(R.id.signupbutton);

        textviewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();
            }
        });

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname, username, email, password;
                fullname = textInputEditTextfullname.getText().toString();
                username = textInputEditTextusername.getText().toString();
                email = textInputEditTextemail.getText().toString();
                password = textInputEditTextpassword.getText().toString();


//                Log.d("SIGNUP","FN"+fullname);
//                Log.d("SIGNUP","UN"+username);
//                Log.d("SIGNUP","EM"+email);
//                Log.d("SIGNUP","PAS"+password);

                Log.d("SIGNUP","FN "+fullname.isEmpty());
                Log.d("SIGNUP","UN "+username.isEmpty());
                Log.d("SIGNUP","EM "+email.isEmpty());
                Log.d("SIGNUP","PAS "+password.isEmpty());


                if (!fullname.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    Log.d("SIGNUP","HERE BRO LINE 69");
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.myLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[4];
                            field[0] = "fullname";
                            field[1] = "username";
                            field[2] = "email";
                            field[3] = "password";
                            String[] data = new String[4];
                            data[0] = fullname;
                            data[1] = username;
                            data[2] = email;
                            data[3] = password;

                            PutData putData = new PutData(ServerConstants.SERVER_BASE_URL+"signup.php", "POST", field, data);
                            if (putData.startPut()){
                                if (putData.onComplete()){
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();

                                    if(result.equals("Sign Up Success")){
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),login.class);
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
                    Log.d("SIGNUP","HERE BRO LINE 107");
                    Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}