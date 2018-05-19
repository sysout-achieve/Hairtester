package com.example.msi.connect;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class chatmsgsaveRequest extends StringRequest {
    final static private String URL = "http://13.125.234.222/new/chat_msg_save.php";
    private Map<String, String> parameters;

    public chatmsgsaveRequest(String sendid, String sendName, String receiveid, String receivename, String message, int readchk, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("sendid", sendid);
        parameters.put("sendName", sendName);
        parameters.put("receiveid", receiveid);
        parameters.put("receivename", receivename);
        parameters.put("message", message);
        parameters.put("readchk", String.valueOf(readchk));

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

}

