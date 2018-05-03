package com.example.msi.connect;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MSI on 2018-04-19.
 */

public class ProfileSaveRequest extends StringRequest {

    final static private String URL = "http://13.125.234.222/new/profilesave.php";
    private Map<String, String> parameters;

    public ProfileSaveRequest(String userID, String profile_img_string, String nick_str, String age_str,String kind_sex ,String kind_user ,String experience_str ,String list ,String place ,String address_str, int checked_designer, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("profile_img_string", profile_img_string);
        parameters.put("nick_str", nick_str);
        parameters.put("age_str", age_str);
        parameters.put("kind_sex", kind_sex);
        parameters.put("kind_user", kind_user);
        parameters.put("experience_str", experience_str);
        parameters.put("list", list);
        parameters.put("place", place);
        parameters.put("address_str", address_str);
        parameters.put("checked_designer", checked_designer+"");
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

}


