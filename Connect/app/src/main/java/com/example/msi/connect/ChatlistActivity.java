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
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatlistActivity extends AppCompatActivity {

    private ArrayList<Friendsitem> friendslist;
    RecyclerView friendsview;
    friendsAdapter friendsAdapter;

    String userID, userName;

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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        friendsview.setLayoutManager(layoutManager);
        friendslist = new ArrayList<>();
        friendsAdapter = new friendsAdapter(friendslist);
        friendsview.setAdapter(friendsAdapter);
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
