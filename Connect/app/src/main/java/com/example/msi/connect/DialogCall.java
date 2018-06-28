package com.example.msi.connect;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DialogCall {

    Double finalrate;
    int length;

   public void dialog_call(final Context context, final int review_number, final String userID, String writeID, final String staffid, final ArrayList<ReviewItem> reviewItems, final ReviewAdapter reviewAdapter, final TextView rev_member, final TextView rate_score, final RatingBar ratebar_rev,  final int check_rate){
       if(userID.equals(writeID)){
           AlertDialog.Builder builder = new AlertDialog.Builder(context);
           builder.setTitle("리뷰를 삭제하시겠습니까?");
           builder.setMessage("리뷰를 삭제하시면 다시 작성할 수 없습니다. 그래도 삭제하시겠습니까?");
           builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

               }
           });
           builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   Response.Listener<String> responseListener = new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           reviewRequest(context, userID, staffid, reviewItems, reviewAdapter, rev_member, rate_score, ratebar_rev, check_rate);
                       }
                   };
                   ReviewDeleteRequest reviewDeleteRequest = new ReviewDeleteRequest(review_number, responseListener);
                   RequestQueue queue = Volley.newRequestQueue(context);
                   queue.add(reviewDeleteRequest);
               }
           });
           builder.show();
       }
    } //dialog_call() fin.

    private void receiveArray_review(String dataObject, ArrayList<ReviewItem> reviewItems, ReviewAdapter reviewAdapter) {
        reviewItems.clear();
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);

            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));

            for (int i = 0; i < jsonArray.length(); i++) {
                // Array 에서 하나의 JSONObject 를 추출
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                // 필자는 RecyclerView 로 데이터를 표시 함
                reviewItems.add(new ReviewItem(dataJsonObject.getString("review_title"), dataJsonObject.getString("review_content"),
                        dataJsonObject.getDouble("rating"), dataJsonObject.getString("buyer"), dataJsonObject.getString("date"), dataJsonObject.getInt("num")));
            }
            // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
            reviewAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void reviewRequest(Context context, String userID, String staffid, final ArrayList<ReviewItem> reviewItems, final ReviewAdapter reviewAdapter, final TextView rev_member, final TextView rate_score, final RatingBar ratebar_rev, final int check_rate) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //check_rate = 0 일 경우 단순 리뷰 목록을 불러옴
                    //check_rate = 1 일 경우 리뷰의 전체 평점과 전체 리뷰 갯수를 함께 구함
                    if(check_rate == 0){
                        receiveArray_review(jsonResponse.toString(), reviewItems, reviewAdapter);
                    } else {
                        receiveArray_review_rate(jsonResponse.toString(), reviewItems, reviewAdapter, rev_member, rate_score, ratebar_rev);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ReviewCallRequest reviewCallRequest = new ReviewCallRequest(userID, staffid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(reviewCallRequest);
    }

    public void receiveArray_review_rate(String dataObject, ArrayList<ReviewItem> reviewItems, ReviewAdapter reviewAdapter,TextView rev_member, TextView rate_score, RatingBar ratebar_rev) {
        reviewItems.clear();
        double rating = 0.0;
        try {
            // String 으로 들어온 값 JSONObject 로 1차 파싱
            JSONObject wrapObject = new JSONObject(dataObject);
            // JSONObject 의 키 "response" 의 값들을 JSONArray 형태로 변환
            JSONArray jsonArray = new JSONArray(wrapObject.getString("response"));
            length = jsonArray.length();
                for (int i = 0; i < jsonArray.length(); i++) {
                    // Array 에서 하나의 JSONObject 를 추출
                    JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                    // 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
                    // 필자는 RecyclerView 로 데이터를 표시 함
                    rating = dataJsonObject.getDouble("rating") + rating;
                    reviewItems.add(new ReviewItem(dataJsonObject.getString("review_title"), dataJsonObject.getString("review_content"),
                            dataJsonObject.getDouble("rating"), dataJsonObject.getString("buyer"), dataJsonObject.getString("date"), dataJsonObject.getInt("num")));
                }
                // Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
                finalrate = Double.valueOf(Math.round(rating * 100 / jsonArray.length())) / 100;
                rev_member.setText(jsonArray.length() + " 개의 리뷰");
                rate_score.setText(finalrate + "");
                ratebar_rev.setRating(finalrate.floatValue());
                reviewAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
