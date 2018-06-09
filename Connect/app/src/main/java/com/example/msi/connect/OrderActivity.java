package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private ArrayList<OrderItem> orderItems;
    RecyclerView view;
    OrderAdapter orderAdapter;
    String userID;
    Spinner kind, expire;
    String check_kind = "구매";
    String servicea = "전체";

    private void receiveArray(String dataObject) {
        orderItems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);

            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            for (int i = 0; i < jsonArray.length(); i++) {
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                // 필자는 RecyclerView 로 데이터를 표시 함
                orderItems.add(new OrderItem(dataJsonObject.getString("pd_title"), dataJsonObject.getString("buyer"),
                        dataJsonObject.getString("phone"), dataJsonObject.getString("price"), dataJsonObject.getString("date"),
                        dataJsonObject.getString("seller"), dataJsonObject.getString("serviceable"), dataJsonObject.getInt("num")));
            }
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            orderAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void serverRequest() {
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
        OrderRequest orderRequest = new OrderRequest(userID, check_kind, servicea, responseListener);
        RequestQueue queue = Volley.newRequestQueue(OrderActivity.this);
        queue.add(orderRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serverRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        view = (RecyclerView) findViewById(R.id.recy_order);
        //Linearlayout manager 사용
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        view.setLayoutManager(layoutManager);
        kind = (Spinner) findViewById(R.id.kind_spin);
        expire = (Spinner) findViewById(R.id.exp_spin);

        orderItems = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderItems);
        view.setAdapter(orderAdapter);
        serverRequest();

        kind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                check_kind = parent.getItemAtPosition(position).toString();
                serverRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        expire.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                servicea = parent.getItemAtPosition(position).toString();
                serverRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(OrderActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        view.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String seller = orderItems.get(position).getseller();
                String buyer = orderItems.get(position).getbuyer();
                String pd_title = orderItems.get(position).getpd_title();
                String phone = orderItems.get(position).getphone();
                String price = orderItems.get(position).getprice();
                String date = orderItems.get(position).getdate();
                String serviceable = orderItems.get(position).getserviceable();
                int num = orderItems.get(position).getnum();
                Intent intent = new Intent(OrderActivity.this, OrderCheckActivity.class);
                intent.putExtra("seller", seller);
                intent.putExtra("buyer", buyer);
                intent.putExtra("pd_title", pd_title);
                intent.putExtra("phone", phone);
                intent.putExtra("price", price);
                intent.putExtra("date", date);
                intent.putExtra("serviceable", serviceable);
                intent.putExtra("userID", userID);
                intent.putExtra("num", num);

                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }


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


    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        Context context;
        private ArrayList<OrderItem> orderItem;

        @Override
        public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //xml 디자인한 부분 적용
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderitem, parent, false);
            OrderAdapter.ViewHolder vh = new OrderAdapter.ViewHolder(view);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.pd_title_txt.setText(orderItem.get(position).getpd_title());
            holder.buyer_txt.setText(orderItem.get(position).getbuyer());
            holder.phone_txt.setText(orderItem.get(position).getphone());
            holder.price_txt.setText(orderItem.get(position).getprice());
            holder.seller_txt.setText(orderItem.get(position).getseller());
            holder.date_txt.setText(orderItem.get(position).getdate());
            if (orderItem.get(position).getserviceable().equals("1")) {
                holder.serviceable_txt.setText("사용 가능");
                holder.serviceable_txt.setTextColor(Color.BLACK);
                holder.orderlayout.setBackgroundColor(Color.WHITE);
            } else {
                holder.serviceable_txt.setText("사용 완료");
                holder.serviceable_txt.setTextColor(Color.RED);
                holder.orderlayout.setBackgroundColor(Color.LTGRAY);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView pd_title_txt, buyer_txt, phone_txt, price_txt, seller_txt, serviceable_txt, date_txt;
            public LinearLayout orderlayout;
            public ViewHolder(View view) {
                super(view);
                pd_title_txt = (TextView) view.findViewById(R.id.pd_title_txt);
                buyer_txt = (TextView) view.findViewById(R.id.buyer_txt);
                phone_txt = (TextView) view.findViewById(R.id.phone_txt);
                price_txt = (TextView) view.findViewById(R.id.price_txt);
                seller_txt = (TextView) view.findViewById(R.id.seller_txt);
                serviceable_txt = (TextView) view.findViewById(R.id.serviceable_txt);
                date_txt = (TextView) view.findViewById(R.id.date_txt);
                orderlayout = (LinearLayout) view.findViewById(R.id.orderlayout);
            }
        }

        public OrderAdapter(ArrayList<OrderItem> mdataset) {
            orderItem = mdataset;
        }

        @Override
        public int getItemCount() {
            //아이템을 측정하는 카운터
            return orderItem.size();
        }
    }
}
