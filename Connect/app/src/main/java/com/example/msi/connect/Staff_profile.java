package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    ImageView staffimg, chatimg, mapimg, friend_btn, friend_btn2, menu_visible, style_visible, review_visible;
    TextView staffname_txt, staffid_txt, allview_txt, allreview_txt;
    TextView styletxt, menutxt;
    String userID, userName, staffid, staffname, place, salelist;
    //String으로 데이터 할당해둠
    int addcheck;
    String servicelist, address;
    FloatingActionButton floatbtn, showroom, saleroom;
    private ArrayList<ShowroomItem> showroomItems;
    RecyclerView recyclerView;
    PicRecyclerAdapter picRecyclerAdapter;
    LinearLayout contain_menu, menu_lay, cut_lay, color_lay, perm_lay, clinic_lay, style_lay, recy_lay;
    LinearLayout review_lay, content_gonelay, content_lay1, content_lay2, content_lay3;
    TextView review_title1, review1, review_rat1, reviewid1, review_date1;


    private void viewctrl(LinearLayout view, ImageView imageView) {
        int getVisible = view.getVisibility();
        if (getVisible == View.GONE) {
            view.setVisibility(View.VISIBLE);
            imageView.setRotation(0);
        } else if (getVisible == View.VISIBLE) {
            view.setVisibility(View.GONE);
            imageView.setRotation(-90);
        }
    }

    private void fill_review(String title, String content, String id, String rate, String date, TextView titleview, TextView contentview, TextView idview, TextView rateview, TextView dateview, LinearLayout layout){
        titleview.setText(title);
        contentview.setText(content);
        idview.setText(id);
        rateview.setText(rate);
        dateview.setText(date);
        layout.setVisibility(View.VISIBLE);
    }

    private void receiveArray_review(String dataObject) {
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환

            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            JSONObject dataJsonObject = jsonArray.getJSONObject(0);
            allreview_txt.setVisibility(View.VISIBLE);
            review_lay.setVisibility(View.VISIBLE);
            content_gonelay.setVisibility(View.VISIBLE);
            String title = dataJsonObject.getString("review_title");
            if (!title.isEmpty() && title!="null" && !title.equals("null")) {
                // Array 에서 하나의 JSONObject 를 추출
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                fill_review(dataJsonObject.getString("review_title"), dataJsonObject.getString("review_content"), dataJsonObject.getString("buyer"),
                        dataJsonObject.getDouble("rating")+"", dataJsonObject.getString("date"),review_title1, review1, reviewid1, review_rat1,
                        review_date1, content_lay1);
            } else {
                allreview_txt.setText("첫 리뷰작성하러 가기");
                // 리뷰가 1개 이하이면 전체보기 텍스트 안보이게 함
                content_lay1.setVisibility(View.GONE);
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
                    JSONArray jsonArray = new JSONArray(jsonResponse.getString("response"));
                    JSONObject dataJsonObject = jsonArray.getJSONObject(0);
                    String staff = dataJsonObject.getString("staff");
                    if(staff.equals("true")){
                            receiveArray_review(jsonResponse.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        StaffCallRequest staffCallRequest = new StaffCallRequest(userID, staffid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Staff_profile.this);
        queue.add(staffCallRequest);
    }

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

        reviewRequest(userID, staffid);
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
        setContentView(R.layout.activity_staff_profile);

        final Intent intent = getIntent();
        staffid = intent.getStringExtra("staffid");
        staffname = intent.getStringExtra("staffname");
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        floatbtn = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        showroom = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        saleroom = (FloatingActionButton) findViewById(R.id.floatingActionButton4);
        menutxt = (TextView) findViewById(R.id.menutxt);
        styletxt = (TextView) findViewById(R.id.styletxt);
        allview_txt = (TextView) findViewById(R.id.allview_txt);
        allreview_txt = (TextView) findViewById(R.id.allreview_txt);

        staffimg = (ImageView) findViewById(R.id.staff_img);
        chatimg = (ImageView) findViewById(R.id.chat_img);
        mapimg = (ImageView) findViewById(R.id.map_img);
        friend_btn = (ImageView) findViewById(R.id.friend_btn);
        friend_btn2 = (ImageView) findViewById(R.id.friend_btn2);
        menu_visible = (ImageView) findViewById(R.id.menu_visible);
        style_visible = (ImageView) findViewById(R.id.style_visible);
        review_visible = (ImageView) findViewById(R.id.review_visible);

        staffid_txt = (TextView) findViewById(R.id.staffid_txt);
        staffname_txt = (TextView) findViewById(R.id.staffname_txt);
        //layout위치 클릭
        menu_lay = (LinearLayout) findViewById(R.id.menu_lay);
        style_lay = (LinearLayout) findViewById(R.id.style_lay);
        contain_menu = (LinearLayout) findViewById(R.id.contain_menu);
        recy_lay = (LinearLayout) findViewById(R.id.recy_lay);
        cut_lay = (LinearLayout) findViewById(R.id.cut_lay);
        color_lay = (LinearLayout) findViewById(R.id.color_lay);
        perm_lay = (LinearLayout) findViewById(R.id.perm_lay);
        clinic_lay = (LinearLayout) findViewById(R.id.clinic_lay);
        review_lay = (LinearLayout) findViewById(R.id.review_lay);
        content_gonelay = (LinearLayout) findViewById(R.id.content_gonelayout);
        content_lay1 = (LinearLayout) findViewById(R.id.content_lay1);
        review_title1 = (TextView) findViewById(R.id.review_title1);
        review1 = (TextView) findViewById(R.id.review1);
        review_rat1 = (TextView) findViewById(R.id.review_rat1);
        reviewid1 = (TextView) findViewById(R.id.reviewid1);
        review_date1 = (TextView) findViewById(R.id.review_date1);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        showroomItems = new ArrayList<>();
        picRecyclerAdapter = new PicRecyclerAdapter(showroomItems);
        recyclerView.setAdapter(picRecyclerAdapter);

        staffname_txt.setText(staffname);
        staffid_txt.setText("(" + staffid + ")");
        if (staffid.equals(userID)) {
            chatimg.setVisibility(View.GONE);
            friend_btn.setVisibility(View.GONE);
            floatbtn.setVisibility(View.VISIBLE);
        }

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    addcheck = jsonResponse.getInt("add");
                    if (addcheck == 1) {
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
        final GestureDetector gestureDetector = new GestureDetector(Staff_profile.this, new GestureDetector.SimpleOnGestureListener() {
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
        /*아이템 터치 fin.*/
        /*layout click*/
        menu_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewctrl(contain_menu, menu_visible);
            }
        });

        review_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewctrl(content_gonelay, review_visible);
            }
        });

        style_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewctrl(recy_lay, style_visible);
            }
        });
        /*layout click fin.*/

        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showroom.getVisibility() == View.GONE) {
                    saleroom.setVisibility(View.VISIBLE);
                    showroom.setVisibility(View.VISIBLE);
                    menutxt.setVisibility(View.VISIBLE);
                    styletxt.setVisibility(View.VISIBLE);
                    floatbtn.setRotation(45);
                } else if (showroom.getVisibility() == View.VISIBLE) {
                    saleroom.setVisibility(View.GONE);
                    showroom.setVisibility(View.GONE);
                    menutxt.setVisibility(View.GONE);
                    styletxt.setVisibility(View.GONE);
                    floatbtn.setRotation(0);
                }
            }
        });

        cut_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, CheckReservActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                intent.putExtra("method", "커트");
                intent.putExtra("price", "1000원");
                intent.putExtra("time", "30분");
                intent.putExtra("staffaddress", address);
                intent.putExtra("staffplace", place);
                startActivity(intent);
            }
        });

        allview_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, TimelineActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                startActivity(intent);
            }
        });

        color_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, CheckReservActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                intent.putExtra("method", "염색");
                intent.putExtra("price", "8000원");
                intent.putExtra("time", "60분");
                intent.putExtra("staffaddress", address);
                intent.putExtra("staffplace", place);
                startActivity(intent);
            }
        });

        perm_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, CheckReservActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                intent.putExtra("method", "펌");
                intent.putExtra("price", "15000원");
                intent.putExtra("time", "90분");
                intent.putExtra("staffaddress", address);
                intent.putExtra("staffplace", place);
                startActivity(intent);
            }
        });

        clinic_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, CheckReservActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                intent.putExtra("method", "트리트먼트");
                intent.putExtra("price", "15000원");
                intent.putExtra("time", "30분");
                intent.putExtra("staffaddress", address);
                intent.putExtra("staffplace", place);
                startActivity(intent);
            }
        });

        content_gonelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, ReviewActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                startActivity(intent);
            }
        });
        allreview_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, ReviewActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("staffid", staffid);
                startActivity(intent);
            }
        });


        showroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, Addshowroom.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                startActivityForResult(intent, 1);
            }
        });
        saleroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Staff_profile.this, SetReservationActivity.class);
                intent.putExtra("userID", userID);
                startActivityForResult(intent, 5);
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
                intent.putExtra("get", "get");
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

    private void receiveArray(String dataObject) {
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            // Array 에서 하나의 JSONObject 를 추출
            JSONObject dataJsonObject = jsonArray.getJSONObject(0);
            // 추출한 Object 에서 필요한 데이터를 화면에 표시, 변수들에 저장한 상태로 화면 전환 시 저장한 값을 전달, 액티비티 전환 시 통신할 필요 없게 미리 필요한 데이터 저장해 둠
            String img = dataJsonObject.getString("profile_img_str");
            if (!img.equals("default") && img != null) {
                Picasso.with(this).load(img).into(staffimg);
            }
            servicelist = dataJsonObject.getString("list");
            place = dataJsonObject.getString("place");
            address = dataJsonObject.getString("address");
            salelist = dataJsonObject.getString("salelist");
            checksale(salelist, 0, cut_lay);
            checksale(salelist, 1, color_lay);
            checksale(salelist, 2, perm_lay);
            checksale(salelist, 3, clinic_lay);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void checksale(String sale, int charat, LinearLayout layout) {
        if (sale.charAt(charat) == 49) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    private void receiveArray_picture(String dataObject) {
        showroomItems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            int length = jsonArray.length();
            if (length > 3) {
                length = 3;
                allview_txt.setVisibility(View.VISIBLE);
            }
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
}//Staff_profile fin.
