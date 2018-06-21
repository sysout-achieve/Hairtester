package com.example.msi.connect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static MenuItem visitagain;
    FloatingActionButton fab;
    String userID, userName, userAge, profile_img_string;
    ProgressDialog mDialog;
    TextView rank, style;
    SharedPreferences loginID;
    private String ip = "http://13.125.234.222:3000";
    private static Socket mSocket;
    ProgressBar mainprogressBar;
    ScrollView mainView;
    TextView hotid1, hotid2, hotheart1, hotheart2;
    TextView all_style, all_designer;
    TextView designerid1, designerid2;
    ImageView hotimg1, hotimg2, designerimg1, designerimg2;


    {
        try {
            mSocket = IO.socket(ip);
        } catch (URISyntaxException e) {

        }
    }
    // 채팅이 왔을 때 인식함

    /* 삭제 가능 발표 후 검토 예정*/
    private Emitter.Listener handleInmcoming_chatlist = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String sendname;
                    String sendid;
                    try {
                        message = data.getString("message").toString();
                        sendid = data.getString("sendid").toString();
                        sendname = data.getString("sendname").toString();
                    } catch (JSONException e) {
                        return;
                    }
                    receiveMessage_chatroom(sendid, sendname, message, 1, userID);
                }
            });
        }
    };

    //채팅이 왔을 때 데이터 베이스로 저장
    private void receiveMessage_chatroom(String sendid, String sendName, String message, int readchk, String userID) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };
        AddchatlistRequest addchatlistRequest = new AddchatlistRequest(sendid, sendName, message, readchk, userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Main2Activity.this);
        queue.add(addchatlistRequest);
    }
    /*-------------발표 후 검토 fin.--------------*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mainprogressBar = (ProgressBar) findViewById(R.id.mainprogressBar);
        mainView = (ScrollView) findViewById(R.id.mainView);

        setSupportActionBar(toolbar);
        mSocket.connect();
        loginID = getSharedPreferences("loginID", MODE_PRIVATE);
        SharedPreferences.Editor loginIDedit = loginID.edit();

        hotid1 = (TextView) findViewById(R.id.hotid1);
        hotid2 = (TextView) findViewById(R.id.hotid2);
        hotheart1 = (TextView) findViewById(R.id.hotheart1);
        hotheart2 = (TextView) findViewById(R.id.hotheart2);
        designerid1 = (TextView) findViewById(R.id.designerid1);
        designerid2 = (TextView) findViewById(R.id.designerid2);
        all_style = (TextView) findViewById(R.id.all_style);
        all_designer = (TextView) findViewById(R.id.all_designer);
        hotimg1 = (ImageView) findViewById(R.id.hotimg1);
        hotimg2 = (ImageView) findViewById(R.id.hotimg2);
        designerimg1 = (ImageView) findViewById(R.id.designerimg1);
        designerimg2 = (ImageView) findViewById(R.id.designerimg2);

        //로그인 정보 intent로 get.
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        userAge = intent.getStringExtra("userAge") + "";
        profile_img_string = intent.getStringExtra("profile_img_string");


        if (userID != null) {
            loginIDedit.putString("loginID", userID).commit();
            loginIDedit.putString("loginName", userName).commit();
            loginIDedit.putString("loginAge", userAge).commit();
            loginIDedit.putString("loginProfile", profile_img_string).commit();
        } else {
            userID = loginID.getString("loginID", "null");
            userName = loginID.getString("loginName", "null");
            userAge = loginID.getString("loginAge", "null");
//            profile_img_string = loginID.getString("loginProfile","default");
        }

        rank = (TextView) findViewById(R.id.rank);
        style = (TextView) findViewById(R.id.style);

        mSocket.on("message", handleInmcoming_chatlist);
        mSocket.emit("connect_room", userID);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, Find_staffActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, StylelineActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, Staff_profile.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                intent.putExtra("staffid", userID);
                intent.putExtra("staffname", userName);
                startActivity(intent);
            }
        });

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = new JSONArray(jsonResponse.getString("response"));
                    JSONObject dataJsonObject = jsonArray.getJSONObject(0);
                    String staff = dataJsonObject.getString("staff");
                    if (staff.equals("true")) {
                        fab.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        StaffCallRequest staffCallRequest = new StaffCallRequest(userID, userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Main2Activity.this);
        queue.add(staffCallRequest);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    } //on Create fin.

    private void requestLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
//        menu.getItem(R.id.visitagain).setEnabled(true);
        ImageView profile_img = (ImageView) findViewById(R.id.profile_img);
        TextView txt_id = (TextView) findViewById(R.id.id_txt);
        if (profile_img_string != null) {
            Picasso.with(this).load(profile_img_string).into(profile_img);
        }
        txt_id.setText(userName);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.myinfo) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            String userID = jsonResponse.getString("userID");
                            String profile_img_string = jsonResponse.getString("profile_img_string");
                            String userName = jsonResponse.getString("nickName");
                            String userAge = jsonResponse.getString("userAge");
                            String kind_sex = jsonResponse.getString("kind_sex");
                            String kind_user = jsonResponse.getString("kind_user");
                            String experience = jsonResponse.getString("experience");
                            String list = jsonResponse.getString("list");
                            String place = jsonResponse.getString("place");
                            String address_str = jsonResponse.getString("address_str");
                            int checked_designer = jsonResponse.getInt("checked_designer");

                            Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("profile_img_string", profile_img_string);
                            intent.putExtra("userName", userName);
                            intent.putExtra("userAge", userAge);
                            intent.putExtra("kind_sex", kind_sex);
                            intent.putExtra("kind_user", kind_user);
                            intent.putExtra("experience", experience);
                            intent.putExtra("list", list);
                            intent.putExtra("place", place);
                            intent.putExtra("address_str", address_str);
                            intent.putExtra("checked_designer", checked_designer);
                            intent.putExtra("userinfo", true);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("profile_img_string", profile_img_string);
                            intent.putExtra("userName", userName);
                            intent.putExtra("userAge", userAge);
                            intent.putExtra("userinfo", false);
                            startActivity(intent);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ProfileRequest profileRequest = new ProfileRequest(userID, responseListener);
            RequestQueue queue = Volley.newRequestQueue(Main2Activity.this);
            queue.add(profileRequest);

        } else if (id == R.id.reservation) {
            Intent intent = new Intent(Main2Activity.this, ChatlistActivity.class);
            intent.putExtra("userID", userID);
            intent.putExtra("userName", userName);
            startActivity(intent);
        } else if (id == R.id.review) {
            Intent intent = new Intent(Main2Activity.this, Maintain_ReviewActivity.class);
            intent.putExtra("userID", userID);
            intent.putExtra("userName", userName);
            startActivity(intent);
        } else if (id == R.id.reserv) {
            Intent intent = new Intent(Main2Activity.this, OrderActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        } else if (id == R.id.logout) {
            mDialog = new ProgressDialog(Main2Activity.this);
            mDialog.setMessage("Logout...");
            mDialog.show();
            LoginManager.getInstance().logOut();
            requestLogout();
            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
