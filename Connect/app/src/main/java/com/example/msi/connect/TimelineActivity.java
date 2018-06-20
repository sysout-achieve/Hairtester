package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    String userID, staffid;
    String linename = "time";
    private ArrayList<ShowroomItem> showroomItems;
    PicRecyclerAdapter picRecyclerAdapter;
    int paging_num = 0;

    private void receiveArray_picture(String dataObject) {
        showroomItems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                showroomItems.add(new ShowroomItem(dataJsonObject.getString("text"), dataJsonObject.getString("img"), dataJsonObject.getString("date"), dataJsonObject.getInt("heart"), dataJsonObject.getInt("num")));
            }
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            picRecyclerAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        textView.setText(staffid);
        ImageButton bactbtn = (ImageButton) findViewById(R.id.btnBack_staff);
        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        staffid = intent.getStringExtra("staffid");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyview_timeline);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        showroomItems = new ArrayList<>();
        picRecyclerAdapter = new PicRecyclerAdapter(showroomItems);
        recyclerView.setAdapter(picRecyclerAdapter);

        Response.Listener<String> responseListener_picture = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    receiveArray_picture(jsonResponse.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        PictureCallRequest pictureCallRequest = new PictureCallRequest(userID, staffid, linename, paging_num, responseListener_picture);
        RequestQueue queue = Volley.newRequestQueue(TimelineActivity.this);
        queue.add(pictureCallRequest);

        /*리사이클러 뷰 시술 기록 아이템 터치*/
        final GestureDetector gestureDetector = new GestureDetector(TimelineActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String text = showroomItems.get(position).gettext();
                String img = showroomItems.get(position).getimg();
                String date = showroomItems.get(position).getdate();
                int heart = showroomItems.get(position).getheart();
                int num = showroomItems.get(position).getnum();
                Intent intent = new Intent(TimelineActivity.this, ShowroomActivity.class);
                intent.putExtra("text", text);
                intent.putExtra("img", img);
                intent.putExtra("date", date);
                intent.putExtra("heart", heart);
                intent.putExtra("num", num);
                intent.putExtra("staffid", staffid);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        /*아이템 터치 fin.*/

    }
}

class PicRecyclerAdapter extends RecyclerView.Adapter<PicRecyclerAdapter.ViewHolder> {
    private ArrayList<ShowroomItem> showroomItems;
    Context context;

    @Override
    public PicRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pictueritem, parent, false);
        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
//        TextView picture_text, picture_img, picture_heart, picture_date, picture_num;

        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.recycleimg);
//            picture_text = (TextView) view.findViewById(R.id.picture_text);
//            picture_img = (TextView) view.findViewById(R.id.picture_img);
//            picture_heart = (TextView) view.findViewById(R.id.picture_heart);
//            picture_date = (TextView) view.findViewById(R.id.picture_date);
//            picture_num = (TextView) view.findViewById(R.id.picture_num);
        }
    }

    public PicRecyclerAdapter(ArrayList<ShowroomItem> mdataset) {
        showroomItems = mdataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.picture_text.setText(showroomItems.get(position).gettext());
//        holder.picture_img.setText(showroomItems.get(position).getimg());
//        holder.picture_heart.setText(showroomItems.get(position).getheart()+"");
//        holder.picture_date.setText(showroomItems.get(position).getdate());
//        holder.picture_num.setText(showroomItems.get(position).getnum());
        holder.img.setImageBitmap(StringToBitMap(showroomItems.get(position).getimg()));
    }

    public Bitmap StringToBitMap(String encodedString) { // 스트링으로 받은 이미지를 비트맵으로 다시 변환
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return showroomItems.size();
    }
}//PicRecyclerAdapter fin.

