package com.example.msi.connect;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SubmainActivity extends AppCompatActivity {

String userID;
String userName;
String userAge;
String profile_img_string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submain);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        userAge = intent.getStringExtra("userAge")+"";
        profile_img_string = intent.getStringExtra("profile_img_string");

        Button profile_btn = (Button) findViewById(R.id.profile_btn);

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });

        Button btn = (Button) findViewById(R.id.example);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SubmainActivity.this, Main2Activity.class);
                startActivity(intent1);
            }
        });

    }

    class BackgroundTask extends AsyncTask<Void, Void, String>{
        String target;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) !=null ) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            target = "http://13.125.234.222/new/profile.php";
        }

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(SubmainActivity.this, ProfileActivity.class);
            intent.putExtra("info",s);
            intent.putExtra("userID", userID );
            intent.putExtra("userName", userName);
            intent.putExtra("userAge", userAge);
            intent.putExtra("profile_img_string", profile_img_string);
            SubmainActivity.this.startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }



}
