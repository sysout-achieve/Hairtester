package com.example.msi.connect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ProgressDialog mDialog;
    String email, birth, name;

    Button login;
    TextView join;
    EditText userid;
    EditText password;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

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
        if(AccessToken.getCurrentAccessToken() != null){
            status_txt.setText("페이스북 아이디가 저장되어있습니다.");
        }

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
                            Intent intent = new Intent(MainActivity.this, SubmainActivity.class);
                            intent.putExtra("userID", email);
                            intent.putExtra("userAge", birth);
                            intent.putExtra("userName", name);
                            startActivity(intent);
                        }

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","id, email, birthday, friends");
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
                                    Intent intent = new Intent(MainActivity.this, SubmainActivity.class);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("userName", userName);
                                    intent.putExtra("userAge", userAge);
                                    startActivity(intent);
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
    public int getdata(JSONObject object) {
        try {
            email = object.getString("email");
            birth = object.getString("birthday");
//            name = object.getString("name");
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
//            return ;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void request(){
//        UserManagement.getInstance().requestMe(new MeResponseCallback() {
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//                Log.d("error", "Session Closed Error is " + errorResult.toString());
//            }
//
//            @Override
//            public void onNotSignedUp() {
//
//            }
//
//            @Override
//            public void onSuccess(UserProfile result) {
//                Toast.makeText(MainActivity.this, "사용자 이름은 " + result.getNickname(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
