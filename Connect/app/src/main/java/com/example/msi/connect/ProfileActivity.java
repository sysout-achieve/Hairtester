package com.example.msi.connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
//        String info = intent.getStringExtra("info");
        String userID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");
        String userAge = intent.getStringExtra("userAge")+"";


        TextView showID = (TextView) findViewById(R.id.showID);
        TextView showName = (TextView) findViewById(R.id.showName);
        TextView showAge = (TextView) findViewById(R.id.showAge);
//        TextView agetxt = (TextView) findViewById(R.id.agetxt);
//        agetxt.setText(info);
        showID.setText(userID);
        showName.setText(userName);
        showAge.setText(userAge+"");
    }
}
