package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    FloatingActionButton write_rebtn;
    String userID, staffid;
    private ArrayList<ReviewItem> reviewItems;
    ReviewAdapter reviewAdapter;
    RecyclerView review_recy_view;
    TextView rate_scrore, rev_member, review_text;
    RatingBar ratebar_rev;
    RelativeLayout relativelay;
    LinearLayout Linear;

    private void receiveArray_review(String dataObject) {
        Double rating = 0.0;
        reviewItems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);

            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            JSONObject dataJsonObject_autho_check = jsonArray.getJSONObject(0);
            String check_autho = dataJsonObject_autho_check.getString("review_autho");
            if(check_autho.equals("true")){
                write_rebtn.setVisibility(View.VISIBLE);
                review_text.setVisibility(View.VISIBLE);
            }

            if(jsonArray.length() == 0){
                relativelay.setBackgroundResource(R.drawable.firstreview);
                Linear.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    // Array 에서 하나의 JSONObject 를 추출
                    JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                    // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                    // 필자는 RecyclerView 로 데이터를 표시 함
                    rating = dataJsonObject.getDouble("rating") + rating;
                    reviewItems.add(new ReviewItem(dataJsonObject.getString("review_title"), dataJsonObject.getString("review_content"),
                            dataJsonObject.getDouble("rating"), dataJsonObject.getString("buyer"), dataJsonObject.getString("date")));
                }
                Double finalrate = Double.valueOf(Math.round(rating * 100 / jsonArray.length())) / 100;
                rev_member.setText(jsonArray.length() + " 개의 리뷰");
                rate_scrore.setText(finalrate + "");
                ratebar_rev.setRating(finalrate.floatValue());
                // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
                reviewAdapter.notifyDataSetChanged();
            }
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
        RequestQueue queue = Volley.newRequestQueue(ReviewActivity.this);
        queue.add(reviewCallRequest);
    }

    @Override
    protected void onResume() {
        reviewRequest(userID, staffid);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        staffid = intent.getStringExtra("staffid");
        Linear = (LinearLayout) findViewById(R.id.Linear);
        review_text = (TextView) findViewById(R.id.review_text);
        review_recy_view = (RecyclerView) findViewById(R.id.review_recy_view);
        /*리뷰 탭의 별점 평균, 리뷰 갯수*/
        rate_scrore = (TextView) findViewById(R.id.rate_score);
        rev_member = (TextView) findViewById(R.id.rev_member);
        ratebar_rev = (RatingBar) findViewById(R.id.ratebar_rev);
        relativelay = (RelativeLayout) findViewById(R.id.relativelay);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        review_recy_view.setLayoutManager(layoutManager);

        reviewItems = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewItems);
        review_recy_view.setAdapter(reviewAdapter);

        reviewRequest(userID, staffid);
        // 리뷰 버튼은 권한이 있어야만 보이게 변경 -> 서비스를 받을 때마다 해당 스텝에게 한 개의 리뷰를 작성할 수 있는 권한을 줌
        write_rebtn = (FloatingActionButton) findViewById(R.id.write_rebtn);

        write_rebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, ReviewWriteActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                startActivityForResult(intent, 30);
            }
        });
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
        View actionbar = inflater.inflate(R.layout.staff_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        TextView textView = (TextView) findViewById(R.id.title_staff);
        textView.setText(staffid + " (리뷰)");
        ImageButton bactbtn = (ImageButton) findViewById(R.id.btnBack_staff);
        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }
}

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<ReviewItem> reviewItems;
    Context context;

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewitem, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView review_title_all, review_all, review_rat_all, reviewid_all, review_date_all;
        ImageView rev_img;
        public ViewHolder(View view) {
            super(view);
            rev_img = view.findViewById(R.id.rev_img);
            review_title_all = view.findViewById(R.id.review_title_all);
            review_all = view.findViewById(R.id.review_all);
            review_rat_all = view.findViewById(R.id.review_rat_all);
            reviewid_all = view.findViewById(R.id.reviewid_all);
            review_date_all = view.findViewById(R.id.review_date_all);
        }
    }

    public ReviewAdapter(ArrayList<ReviewItem> mdataset) {
        reviewItems = mdataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.rev_img.setImageResource(R.drawable.star);
        holder.review_title_all.setText(reviewItems.get(position).getReview_title());
        holder.review_all.setText(reviewItems.get(position).getReview_content());
        holder.review_rat_all.setText(reviewItems.get(position).getReview_rat() + "");
        holder.reviewid_all.setText(reviewItems.get(position).getReview_id());
        holder.review_date_all.setText(reviewItems.get(position).getReview_date());
    }

    @Override
    public int getItemCount() {
        return reviewItems.size();
    }
}//PicRecyclerAdapter fin.

