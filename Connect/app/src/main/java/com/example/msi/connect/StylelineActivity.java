package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StylelineActivity extends AppCompatActivity {

    Spinner array_spin;
    String userID, userName;
    String linename = "최신순";
    int paging_num = 5;
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바

    /* linename은 서버에 저장할 정도로 중요한 정보가 아니지만 사용자의 편의성을 위해서 지정했던 정렬 방식을
    기억하는 것이 좋을 것이라고 판단함. SharedPreferences를 이용해서 각 아이디에 해당하는 정보를 저장하기로 함
    다른 기기로 변경 시 어떤 정렬방식을 사용자가 원하는지 알 수 없지만, 중요도가 낮다고 판단함 */

    private ArrayList<Stylelineitem> stylelineitems;
    RecyclerView recy_styleline;
    StylelineAdaper stylelineAdaper;

    private void receiveArray_picture(String dataObject) {
        stylelineitems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                stylelineitems.add(new Stylelineitem(dataJsonObject.getString("myid"), dataJsonObject.getString("text"), dataJsonObject.getString("img"), dataJsonObject.getString("date"), dataJsonObject.getInt("heart"), dataJsonObject.getInt("num"), dataJsonObject.getInt("chk_like")));
            }
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            /*
            * http://isntyet.tistory.com/114 리사이클러뷰 다운 스크롤 페이징 생각 다시 해보자
             * 아래로 내릴 때마다 모든 항목이 갱신되기때문에 스크롤을 내릴수록(많은 이미지를 볼 수록) 페이징이 갱신 시간이 길어짐
             * 이 부분을 해결해야함
            * */
            stylelineAdaper.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            if (length < paging_num - 2) {
                paging_num = 0;
            } else {
                paging_num = paging_num + 5;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void serverRequest(String userID, String linename) {
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
        PictureCallRequest pictureCallRequest = new PictureCallRequest(userID, userID, linename, paging_num, responseListener_picture);
        RequestQueue queue = Volley.newRequestQueue(StylelineActivity.this);
        queue.add(pictureCallRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styleline);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");

        recy_styleline = (RecyclerView) findViewById(R.id.recy_styleline);
        array_spin = (Spinner) findViewById(R.id.array_spin);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        //Linearlayout manager 사용
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recy_styleline.setLayoutManager(layoutManager);
        //
        stylelineitems = new ArrayList<>();
        stylelineAdaper = new StylelineAdaper(stylelineitems);
        recy_styleline.setAdapter(stylelineAdaper);
//        serverRequest(userID, linename);
        recy_styleline.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recy_styleline.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)) {
                    if (paging_num != 0) {
                        progressBar.setVisibility(View.VISIBLE);
                        serverRequest(userID, linename);
                    }
                }
            }
        });

        array_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                linename = parent.getItemAtPosition(position).toString();
                serverRequest(userID, linename);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

class StylelineAdaper extends RecyclerView.Adapter<StylelineAdaper.ViewHolder> {

    Context context;
    private ArrayList<Stylelineitem> stylelineitems;
    ImageConvert imageConvert = new ImageConvert();

    @Override
    public StylelineAdaper.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //xml 디자인한 부분 적용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_showroom, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView read_picture_img, heart;
        public TextView read_text, id_txt_read, heart_num, read_date;

        public ViewHolder(View view) {
            super(view);
            read_picture_img = (ImageView) view.findViewById(R.id.read_picture_img);
            heart = (ImageView) view.findViewById(R.id.heart);
            read_text = (TextView) view.findViewById(R.id.read_txt);
            id_txt_read = (TextView) view.findViewById(R.id.id_txt_read);
            heart_num = (TextView) view.findViewById(R.id.heart_num);
            read_date = (TextView) view.findViewById(R.id.read_date);
        }
    }

    public StylelineAdaper(ArrayList<Stylelineitem> mdataset) {
        stylelineitems = mdataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.read_text.setText(stylelineitems.get(position).gettext());
        holder.id_txt_read.setText(stylelineitems.get(position).getid());
        holder.heart_num.setText(stylelineitems.get(position).getheart() + "");
        holder.read_date.setText(stylelineitems.get(position).getdate());
        holder.read_picture_img.setImageBitmap(imageConvert.StringToBitMap(stylelineitems.get(position).getimg()));
        if (stylelineitems.get(position).getChk_heart() == 1) {
            holder.heart.setImageResource(R.drawable.heart);
        } else {
            holder.heart.setImageResource(R.drawable.non_heart);
        }
    }

    @Override
    public int getItemCount() {
        //아이템을 측정하는 카운터
        return stylelineitems.size();
    }
}