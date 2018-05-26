package com.example.msi.connect;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Staff_profile extends AppCompatActivity {

    ImageView staffimg, chatimg, mapimg, friend_btn, friend_btn2;
    TextView staffname_txt, staffid_txt;
    String userID, userName, staffid, staffname, place;
    //String으로 데이터 할당해둠
    int addcheck;
    String servicelist, address;
    FloatingActionButton showroom;

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
        userName = intent.getStringExtra("userName");

        showroom = (FloatingActionButton) findViewById(R.id.floatingActionButton2);

        staffimg = (ImageView) findViewById(R.id.staff_img);
        chatimg = (ImageView) findViewById(R.id.chat_img);
        mapimg = (ImageView) findViewById(R.id.map_img);
        friend_btn = (ImageView) findViewById(R.id.friend_btn);
        friend_btn2 = (ImageView) findViewById(R.id.friend_btn2);

        staffid_txt = (TextView) findViewById(R.id.staffid_txt);
        staffname_txt = (TextView) findViewById(R.id.staffname_txt);

        staffname_txt.setText(staffname);
        staffid_txt.setText("("+staffid+")");
        if(staffid == userID){
            chatimg.setVisibility(View.GONE);
            friend_btn.setVisibility(View.GONE);
            showroom.setVisibility(View.VISIBLE);
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                        addcheck = jsonResponse.getInt("add");
                    if(addcheck==1){
                        friend_btn.setVisibility(View.GONE);
                        friend_btn2.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        AddfriendRequest addfriendRequest = new AddfriendRequest(userID, staffid, staffname, 0, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
        queue.add(addfriendRequest);

        showroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, Addshowroom.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                startActivityForResult(intent, 1);
            }
        });

        chatimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, ChatActivity.class);
                intent.putExtra("staffid", staffid);
                intent.putExtra("staffname", staffname);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
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

        friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            addcheck = jsonResponse.getInt("add");

                                friend_btn.setVisibility(View.GONE);
                                friend_btn2.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                AddfriendRequest addfriendRequest = new AddfriendRequest(userID, staffid, staffname, 1, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
                queue.add(addfriendRequest);

                Toast.makeText(Staff_profile.this, "친구를 추가하셨습니다.", Toast.LENGTH_SHORT).show();

            }
        });
        friend_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            addcheck = jsonResponse.getInt("add");

                                friend_btn.setVisibility(View.VISIBLE);
                                friend_btn2.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                AddfriendRequest addfriendRequest = new AddfriendRequest(userID, staffid, staffname, 1, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
                queue.add(addfriendRequest);

                Toast.makeText(Staff_profile.this, "친구를 삭제하셨습니다.", Toast.LENGTH_SHORT).show();
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
//
//    private int receivecheck(String dataObject){
//        try {
//            // String 으로 들어온 값 JSONObject 로 1차 파싱
//            JSONObject wrapObject = new JSONObject(dataObject);
//            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
//
//            // Array 에서 하나의 JSONObject 를 추출
//            JSONObject dataJsonObject = jsonArray.getJSONObject(0);
//            int addcheck = Integer.parseInt(dataJsonObject.getString("add"));
//            return addcheck;
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }
//        return 0;
//    }
}
