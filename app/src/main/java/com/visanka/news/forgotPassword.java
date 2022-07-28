package com.visanka.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.visanka.news.constants.ServerConstants;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class forgotPassword extends AppCompatActivity {
    EditText emailID;
    Button submit;
    private String EmailID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailID = findViewById(R.id.emailID);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailID = emailID.getText().toString().trim();
                if (EmailID != ""){
                    Handler handler = new Handler(Looper.myLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[1];
                            field[0] = "email";
                            String[] data = new String[1];
                            data[0] = EmailID;

                            PutData putData = new PutData(ServerConstants.SERVER_BASE_URL +"forgot.php","POST",field,data);
                            if(putData.startPut()){
                                if (putData.onComplete()){
                                    String result = putData.getResult();
                                    if(result.equals("Message has been sent")) {
                                        Toast.makeText(getApplicationContext(), "Reset Password Link has been sent to your EmailID", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(forgotPassword.this, login.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        emailID.setText(result);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}