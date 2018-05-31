package com.example.msi.connect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowroomActivity extends AppCompatActivity {

    String text, img, date, userID, staffid;
    TextView txt_read, clickedid, date_read;
    ImageView staffimg, read_img;

    public Bitmap StringToBitMap(String encodedString){ // 스트링으로 받은 이미지를 비트맵으로 다시 변환
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom);

        Intent intent = getIntent();
        text = intent.getStringExtra("text");
        img = intent.getStringExtra("img");
        date = intent.getStringExtra("date");
        staffid = intent.getStringExtra("staffid");
        userID = intent.getStringExtra("userID");

        // 클릭한 타임라인 정보 표시
        txt_read = (TextView) findViewById(R.id.read_txt);
        date_read = (TextView) findViewById(R.id.read_date);
        read_img = (ImageView) findViewById(R.id.read_picture_img);
        txt_read.setText(text);
        date_read.setText(date);
        // 스텝 정보 표시
        staffimg = (ImageView) findViewById(R.id.id_img_read);
        clickedid = (TextView) findViewById(R.id.id_txt_read);
        clickedid.setText(staffid);
        if(!img.equals(null)){
            Bitmap bitmap = StringToBitMap(img);
            read_img.setImageBitmap(bitmap);
            read_img.setVisibility(View.VISIBLE);
        }
    }
}
