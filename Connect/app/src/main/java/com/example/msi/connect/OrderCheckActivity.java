package com.example.msi.connect;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class OrderCheckActivity extends AppCompatActivity {

    Button back_btn;
    ImageButton service_fin;
    Switch switch_fin;
    String userID, seller, buyer, pd_title, phone, price, date, serviceable;
    int num;
    TextView pdtitlecheck, buyercheck, sellercheck, phonecheck, pricecheck, datecheck, serviceablecheck;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.order_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        ImageButton bactbtn = (ImageButton) findViewById(R.id.btnBack_order);
        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderCheckActivity.this);
        builder.setTitle("서비스 완료");
        builder.setMessage("서비스를 완료하면 현재 선택한 쿠폰은 다시 사용이 불가합니다. 완료하시겠습니까?");
        builder.setNegativeButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                finish();
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                OrderFinish orderFinish = new OrderFinish(userID, num, responseListener);
                RequestQueue queue = Volley.newRequestQueue(OrderCheckActivity.this);
                queue.add(orderFinish);
            }
        });

        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check);

        back_btn = (Button) findViewById(R.id.backorder);
        service_fin = (ImageButton) findViewById(R.id.service_fin);
        switch_fin = (Switch) findViewById(R.id.switch_fin);
        service_fin.setEnabled(false);
        pdtitlecheck = (TextView) findViewById(R.id.pd_title_check);
        buyercheck = (TextView) findViewById(R.id.buyer_check);
        sellercheck = (TextView) findViewById(R.id.seller_check);
        phonecheck = (TextView) findViewById(R.id.phone_check);
        pricecheck = (TextView) findViewById(R.id.price_check);
        datecheck = (TextView) findViewById(R.id.date_check);
        serviceablecheck = (TextView) findViewById(R.id.serviceable_check);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        seller = intent.getStringExtra("seller");
        buyer = intent.getStringExtra("buyer");
        pd_title = intent.getStringExtra("pd_title");
        phone = intent.getStringExtra("phone");
        price = intent.getStringExtra("price");
        date = intent.getStringExtra("date");
        serviceable = intent.getStringExtra("serviceable");
        num = intent.getIntExtra("num", 0);

        pdtitlecheck.setText(pd_title);
        buyercheck.setText(buyer);
        sellercheck.setText(seller);
        phonecheck.setText(phone);
        pricecheck.setText(price);
        datecheck.setText(date);
        if (serviceable.equals("0")) {
            serviceablecheck.setText("사용 불가");
            serviceablecheck.setTextColor(Color.RED);
        } else {
            serviceablecheck.setText("사용 가능");
            if (userID.equals(seller)) {
                switch_fin.setVisibility(View.VISIBLE);
            }

        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch_fin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    service_fin.setEnabled(true);
                } else {
                    service_fin.setEnabled(false);
                }
            }
        });

        service_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
    }
}
