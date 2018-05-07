package com.example.msi.connect;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MSI on 2018-04-19.
 */

public class StaffProfileRequest extends StringRequest {

    final static private String URL = "http://13.125.234.222/new/staffprofile.php";
    private Map<String, String> parameters;

    public StaffProfileRequest(String staffID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("staffID", staffID);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
