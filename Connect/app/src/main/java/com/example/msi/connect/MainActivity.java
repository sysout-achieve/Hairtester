package com.example.msi.connect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import android.support.v7.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.ConnectivityManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import android.view.MotionEvent;
import de.hdodenhof.circleimageview.CircleImageView;



public class MainActivity extends AppCompatActivity {

/*카카오 로그인 api 변수*/
    private SessionCallback callback;
    int chk_success;

/*------------------*/

    CallbackManager callbackManager;
    ProgressDialog mDialog;
    String email, birth, name1, name2, profile_img_string;

    Button login;
    TextView join;
    EditText userid;
    EditText password;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

// 어플 고유의 키해시를 얻는 method
//  private void printkeyHash() {
//        try{
//        PackageInfo info = getPackageManager().getPackageInfo("com.example.msi.connect", PackageManager.GET_SIGNATURES);
//        for(Signature signature:info.signatures)
//        {
//            MessageDigest md = MessageDigest.getInstance("SHA");
//            md.update(signature.toByteArray());
//            Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
//        }
//
//    } catch (PackageManager.NameNotFoundException e) {
//        e.printStackTrace();
//    } catch (NoSuchAlgorithmException e) {
//        e.printStackTrace();
//    }
//  }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        login = (Button) findViewById(R.id.login_btn);
        join = (TextView) findViewById(R.id.join_btn);
        userid = (EditText) findViewById(R.id.user_id);
        password = (EditText) findViewById(R.id.password);
        TextView status_txt = (TextView) findViewById(R.id.status_txt);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday"));
        //already login
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    getdata(object);
                    if (getdata(object) == 1) {
                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                        intent.putExtra("userID", email);
                        intent.putExtra("userAge", birth);
                        intent.putExtra("userName", name1+name2);
                        intent.putExtra("profile_img_string",profile_img_string);
                        startActivity(intent);
                        finish();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields","id, email, birthday, friends, first_name, last_name");
            request.setParameters(parameters);
            request.executeAsync();
        }
/* 카카오 로그인 구현 */

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);


        if(Session.getCurrentSession().isOpened()){
            requestMe();
        }


/*카카오 로그인 fin.*/
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("data...");
                mDialog.show();

                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        Log.d("response",response.toString());
                        getdata(object);
                        if (getdata(object) == 1) {
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            intent.putExtra("userID", email);
                            intent.putExtra("userAge", birth);
                            intent.putExtra("userName", name1 + name2);
                            intent.putExtra("profile_img_string",profile_img_string);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id, email, birthday, first_name, last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = userid.getText().toString();
                final String userPassword = password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");


                                if (success) {
                                    String userID = jsonResponse.getString("userID");
                                    String userName = jsonResponse.getString("userName");
                                    String userAge = jsonResponse.getString("userAge");
                                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("userName", userName);
                                    intent.putExtra("userAge", userAge);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("로그인에 실패했습니다.")
                                            .setNegativeButton("다시 시도", null)
                                            .create()
                                            .show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });
    } //onCreate fin.

    //인터넷 연결 확인
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    // facebook 로그인 했을 경우 로그인 아이디의 데이터 get.
    public int getdata(JSONObject object) {
        try {
            email = object.getString("email");
            birth = object.getString("birthday");
            name1 = object.getString("last_name");
            name2 = object.getString("first_name");
            Profile profile = Profile.getCurrentProfile();
            profile_img_string = profile.getProfilePictureUri(100, 100).toString();
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태. 일반적으로 로그인 후의 다음 activity로 이동한다.
            if(Session.getCurrentSession().isOpened()){ // 한 번더 세션을 체크해주었습니다.
                requestMe();
            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }



    private void requestMe() {

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("onFailure", errorResult + "");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("onSessionClosed",errorResult + "");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.e("onSuccess",userProfile.toString());
                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("data...");
                mDialog.show();
                name1 = userProfile.getNickname();
                email = userProfile.getEmail();
                //카카오톡 이메일 요청 못함
                profile_img_string = userProfile.getThumbnailImagePath(); // <- 프로필 작은 이미지 , userProfile.getProfileImagePath() <- 큰 이미지
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("userID", email);
                intent.putExtra("userName", name1);
                intent.putExtra("userAge", birth);
                intent.putExtra("profile_img_string",profile_img_string);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("onNotSignedUp","onNotSignedUp");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
}


