package com.example.msi.connect;




import android.content.Context;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {
     EditText mInputMessageView;
    RecyclerView mMessagesView;
    TextView txtvw;
    Button sendbtn;
    List<Message> mMessages = new ArrayList<Message>();
    private ArrayList<ChatItem> chatItems;
    chatAdapter chatAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String ip = "http://13.125.234.222:3000";


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(ip);
        } catch (URISyntaxException e) {
        }
    }
//    private void attemptSend() {
//        String message = mInputMessageView.getText().toString().trim();
//        if (TextUtils.isEmpty(message)) {
//            return;
//        }
//
//        mInputMessageView.setText("");
//        mSocket.emit("메세지를 입력하세요.", message);
//    }
    private void addMessage(String message){
        chatItems.add(new ChatItem(null,message,null));
        chatAdapter.notifyItemInserted(chatItems.size());
        scrollToBottom();

    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(chatAdapter.getItemCount()-1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket.connect();
        mSocket.on("message", handleInmcomingMessages);
        setContentView(R.layout.activity_chat);
        mMessagesView = (RecyclerView) findViewById(R.id.messages);
        sendbtn = (Button) findViewById(R.id.send_btn);
        mInputMessageView = (EditText) findViewById(R.id.message_text);


        //Linearlayout manager 사용
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mMessagesView.setLayoutManager(layoutManager);

        chatItems = new ArrayList<>();
        chatAdapter = new chatAdapter(chatItems);
        mMessagesView.setAdapter(chatAdapter);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        addMessage(message);
        mSocket.emit("message", message);
    }

private Emitter.Listener handleInmcomingMessages = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                String message;
                try{
                    message = data.getString("message").toString();

                }catch (JSONException e){
                    return;
                }
                addMessage(message);
    }
});

    }
};


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.connected();
    }


}
class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder>{

    private ArrayList<ChatItem> chatItems;

    //    public Adapter(Context context, ArrayList<RecycleItem> stafflist){
//        this.context = context;
//        this.stafflist = stafflist;
//    }
    @Override
    public chatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //xml 디자인한 부분 적용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.chatmessage.setText(chatItems.get(position).getchatmessage());

    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView chatmessage;

        public ViewHolder(View view) {
            super(view);

            chatmessage = (TextView)view.findViewById(R.id.chatmessage);

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
}


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
