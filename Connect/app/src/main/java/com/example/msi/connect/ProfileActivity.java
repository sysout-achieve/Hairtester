package com.example.msi.connect;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v7.widget.Toolbar;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        String profile_img_string = intent.getStringExtra("profile_img_string");
        String userID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");
        String userAge = intent.getStringExtra("userAge")+"";

        ImageView profile_img = (ImageView) findViewById(R.id.profile_img);
        TextView showID = (TextView) findViewById(R.id.showID);
        TextView showAge = (TextView) findViewById(R.id.showAge);
        EditText nametxt = (EditText)findViewById(R.id.nicknametxt);



        showID.setText(userID);
        nametxt.setText(userName);
        showAge.setText(userAge+"");
        if(profile_img_string != null){
            Picasso.with(this).load(profile_img_string).into(profile_img);
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);
        ImageView bactbtn = (ImageView)findViewById(R.id.btnBack);
        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        return true;
    }
}




