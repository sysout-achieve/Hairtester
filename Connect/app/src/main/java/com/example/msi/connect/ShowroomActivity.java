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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ShowroomActivity extends AppCompatActivity {

    String text, img, date, userID, staffid;
    TextView txt_read, clickedid, date_read, heart_num;
    ImageView staffimg, read_img, heart_img;

    int heart, num;
    int check_upda = 0;


    public Bitmap StringToBitMap(String encodedString) { // 스트링으로 받은 이미지를 비트맵으로 다시 변환
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void Requestlike(int num, String writeID, String likeID, int check_upda) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int check = jsonResponse.getInt("check");
                    int clicked = jsonResponse.getInt("clicked");
                    heart_num.setText(clicked+"");
                    if (check == 0) {
                        heart_img.setImageResource(R.drawable.non_heart);
                    } else {
                        heart_img.setImageResource(R.drawable.heart);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        AddlikeRequest addlikeRequest = new AddlikeRequest(num, writeID, likeID, check_upda, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ShowroomActivity.this);
        queue.add(addlikeRequest);
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
        heart = intent.getIntExtra("heart", 0);
        num = intent.getIntExtra("num", 0);
        Requestlike(num, staffid, userID, check_upda);
        check_upda = 1;
        // 클릭한 타임라인 정보 표시
        txt_read = (TextView) findViewById(R.id.read_txt);
        date_read = (TextView) findViewById(R.id.read_date);
        heart_num = (TextView) findViewById(R.id.heart_num);
        read_img = (ImageView) findViewById(R.id.read_picture_img);
        heart_img = (ImageView) findViewById(R.id.heart);

        txt_read.setText(text);
        date_read.setText(date);
        // 스텝 정보 표시
        staffimg = (ImageView) findViewById(R.id.id_img_read);
        clickedid = (TextView) findViewById(R.id.id_txt_read);
        clickedid.setText(staffid);
        if (!img.equals(null)) {
            Bitmap bitmap = StringToBitMap(img);
            read_img.setImageBitmap(bitmap);
            read_img.setVisibility(View.VISIBLE);
        }

        heart_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heart == 0) {
                    Requestlike(num, staffid, userID, check_upda);
                } else {
                    Requestlike(num, staffid, userID, check_upda);
                }

            }
        });
    }
}
