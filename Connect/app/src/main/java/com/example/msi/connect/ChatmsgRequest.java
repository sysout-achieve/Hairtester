package com.example.msi.connect;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChatmsgRequest extends StringRequest {
    final static private String URL = "http://13.125.234.222/new/chatmsg_show.php";
    private Map<String, String> parameters;

    public ChatmsgRequest(String sendid, String receiveid, int start_legth, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("sendid", sendid);
        parameters.put("receiveid", receiveid);
        parameters.put("start_length", String.valueOf(start_legth));
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

}

