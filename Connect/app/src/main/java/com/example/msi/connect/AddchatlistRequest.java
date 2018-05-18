package com.example.msi.connect;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddchatlistRequest extends StringRequest {
    final static private String URL = "http://13.125.234.222/new/addchatroom.php";
    private Map<String, String> parameters;

    public AddchatlistRequest(String room, String roomid, String recentmsg, int readchk, String host, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("room", room);
        parameters.put("roomid", roomid);
        parameters.put("recentmsg", recentmsg);
        parameters.put("readchk", String.valueOf(readchk));
        parameters.put("host", host);
        }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

}

