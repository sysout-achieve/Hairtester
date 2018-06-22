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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Maintain_ReviewActivity extends AppCompatActivity {

    String userID, username;
    String staffid = "no_id";
    int review_num;
    int write_review_code = 80;
    String servicea = "전체";
    String check_kind = "구매";
    private ArrayList<OrderItem> orderItems;
    private ArrayList<ReviewItem> reviewItems;
    RecyclerView do_review, done_review;
    MaintainAdapter maintainAdapter;
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain__review);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        username = intent.getStringExtra("username");
        do_review = (RecyclerView) findViewById(R.id.do_review);
        done_review = (RecyclerView) findViewById(R.id.done_review);

        //do_review 리사이클러뷰에 maintainAdapter 적용
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        do_review.setLayoutManager(layoutManager);
        orderItems = new ArrayList<>();
        maintainAdapter = new MaintainAdapter(orderItems);
        do_review.setAdapter(maintainAdapter);
        serverRequest(check_kind);
        //done_review 리사이클러뷰에 reviewAdapter 적용
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        done_review.setLayoutManager(layoutManager1);

        reviewItems = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewItems);
        done_review.setAdapter(reviewAdapter);
        reviewRequest(userID, staffid);

        /*리사이클러뷰 리뷰 작성 가능 아이템 터치*/
        do_review.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), do_review, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String staffid = orderItems.get(position).getseller();
                review_num = orderItems.get(position).getnum();
                Intent intent = new Intent(Maintain_ReviewActivity.this, ReviewWriteActivity.class);
                intent.putExtra("staffid", staffid);
                intent.putExtra("userID", userID);
                intent.putExtra("review_num", review_num);
                startActivityForResult(intent, write_review_code);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        /*아이템 터치 fin.*/
        /*리사이클러뷰 리뷰 작성 가능 아이템 터치*/
        done_review.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), do_review, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onLongItemClick(View view, int position) {
                //롱클릭으로 리뷰 삭제 기능 추가
                /**/
            }
        }));
        /*아이템 터치 fin.*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == write_review_code && resultCode == 30 ){
            serverRequest(check_kind);
            reviewRequest(userID, staffid);
        }
    }

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
                        dataJsonObject.getString("seller"), dataJsonObject.getString("serviceable"), dataJsonObject.getInt("num"), dataJsonObject.getInt("autho_review")));
            }
            int length = jsonArray.length();
            if(length == 0){
                do_review.setVisibility(View.GONE);
            } else {
                do_review.setVisibility(View.VISIBLE);
            }
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            maintainAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void serverRequest(String check_kind) {
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
        RequestQueue queue = Volley.newRequestQueue(Maintain_ReviewActivity.this);
        queue.add(orderRequest);
    }

    private void receiveArray_review(String dataObject) {
        reviewItems.clear();
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
                    reviewItems.add(new ReviewItem(dataJsonObject.getString("review_title"), dataJsonObject.getString("review_content"),
                            dataJsonObject.getDouble("rating"), dataJsonObject.getString("buyer"), dataJsonObject.getString("date")));
                }
                int length = jsonArray.length();
                if(length == 0){
                    done_review.setVisibility(View.GONE);
                } else {
                    done_review.setVisibility(View.VISIBLE);
                }

                // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
                reviewAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void reviewRequest(String userID, String staffid) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    receiveArray_review(jsonResponse.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ReviewCallRequest reviewCallRequest = new ReviewCallRequest(userID, staffid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Maintain_ReviewActivity.this);
        queue.add(reviewCallRequest);
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
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("리뷰 관리");
        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }
}

class MaintainAdapter extends RecyclerView.Adapter<MaintainAdapter.ViewHolder> {
    Context context;
    private ArrayList<OrderItem> orderItem;

    @Override
    public MaintainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //xml 디자인한 부분 적용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintain_reviewitem, parent, false);
        MaintainAdapter.ViewHolder vh = new MaintainAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(MaintainAdapter.ViewHolder holder, int position) {
        if(orderItem.get(position).getAutho_review() == 1){
            holder.maintain_title_txt.setText(orderItem.get(position).getpd_title());
            holder.maintain_price_txt.setText(orderItem.get(position).getprice()+" 원");
            holder.maintain_seller_txt.setText(orderItem.get(position).getseller());
            holder.maintain_date_txt.setText(orderItem.get(position).getdate());
            holder.maintain_serviceable_txt.setText("사용 완료");
            holder.maintain_serviceable_txt.setTextColor(Color.RED);
            holder.maintain_orderlayout.setBackgroundColor(Color.WHITE);
        } else {
            holder.maintain_orderlayout.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView maintain_title_txt, maintain_price_txt, maintain_seller_txt, maintain_serviceable_txt, maintain_date_txt;
        public LinearLayout maintain_orderlayout;
        public ViewHolder(View view) {
            super(view);
            maintain_title_txt = (TextView) view.findViewById(R.id.maintain_title_txt);
            maintain_price_txt = (TextView) view.findViewById(R.id.maintain_price_txt);
            maintain_seller_txt = (TextView) view.findViewById(R.id.maintain_seller_txt);
            maintain_serviceable_txt = (TextView) view.findViewById(R.id.maintain_serviceable_txt);
            maintain_date_txt = (TextView) view.findViewById(R.id.maintain_date_txt);
            maintain_orderlayout = (LinearLayout) view.findViewById(R.id.maintain_orderlayout);
        }
    }

    public MaintainAdapter(ArrayList<OrderItem> mdataset) {
        orderItem = mdataset;
    }

    @Override
    public int getItemCount() {
        //아이템을 측정하는 카운터
        return orderItem.size();
    }
}

