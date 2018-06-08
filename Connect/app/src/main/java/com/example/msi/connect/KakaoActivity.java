package com.example.msi.connect;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.EncodingUtils;
import cz.msebera.android.httpclient.util.EntityUtils;

public class KakaoActivity extends Activity {

    public Context mContext;
    private WebView mainWebView;
    private final String APP_SCHEME = "iamportkakao://";
    //    String userID;
    WebViewInterface mWebViewInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao);
        mContext = this.getApplicationContext();
        Intent intent = getIntent();

        String userID = intent.getStringExtra("userID");
        String salename = intent.getStringExtra("salename");
        int saleprice = intent.getIntExtra("saleprice", 10000);
        String staffid = intent.getStringExtra("staffid");
        String phone = intent.getStringExtra("phone");
        mainWebView = (WebView) findViewById(R.id.mainWebView);
        mainWebView.setWebViewClient(new KakaoWebViewClient(this));
        WebSettings settings = mainWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        mainWebView.loadUrl("http://13.125.234.222/new/payment.php?userid=" + userID + "&salename=" + salename + "&saleprice=" + saleprice + "&staffid=" + staffid + "&phone=" + phone);

        mWebViewInterface = new WebViewInterface(KakaoActivity.this, mainWebView);
        mainWebView.addJavascriptInterface(mWebViewInterface, "android");
    }


    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.toString();

        if (url.startsWith(APP_SCHEME)) {
            // "iamportapp://https://pgcompany.com/foo/bar"와 같은 형태로 들어옴
            String redirectURL = url.substring(APP_SCHEME.length() + "://".length());
            mainWebView.loadUrl(redirectURL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null) {
            Uri intentData = intent.getData();

            if (intentData != null) {
                //카카오페이 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();

                if (url.startsWith(APP_SCHEME)) {
                    String path = url.substring(APP_SCHEME.length());
                    if ("process".equalsIgnoreCase(path)) {
                        mainWebView.loadUrl("javascript:IMP.communicate({result:'process'})");
                    } else {
//                        mainWebView.loadUrl("app://application");
                        mainWebView.loadUrl("javascript:IMP.communicate({result:'cancel'})");
                    }
                }
            }
        }
    }//onResume fin.

    public class KakaoWebViewClient extends WebViewClient {
        private Activity activity;

        public KakaoWebViewClient(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                Intent intent = null;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                    Uri uri = Uri.parse(intent.getDataString());
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                } catch (URISyntaxException ex) {
                    return false;
                } catch (ActivityNotFoundException e) {
                    if (intent == null) return false;

                    String packageName = intent.getPackage(); //packageName should be com.kakao.talk
                    if (packageName != null) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                        return true;
                    }
                    return false;
                }
            }
            if (url.startsWith("app://")) {
                Intent intent1 = new Intent(KakaoActivity.this, Main2Activity.class);
                startActivity(intent1);
                finish();
            } else if (url.startsWith("cancel://")) {
                Intent intent1 = new Intent(KakaoActivity.this, Main2Activity.class);
                startActivity(intent1);
                finish();
            }
            return false;
        }

        // 실패 salename=%EC%B
        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }
}
