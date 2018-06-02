package com.example.msi.connect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SetReservationActivity extends AppCompatActivity {

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reservation);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        final CheckBox cb1 = (CheckBox) findViewById(R.id.cut);
        final CheckBox cb2 = (CheckBox) findViewById(R.id.color);
        final CheckBox cb3 = (CheckBox) findViewById(R.id.perm);
        final CheckBox cb4 = (CheckBox) findViewById(R.id.clinic);

        Button save = (Button) findViewById(R.id.save_sale);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "";  // 결과를 출력할 문자열  ,  항상 스트링은 빈문자열로 초기화 하는 습관을 가지자
                if (cb1.isChecked() == true) {
                    result += 1;
                } else {
                    result += 0;
                }
                if (cb2.isChecked() == true) {
                    result += 1;
                } else {
                    result += 0;
                }
                if (cb3.isChecked() == true) {
                    result += 1;
                } else {
                    result += 0;
                }
                if (cb4.isChecked() == true) {
                    result += 1;
                } else {
                    result += 0;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SetReservationActivity.this);
                                builder.setMessage("쿠폰 목록을 저장했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create()
                                        .show();
                                setResult(5);
                                finish();
                            }else {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SetReservationActivity.this);
                                builder.setMessage("쿠폰 목록 저장에 실패했습니다.")
                                        .setNegativeButton("확인", null)
                                        .create()
                                        .show();
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                SavesaleRequest savesaleRequest = new SavesaleRequest(userID, result, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SetReservationActivity.this);
                queue.add(savesaleRequest);
            }
        });


    }
}
