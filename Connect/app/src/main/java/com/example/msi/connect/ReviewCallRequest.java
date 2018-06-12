package com.example.msi.connect;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ReviewCallRequest extends StringRequest {
    final static private String URL = "http://13.125.234.222/new/reviewcall.php";
    private Map<String, String> parameters;

    public ReviewCallRequest(String userID, String staffid, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("staffid", staffid);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

}
