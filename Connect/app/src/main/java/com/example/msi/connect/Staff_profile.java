package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Staff_profile extends AppCompatActivity {

    private RecyclerItemClickListener.OnItemClickListener mListener;
    ImageView staffimg, chatimg, mapimg, friend_btn, friend_btn2;
    TextView staffname_txt, staffid_txt;
    String userID, userName, staffid, staffname, place;
    //String으로 데이터 할당해둠
    int addcheck;
    String servicelist, address;
    FloatingActionButton showroom;
    private ArrayList<ShowroomItem> showroomItems;
    PicRecyclerAdapter picRecyclerAdapter;


    @Override
    protected void onStart() {
        super.onStart();
        /* 서버에서 스텝 프로필 받기*/
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
        StaffProfileRequest StaffRequest = new StaffProfileRequest(staffid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
        queue.add(StaffRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        PictureCallRequest pictureCallRequest = new PictureCallRequest(staffid, responseListener_picture);
        RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
        queue.add(pictureCallRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        final Intent intent = getIntent();
        staffid = intent.getStringExtra("staffid");
        staffname = intent.getStringExtra("staffname");
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        showroom = (FloatingActionButton) findViewById(R.id.floatingActionButton2);

        staffimg = (ImageView) findViewById(R.id.staff_img);
        chatimg = (ImageView) findViewById(R.id.chat_img);
        mapimg = (ImageView) findViewById(R.id.map_img);
        friend_btn = (ImageView) findViewById(R.id.friend_btn);
        friend_btn2 = (ImageView) findViewById(R.id.friend_btn2);

        staffid_txt = (TextView) findViewById(R.id.staffid_txt);
        staffname_txt = (TextView) findViewById(R.id.staffname_txt);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        showroomItems = new ArrayList<>();
        picRecyclerAdapter = new PicRecyclerAdapter(showroomItems);
        recyclerView.setAdapter(picRecyclerAdapter);

        staffname_txt.setText(staffname);
        staffid_txt.setText("("+staffid+")");
        if (staffid.equals(userID)) {
            chatimg.setVisibility(View.GONE);
            friend_btn.setVisibility(View.GONE);
            showroom.setVisibility(View.VISIBLE);
        }

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                        addcheck = jsonResponse.getInt("add");
                    if (addcheck==1) {
                        friend_btn.setVisibility(View.GONE);
                        friend_btn2.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        AddfriendRequest addfriendRequest = new AddfriendRequest(userID, staffid, staffname, 0, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(Staff_profile.this);
        queue1.add(addfriendRequest);

        /*리사이클러 뷰 시술 기록 아이템 터치*/
        final GestureDetector gestureDetector = new GestureDetector(Staff_profile.this, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e){
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
                Intent intent = new Intent(Staff_profile.this, ShowroomActivity.class);
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

//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                View child = rv.findChildViewUnder(e.getX(), e.getY());
//
//                if(child!=null&&gestureDetector.onTouchEvent(e)) {
////                    mListener.onItemClick(child, rv.getChildAdapterPosition(child));
//                    return true;
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
        /*아이템 터치 fin.*/
        showroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, Addshowroom.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                startActivityForResult(intent, 1);
            }
        });

        chatimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, ChatActivity.class);
                intent.putExtra("staffid", staffid);
                intent.putExtra("staffname", staffname);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

        mapimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, MapsActivity.class);
                intent.putExtra("get","get");
                intent.putExtra("staffaddress", address);
                intent.putExtra("staffplace", place);
                intent.putExtra("staffname", staffname);
                startActivity(intent);
            }
        });

        friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            addcheck = jsonResponse.getInt("add");
                                friend_btn.setVisibility(View.GONE);
                                friend_btn2.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                AddfriendRequest addfriendRequest = new AddfriendRequest(userID, staffid, staffname, 1, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
                queue.add(addfriendRequest);
                Toast.makeText(Staff_profile.this, "친구를 추가하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        friend_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            addcheck = jsonResponse.getInt("add");
                                friend_btn.setVisibility(View.VISIBLE);
                                friend_btn2.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                AddfriendRequest addfriendRequest = new AddfriendRequest(userID, staffid, staffname, 1, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
                queue.add(addfriendRequest);

                Toast.makeText(Staff_profile.this, "친구를 삭제하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void receiveArray(String dataObject){
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(0);
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                // 필자는 RecyclerView 로 데이터를 표시 함
                String img = dataJsonObject.getString("profile_img_str");
                if (!img.equals("default") && img != null) {
                    Picasso.with(this).load(img).into(staffimg);
                }
                servicelist= dataJsonObject.getString("list");
                place = dataJsonObject.getString("place");
                address = dataJsonObject.getString("address");
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void receiveArray_picture(String dataObject){
        showroomItems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            for (int i = 0; i < jsonArray.length(); i++) {
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
//                Bitmap bitmap = StringToBitMap(dataJsonObject.getString("img"));
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                // 필자는 RecyclerView 로 데이터를 표시 함             decodeImage(dataJsonObject.getString("img")
                    showroomItems.add(new ShowroomItem(dataJsonObject.getString("text"), dataJsonObject.getString("img"), dataJsonObject.getString("date"), dataJsonObject.getInt("heart"), dataJsonObject.getInt("num")));
            }
            int length = jsonArray.length();
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            picRecyclerAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}//Staff_profile fin.


class PicRecyclerAdapter extends RecyclerView.Adapter<PicRecyclerAdapter.ViewHolder>{
    private ArrayList<ShowroomItem> showroomItems;
    Context context;
    @Override
    public PicRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int width = parent.getResources().getDisplayMetrics().widthPixels/3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pictueritem,parent,false);
        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = (ImageView)view.findViewById(R.id.recycleimg);
        }
    }

    public PicRecyclerAdapter(ArrayList<ShowroomItem> mdataset){
        showroomItems = mdataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.img.setImageBitmap(StringToBitMap(showroomItems.get(position).getimg()));
    }

    public Bitmap StringToBitMap(String encodedString){ // 스트링으로 받은 이미지를 비트맵으로 다시 변환
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
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

//public static class RowCell extends RecyclerView.ViewHolder{
//    public ImageView img;
//
//    public RowCell(View itemView) {
//        super(itemView);
//        img = (ImageView) itemView.findViewById(R.id.recycleimg);
//    }
//}
