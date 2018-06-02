package com.example.msi.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;


public class Profile_selectActivity extends NMapActivity {

    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "hLyMlpTWmtG7h4USaSVb";// 애플리케이션 클라이언트 아이디 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = new NMapView(this);
        setContentView(mMapView);
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();


    }
}




