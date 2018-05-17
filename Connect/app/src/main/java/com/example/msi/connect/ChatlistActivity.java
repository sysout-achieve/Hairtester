package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatlistActivity extends AppCompatActivity {

    private ArrayList<Friendsitem> friendslist;
    private ArrayList<Chatroomitem> chatroomlist;
    RecyclerView friendsview, chatlistview;
    friendsAdapter friendsAdapter;
    chatlistAdapter chatlistAdapter;

    int length_chatroom;
    int length;

    String userID, userName;


    private void receiveArray(String dataObject){

        friendslist.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);

            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            for(int i = 0; i < jsonArray.length(); i++){
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                // 필자는 RecyclerView 로 데이터를 표시 함
                friendslist.add(new Friendsitem(dataJsonObject.getString("friendid"), dataJsonObject.getString("friendname")));
            }
            length = jsonArray.length();
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            friendsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*  서버에서 스텝 데이터 받아서 리사이클러 뷰에 구현    */
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
        FriendlistRequest friendlistRequest = new FriendlistRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ChatlistActivity.this);
        queue.add(friendlistRequest);

        friendsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");

        TabHost tabs = (TabHost) findViewById(R.id.tabs);
        tabs.setup();
        TabHost.TabSpec spec1 = tabs.newTabSpec("친구");
        spec1.setIndicator("친구");
        spec1.setContent(R.id.친구);
        tabs.addTab(spec1);

        TabHost.TabSpec spec2 = tabs.newTabSpec("대화");
        spec2.setIndicator("대화");
        spec2.setContent(R.id.대화);
        tabs.addTab(spec2);

        friendsview = (RecyclerView) findViewById(R.id.friends_list);
        chatlistview = (RecyclerView) findViewById(R.id.chatroom_list);
        //friendslist recyclerview 생성
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        friendsview.setLayoutManager(layoutManager);
        friendslist = new ArrayList<>();
        friendsAdapter = new friendsAdapter(friendslist);
        friendsview.setAdapter(friendsAdapter);
        //chatroomlist recyclerview 생성
        RecyclerView.LayoutManager layoutManager_chatroom = new LinearLayoutManager(this);
        chatlistview.setLayoutManager(layoutManager_chatroom);
        chatroomlist = new ArrayList<>();
        chatlistAdapter = new chatlistAdapter(chatroomlist);
        chatlistview.setAdapter(chatlistAdapter);

        final GestureDetector gestureDetector = new GestureDetector(ChatlistActivity.this, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }
        });

        friendsview.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                TextView tv = (TextView) rv.getChildViewHolder(child).itemView.findViewById(R.id.staff_name_add);
                TextView staff_id = (TextView) rv.getChildViewHolder(child).itemView.findViewById(R.id.staff_id_add);

                if(child!=null&&gestureDetector.onTouchEvent(e)) {
                    String staffname = tv.getText().toString();
                    String staffid = staff_id.getText().toString();
                    Intent intent = new Intent(ChatlistActivity.this, Staff_profile.class);
                    intent.putExtra("staffname", staffname);
                    intent.putExtra("staffid", staffid);
                    intent.putExtra("userID",userID);
                    intent.putExtra("userName", userName);
                    startActivity(intent);
                }
                return false;
            }




            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}



class friendsAdapter extends RecyclerView.Adapter<friendsAdapter.ViewHolder>{
    Context context;
    private ArrayList<Friendsitem> friendslist;

    //    public Adapter(Context context, ArrayList<RecycleItem> stafflist){
//        this.context = context;
//        this.stafflist = stafflist;
//    }
    @Override
    public friendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //xml 디자인한 부분 적용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlistitem,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView staffname, staff_id;

        public ViewHolder(View view) {
            super(view);
            staffname = (TextView)view.findViewById(R.id.staff_name_add);
            staff_id = (TextView)view.findViewById(R.id.staff_id_add);
        }
    }

    public friendsAdapter(ArrayList<Friendsitem> mdataset){
        friendslist = mdataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.staffname.setText(friendslist.get(position).getchatname());
        holder.staff_id.setText(friendslist.get(position).getchatid());
//        if(stafflist.get(position).getProfile_image() != null && !stafflist.get(position).getProfile_image().equals("default")){
//            Picasso.with(context).load(stafflist.get(position).getProfile_image()).into(holder.imageView);
//        }

//        holder.imageView.setImageURI(stafflist.get(position).getProfile_image());
    }

    @Override
    public int getItemCount() {
        //아이템을 측정하는 카운터
        return friendslist.size();
    }
}


class chatlistAdapter extends RecyclerView.Adapter<chatlistAdapter.ViewHolder>{
    Context context;
    private ArrayList<Chatroomitem> chatroomlist;

    //    public Adapter(Context context, ArrayList<RecycleItem> stafflist){
//        this.context = context;
//        this.stafflist = stafflist;
//    }
    @Override
    public chatlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //xml 디자인한 부분 적용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlistitem,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView chatroomid, chatroom_in, recentmsg, readchk;

        public ViewHolder(View view) {
            super(view);
            chatroomid = (TextView)view.findViewById(R.id.chatroom_name);
            chatroom_in = (TextView)view.findViewById(R.id.joinid);
            recentmsg = (TextView)view.findViewById(R.id.recentmsg);
            readchk = (TextView)view.findViewById(R.id.readchk);
        }
    }

    public chatlistAdapter(ArrayList<Chatroomitem> mdataset){
        chatroomlist = mdataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.chatroomid.setText(chatroomlist.get(position).getchatroomid());
        holder.chatroom_in.setText(chatroomlist.get(position).getchatroom_in());
        holder.recentmsg.setText(chatroomlist.get(position).getrecentmsg());
        holder.readchk.setText(chatroomlist.get(position).getreadchk());

    }

    @Override
    public int getItemCount() {
        //아이템을 측정하는 카운터
        return chatroomlist.size();
    }
}
