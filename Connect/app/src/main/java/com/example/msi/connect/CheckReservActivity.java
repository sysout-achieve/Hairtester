package com.example.msi.connect;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CheckReservActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    ScrollView scrollView;

    TextView salename, saleprice, salecompo, saletime, salecaution, saleshop, reserv_staffid;
    EditText editID, phone, require_txt;
    CheckBox checkBoxall, checkBox1, checkBox2, checkBox3;
    RadioButton radioButton;
    Button buy_btn;
    String str_salename, str_saleprice, str_saletime, str_salecaution;
    String userID, staffid, staffaddress, place;
    Double locateX, locateY;

    public Double setlocateX(String address) {
        String split[] = address.split(":");
        String front = split[1].replace("(", "");
        String splitX[] = front.split(",");
        Double locateX = Double.valueOf(splitX[0]);

        return locateX;
    }

    public Double setlocateY(String address) {
        String split[] = address.split(",");
        String back = split[1].replace(")", "");
        Double locateY = Double.valueOf(back);
        return locateY;
    }

    private void checkboxctrl(CheckBox checkBox) {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox1.isChecked() || !checkBox2.isChecked() || !checkBox3.isChecked()) {
                    checkBoxall.setChecked(false);
                    clickablebtn(buy_btn);
                } else if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                    checkBoxall.setChecked(true);
                    clickablebtn(buy_btn);
                }
            }
        });
    }

    private void clickablebtn(Button buy_btn) {
        if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && !editID.getText().toString().equals("") && !phone.getText().toString().equals("") && radioButton.isChecked()) {
            buy_btn.setEnabled(true);
        } else {
            buy_btn.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reserv);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        staffid = intent.getStringExtra("staffid");
        str_salename = intent.getStringExtra("method");
        str_saleprice = intent.getStringExtra("price");
        str_saletime = intent.getStringExtra("time");
        staffaddress = intent.getStringExtra("staffaddress");
        place = intent.getStringExtra("staffplace");
        locateX = setlocateX(staffaddress);
        locateY = setlocateY(staffaddress);

        TabHost tabs = (TabHost) findViewById(R.id.tabs1);
        tabs.setup();
        TabHost.TabSpec spec1 = tabs.newTabSpec("정보");
        spec1.setIndicator("정보");
        spec1.setContent(R.id.정보);
        tabs.addTab(spec1);

        TabHost.TabSpec spec2 = tabs.newTabSpec("리뷰");
        spec2.setIndicator("리뷰");
        spec2.setContent(R.id.리뷰);
        tabs.addTab(spec2);

        salename = (TextView) findViewById(R.id.salename);
        saleprice = (TextView) findViewById(R.id.saleprice);
        saletime = (TextView) findViewById(R.id.saletime);
        salecaution = (TextView) findViewById(R.id.salecaution);
        salecompo = (TextView) findViewById(R.id.salecompo);
        saleshop = (TextView) findViewById(R.id.saleshop);
        reserv_staffid = (TextView) findViewById(R.id.reserv_staffid);
        scrollView = (ScrollView) findViewById(R.id.scrollview);

        radioButton = (RadioButton) findViewById(R.id.radio1);
        editID = (EditText) findViewById(R.id.editID);
        phone = (EditText) findViewById(R.id.phone);
        require_txt = (EditText) findViewById(R.id.require_txt);

        checkBoxall = (CheckBox) findViewById(R.id.checkboxall);
        checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkbox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkbox3);
        buy_btn = (Button) findViewById(R.id.btn_buy);

        editID.setFocusable(false);
        phone.setFocusable(false);
        require_txt.setFocusable(false);
        //map 삽입
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.innermap);
        mapFragment.getMapAsync(this); // getMapAsync must be called on the main thread.

        editID.setText(userID);
        salename.setText(str_salename);
        salecompo.setText(str_salename);
        saleprice.setText(str_saleprice);
        saletime.setText(str_saletime);
        salecaution.setText("기장에 따라 요금이 다를 수 있습니다.\n(커트 제외)");
        saleshop.setText(place);
        reserv_staffid.setText(staffid);
        buy_btn.setEnabled(false);

        //시작 시 스크롤 가장 위로
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
                scrollView.pageScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0, 0);
            }
        });

        checkBoxall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxall.isChecked()) {
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    clickablebtn(buy_btn);
                } else {
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    clickablebtn(buy_btn);
                }
            }
        });

        checkboxctrl(checkBox1);
        checkboxctrl(checkBox2);
        checkboxctrl(checkBox3);

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckReservActivity.this, KakaoActivity.class);
                startActivity(intent);
            }
        });

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickablebtn(buy_btn);
            }
        });

        editID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editID.setFocusableInTouchMode(true);
                editID.setClickable(true);
                editID.setFocusable(true);
                clickablebtn(buy_btn);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.setFocusableInTouchMode(true);
                phone.setClickable(true);
                phone.setFocusable(true);
                clickablebtn(buy_btn);
            }
        });
        require_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                require_txt.setFocusableInTouchMode(true);
                require_txt.setClickable(true);
                require_txt.setFocusable(true);
                clickablebtn(buy_btn);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng stafflocation = new LatLng(locateX, locateY);
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(stafflocation).title(place);

        mMap.addMarker(makerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stafflocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
