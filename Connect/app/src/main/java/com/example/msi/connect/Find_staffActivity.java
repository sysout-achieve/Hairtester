package com.example.msi.connect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Find_staffActivity extends AppCompatActivity {

    private ArrayList<RecycleItem> stafflist;
    RecyclerView view;
    String userID, userName;
    findAdapter adapter;
    int length;

    private void receiveArray(String dataObject){

        stafflist.clear();
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
                stafflist.add(new RecycleItem(dataJsonObject.getString("profile_img_str"),dataJsonObject.getString("nickname"),
                        dataJsonObject.getString("list"),dataJsonObject.getString("place"), dataJsonObject.getString("id")));
            }
            length = jsonArray.length();
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_staff);

        Intent intent = getIntent();
        userID = intent.getStringExtra("useID");
        userName = intent.getStringExtra("userName");

        view = (RecyclerView) findViewById(R.id.staff_list);
        //Linearlayout manager 사용
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        view.setLayoutManager(layoutManager);

        //
        stafflist = new ArrayList<>();
        adapter = new findAdapter(stafflist);
        view.setAdapter(adapter);
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
        AllStaffRequest allStaffRequest = new AllStaffRequest( responseListener);
        RequestQueue queue = Volley.newRequestQueue(Find_staffActivity.this);
        queue.add(allStaffRequest);
        /*      서버 데이터 fin.     */
        final GestureDetector gestureDetector = new GestureDetector(Find_staffActivity.this, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }
        });

        view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                TextView tv = (TextView) rv.getChildViewHolder(child).itemView.findViewById(R.id.staff_name);
                TextView staff_id = (TextView) rv.getChildViewHolder(child).itemView.findViewById(R.id.staff_id);

                if(child!=null&&gestureDetector.onTouchEvent(e)) {
                    String staffname = tv.getText().toString();
                    String staffid = staff_id.getText().toString();
                    Intent intent = new Intent(Find_staffActivity.this, Staff_profile.class);
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
    }       // onCreate fin.

}       // Find_staffActivity fin.


class findAdapter extends RecyclerView.Adapter<findAdapter.ViewHolder>{
    Context context;
    private ArrayList<RecycleItem> stafflist;

//    public Adapter(Context context, ArrayList<RecycleItem> stafflist){
//        this.context = context;
//        this.stafflist = stafflist;
//    }
    @Override
    public findAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //xml 디자인한 부분 적용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staffitem,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView staffname, staffservice, staffaddress, staff_id;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.recycleritem);
            staffaddress = (TextView)view.findViewById(R.id.staff_address);
            staffname = (TextView)view.findViewById(R.id.staff_name);
            staffservice = (TextView)view.findViewById(R.id.staff_service);
            staff_id = (TextView)view.findViewById(R.id.staff_id);

        }
    }

    public findAdapter(ArrayList<RecycleItem> mdataset){
        stafflist = mdataset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.staffname.setText(stafflist.get(position).getName());
        holder.staffservice.setText(stafflist.get(position).getService());
        holder.staffaddress.setText(stafflist.get(position).getAddress());
        holder.staff_id.setText(stafflist.get(position).getStaff_id());
//        if(stafflist.get(position).getProfile_image() != null && !stafflist.get(position).getProfile_image().equals("default")){
//            Picasso.with(context).load(stafflist.get(position).getProfile_image()).into(holder.imageView);
//        }

//        holder.imageView.setImageURI(stafflist.get(position).getProfile_image());
    }

    @Override
    public int getItemCount() {
        //아이템을 측정하는 카운터
        return stafflist.size();
    }
}