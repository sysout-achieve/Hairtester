package com.example.msi.connect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String userID, userName, userAge, profile_img_string;
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //로그인 정보 intent로 get.
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        userAge = intent.getStringExtra("userAge")+"";
        profile_img_string = intent.getStringExtra("profile_img_string");



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        ImageView profile_img = (ImageView) findViewById(R.id.profile_img);
        TextView txt_id = (TextView) findViewById(R.id.id_txt);
        if(profile_img_string != null){
            Picasso.with(this).load(profile_img_string).into(profile_img);
        }
        txt_id.setText(userName);
        return true;
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
                            intent.putExtra("userinfo",true);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("profile_img_string", profile_img_string);
                            intent.putExtra("userName", userName);
                            intent.putExtra("userAge", userAge);
                            intent.putExtra("userinfo",false);
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
//            Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
//            intent.putExtra("userID",userID);
//            intent.putExtra("userAge", userAge);
//            intent.putExtra("userName", userName);
//            intent.putExtra("profile_img_string", profile_img_string);
//            startActivity(intent);
        } else if (id == R.id.reservation) {

        } else if (id == R.id.visitagain) {

        } else if (id == R.id.review) {

        } else if (id == R.id.settings) {

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
