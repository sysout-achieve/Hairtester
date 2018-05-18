package com.example.msi.connect;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class chatroomlistRequest extends StringRequest {
    final static private String URL = "http://13.125.234.222/new/chatroomlist.php";
    private Map<String, String> parameters;

    public chatroomlistRequest(String userID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

}

