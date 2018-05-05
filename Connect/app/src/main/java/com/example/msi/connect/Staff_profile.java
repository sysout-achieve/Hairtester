package com.example.msi.connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Staff_profile extends AppCompatActivity {

    ImageView staffimg, chatimg, mapimg;
    TextView staffname_txt, staffid_txt;
    String userID, useName, staffid, staffname, place;
    //String으로 데이터 할당해둠

    String servicelist, address;

    @Override
    protected void onStart() {
        super.onStart();
        /* 서버에서 스텝 프로필 받기*/
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    receiveArray(jsonResponse.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        StaffProfileRequest StaffRequest = new StaffProfileRequest(staffid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
        queue.add(StaffRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        Intent intent = getIntent();
        staffid = intent.getStringExtra("staffid");
        staffname = intent.getStringExtra("staffname");
        userID = intent.getStringExtra("userID");
        useName = intent.getStringExtra("userName");

        staffimg = (ImageView) findViewById(R.id.staff_img);
        chatimg = (ImageView) findViewById(R.id.chat_img);
        mapimg = (ImageView) findViewById(R.id.map_img);
        staffid_txt = (TextView) findViewById(R.id.staffid_txt);
        staffname_txt = (TextView) findViewById(R.id.staffname_txt);

        staffname_txt.setText(staffname);
        staffid_txt.setText("("+staffid+")");


        chatimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, ChatActivity.class);
                intent.putExtra("staffid", staffid);
                intent.putExtra("userId", userID);
                startActivity(intent);
            }
        });

        mapimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, MapsActivity.class);
                intent.putExtra("get","get");
                intent.putExtra("staffaddress", address);
                intent.putExtra("staffplace", place);
                intent.putExtra("staffname", staffname);
                startActivity(intent);
            }
        });
    }
    private void receiveArray(String dataObject){
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);

            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));

                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(0);
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                // 필자는 RecyclerView 로 데이터를 표시 함
                String img = dataJsonObject.getString("profile_img_str");
                if(!img.equals("default") && img != null) {
                    Picasso.with(this).load(img).into(staffimg);
                }

                servicelist= dataJsonObject.getString("list");
                place = dataJsonObject.getString("place");
                address = dataJsonObject.getString("address");
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}
