package com.example.msi.connect;




import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    String imgDecodableString;

    String userID, staffid, username, staffname;
    String chatname, chatid;
    int length;
    int start_length = 0;
    EditText mInputMessageView;
    RecyclerView mMessagesView;
    TextView txtvw, whochat;
    Button sendbtn;
    List<Message> mMessages = new ArrayList<Message>();
    private ArrayList<ChatItem> chatItems;
    chatAdapter chatAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String ip = "http://13.125.234.222:3000";


    private static Socket mSocket;
    {
        try {
            mSocket = IO.socket(ip);


        } catch (URISyntaxException e) {

        }
    }

    private void addMessage(String message){
        String currentTime = DateFormat.getDateTimeInstance().format(new Date());
        chatItems.add(new ChatItem(null,message, currentTime,0, null));
        chatAdapter.notifyItemInserted(chatItems.size());
        scrollToBottom(1);
    }

    private void receiveMessage(String message, String sendid){
        String currentTime = DateFormat.getDateTimeInstance().format(new Date());
        chatItems.add(new ChatItem(sendid, message, currentTime,1, null));
        chatAdapter.notifyItemInserted(chatItems.size());
        scrollToBottom(1);
    }

    private void scrollToBottom(int count) {
        mMessagesView.scrollToPosition(chatAdapter.getItemCount()-count);
    }

    private void receiveArray(String dataObject){

        chatItems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);

            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            for(int i = 0; i < jsonArray.length(); i++){
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                // 필자는 RecyclerView 로 데이터를 표시 함             decodeImage(dataJsonObject.getString("img")

                if(userID.equals(dataJsonObject.getString("sendid"))){
                    chatItems.add(new ChatItem(null,dataJsonObject.getString("message"), dataJsonObject.getString("time"),0, null));
                } else {
                    chatItems.add(new ChatItem(dataJsonObject.getString("sendname"), dataJsonObject.getString("message"), dataJsonObject.getString("time"),1, null));
                }
            }
            length = jsonArray.length();
            start_length = start_length+5;

            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            chatAdapter.notifyDataSetChanged();
            if (start_length == 5) {
                scrollToBottom(1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listener_chat(){
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
    ChatmsgRequest chatmsgRequest = new ChatmsgRequest(userID, staffid, start_length, responseListener);
    RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
    queue.add(chatmsgRequest);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        username = intent.getStringExtra("userName");
        staffid = intent.getStringExtra("staffid");
        staffname = intent.getStringExtra("staffname");
        insertchat(userID, staffid);

        mMessagesView = (RecyclerView) findViewById(R.id.messages);
        sendbtn = (Button) findViewById(R.id.send_btn);
        mInputMessageView = (EditText) findViewById(R.id.message_text);
        // socket 접속
        mSocket.emit("joinroom",staffid);
        mSocket.on("message", handleInmcomingMessages);

        //Linearlayout manager 사용
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mMessagesView.setLayoutManager(layoutManager);

        chatItems = new ArrayList<>();
        chatAdapter = new chatAdapter(chatItems);
        mMessagesView.setAdapter(chatAdapter);

        listener_chat();



        mMessagesView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!mMessagesView.canScrollVertically(-1)) {
                    if(start_length != -1){
                        listener_chat();
                    }
                }
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }//onCreate fin.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.chat_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);
        ImageButton btnpicture = (ImageButton) findViewById(R.id.btnpicture);
        ImageButton bactbtn = (ImageButton)findViewById(R.id.btnBack_chat);
        whochat = (TextView)findViewById(R.id.whochat);
        whochat.setText(staffname);

        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        return true;
    }

    private void openGallery()
    {
        Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryintent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            try {
                Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                sendImage(image_bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            // Move to first row
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            imgDecodableString = cursor.getString(columnIndex);
//            cursor.close();
            //Log.d("onActivityResult",imgDecodableString);

        }
    }

    public void sendImage(Bitmap bitmap)
    {
        JSONObject sendData = new JSONObject();
        try{
            sendData.put("userid", userID);
            sendData.put("username", username);
            sendData.put("staffid", staffid);
            sendData.put("message", null);
            sendData.put("image", bitmap);
//            Bitmap bmp = decodeImage(sendData.getString("image"));
            addImage(bitmap);
            mSocket.emit("message",sendData);
        }catch(JSONException e){

        }
    }

    private void addImage(Bitmap bmp){
        /*!!!*/
        String currentTime = DateFormat.getDateTimeInstance().format(new Date());
        chatItems.add(new ChatItem(null,null, currentTime,0, bmp));
        chatAdapter.notifyItemInserted(chatItems.size());
        scrollToBottom(1);
    }
    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }
    private Bitmap decodeImage(String data)
    {
        byte[] b = Base64.decode(data,Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
        return bmp;
    }

    // 유저가 보낸 메세지를 서버 db에 저장
    private void receiveMessage_chatroom( String sendid, String sendName, String message,int readchk, String userID){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };
        AddchatlistRequest addchatlistRequest = new AddchatlistRequest(sendid, sendName, message, readchk, userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
        queue.add(addchatlistRequest);
    }
    private void sendMessage_chat_save( String sendid, String sendName, String receiveid, String receivename, String message, int readchk){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        };
        chatmsgsaveRequest chatmsgsaveRequest = new chatmsgsaveRequest(sendid,sendName,receiveid, receivename, message, readchk, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
        queue.add(chatmsgsaveRequest);
    }

    private void sendMessage() {
        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        addMessage(message);
        receiveMessage_chatroom(staffid, staffname, message,0, userID ); // 유저가 보낸 메세지를 서버 db에 저장 (chatromm table에 host = userid)
        receiveMessage_chatroom(userID, username, message,1, staffid ); // 메세지를 전송받은 유저의 db에 저장 (chatromm table에 host = staffid)
        sendMessage_chat_save(userID, username, staffid, staffname, message, 1);
        try{
            JSONObject data = new JSONObject();
            data.put("userid", userID);
            data.put("username", username);
            data.put("staffid", staffid);
            data.put("message", message);
            mSocket.emit("message", data);
        } catch (Exception e){

        }
    }
/*삭제 가능 코드 발표 후 검토*/
    private void insertchat(final String id_user, final String id_staff){
        try {
            JSONObject parms = new JSONObject();
            parms.put("userid", id_user);
            parms.put("staffid", id_staff);

//            mSocket.emit("insert_chat", parms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
    private Emitter.Listener handleInmcomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String username;
                    String check_room;
                    String sendid;
                    try {
                        message = data.getString("message").toString();
                        username = data.getString("sendname").toString();
                        sendid = data.getString("sendid").toString();
                        check_room = data.getString("checkroom").toString();
                        if (staffid.equals(sendid)) {
                            receiveMessage(message, username);
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    @Override
    protected void onPause() {
        receiveMessage_chatroom(staffid, staffname, "nosave",0, userID );   //php에서 null을 인식하지 못해서 임시방편으로 nosave라는 값을 보냄
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mSocket.emit("disconnect_room", staffid);
        super.onDestroy();
//        mSocket.disconnect();
    }
}   //class ChatActivity fin.

class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder>{

    private ArrayList<ChatItem> chatItems;

    @Override
    public chatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //xml 디자인한 부분 적용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.chatdate.setText(chatItems.get(position).getchattime());
            holder.chatmessage.setText(chatItems.get(position).getchatmessage());

        if(chatItems.get(position).getCheck_send()==1){
            holder.chatlayout.setGravity(Gravity.LEFT);
            holder.chatlayout_inner.setGravity(Gravity.LEFT);
            holder.chatmessage.setGravity(Gravity.LEFT);
            holder.chatmessage.setBackgroundResource(R.drawable.receive_msg);
            holder.chatid.setText(chatItems.get(position).getchatid());
            holder.chatid.setVisibility(View.VISIBLE);
            holder.receive_img.setVisibility(View.VISIBLE);
        } else if (chatItems.get(position).getCheck_send()==0) {
            holder.chatlayout.setGravity(Gravity.RIGHT);
            holder.chatmessage.setGravity(Gravity.RIGHT);
            holder.chatlayout_inner.setGravity(Gravity.RIGHT);
            holder.chatmessage.setBackgroundResource(R.drawable.send_msg);
            holder.receive_img.setVisibility(View.GONE);
            holder.chatid.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chatmessage;
        public TextView chatid;
        public TextView chatdate;
        public ImageView receive_img;
        LinearLayout chatlayout;
        LinearLayout chatlayout_inner;
        public ViewHolder(View view) {
            super(view);
            chatid = (TextView) view.findViewById(R.id.chatid);
            chatlayout = (LinearLayout) view.findViewById(R.id.chatlayout);
            chatlayout_inner = (LinearLayout) view.findViewById(R.id.chatlayout_inner);
            chatmessage = (TextView)view.findViewById(R.id.chatmessage);
            chatdate = (TextView)view.findViewById(R.id.chattime);
            receive_img = (ImageView) view.findViewById(R.id.receive_img);
        }
    }

    public chatAdapter(ArrayList<ChatItem> mdataset){
        chatItems = mdataset;
    }


    @Override
    public int getItemCount() {
        //아이템을 측정하는 카운터
        return chatItems.size();
    }
} // chatAdapter fin.


   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSocket.on("new message", onNewMessage);
        mSocket.connect();
        setContentView(R.layout.activity_chat);

        final ScrollView sv = (ScrollView) findViewById(R.id.scrollView01);
        inputtxt = (EditText) findViewById(R.id.inputtxt);
        txtvw = (TextView) findViewById(R.id.txtvw);
        sendbtn = (Button) findViewById(R.id.send_btn);

        sv.fullScroll(View.FOCUS_DOWN);


        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("chat-message", onMessageReceived);
    }

    private void attemptSend() {
        String message = inputtxt.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        inputtxt.setText("");
        mSocket.emit("new message", message);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // your code...
            JSONObject data = new JSONObject();
            try {
                data.put("key1", "value1");
                data.put("key2", "value2");
                mSocket.emit("chat-message", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            JSONObject receivedData = (JSONObject) args[0];
            // your code...
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(username, message);
                }
            });
        }


    }
}*/


//    private Socket mSocket;
//    {
//        try {
//            mSocket = IO.socket("13.125.234.222");
//        } catch (URISyntaxException e) {}
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mSocket.connect();
//        setContentView(R.layout.activity_chat);
//
//        final ScrollView sv = (ScrollView) findViewById(R.id.scrollView01);
//
//        sv.fullScroll(View.FOCUS_DOWN);
//
//
//        mSocket.on(Socket.EVENT_CONNECT, onConnect);
//        mSocket.on("chat-message", onMessageReceived);
//    }
//    // Socket서버에 connect 된 후, 서버로부터 전달받은 'Socket.EVENT_CONNECT' Event 처리.
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            // your code...
//            JSONObject data = new JSONObject();
//            try {
//                data.put("key1", "value1");
//                data.put("key2", "value2");
//                mSocket.emit("chat-message", data);
//            } catch(JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    // 서버로부터 전달받은 'chat-message' Event 처리.
//    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
//            JSONObject receivedData = (JSONObject) args[0];
//            // your code...
//        }
//    };
