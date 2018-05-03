package com.example.msi.connect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TableLayout staff_lay, customer_lay;
    TableRow customer_man, customer_woman;
    String userID, profile_img_string, userName, userAge;
    String length, kind_sex, kind_user, experience_str, list_str,place, address_str;
    TextView place_select, place_select_customer, addr, addr_customer;
    EditText nametxt, experience;
    TextView showID, showAge;
    TextView sel_method,like_style;
    List<String> list = new ArrayList<String>();
    List<String> list_customer = new ArrayList<String>();
    int checked_designer;
    Switch fin_profile;
    RadioButton man, woman, staff_btn, customer_btn;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);






        ImageView profile_img = (ImageView) findViewById(R.id.profile_img);
        showID = (TextView) findViewById(R.id.showID);
        showAge = (TextView) findViewById(R.id.showAge);
        nametxt = (EditText)findViewById(R.id.nicknametxt);
        sel_method = (TextView) findViewById(R.id.sel_method);
        like_style = (TextView) findViewById(R.id.like_style);
        experience = (EditText) findViewById(R.id.experience);
        place_select = (TextView) findViewById(R.id.place_select);
        place_select_customer = (TextView) findViewById(R.id.place_select_customer);
        addr = (TextView) findViewById(R.id.addr);
        addr_customer = (TextView) findViewById(R.id.addr_customer);
        fin_profile = (Switch) findViewById(R.id.fin_profile);

        RadioGroup sex = (RadioGroup) findViewById(R.id.radioGroup_sex);
        RadioGroup kind = (RadioGroup) findViewById(R.id.radioGroup_kind);
        customer_btn = (RadioButton) findViewById(R.id.customer_btn);
        staff_btn = (RadioButton) findViewById(R.id.staff_btn);
        man = (RadioButton) findViewById(R.id.man);
        woman = (RadioButton) findViewById(R.id.woman);
        //닉네임 활성화 제거
        nametxt.setFocusable(false);
        nametxt.setClickable(false);

        Spinner spinner_man = (Spinner)findViewById(R.id.spinner1);
        Spinner spinner_woman = (Spinner)findViewById(R.id.spinner2);
        /* intent 전달 받음 */
        Intent intent = getIntent();
        boolean userinfo = intent.getBooleanExtra("userinfo", false);


        if(userinfo){
            profile_img_string = intent.getStringExtra("profile_img_string");
            userID = intent.getStringExtra("userID");
            userName = intent.getStringExtra("userName");
            userAge = intent.getStringExtra("userAge")+"";
            kind_sex = intent.getStringExtra("kind_sex");
            kind_user = intent.getStringExtra("kind_user");
            experience_str = intent.getStringExtra("experience");
            list_str = intent.getStringExtra("list");
            place = intent.getStringExtra("place");
            address_str = intent.getStringExtra("address_str");
            checked_designer = intent.getIntExtra("checked_designer", 7);
        } else {
            profile_img_string = intent.getStringExtra("profile_img_string");
            userID = intent.getStringExtra("userID");
            userName = intent.getStringExtra("userName");
            userAge = intent.getStringExtra("userAge")+"";
        }
        /* intent fin.*/

        /* 아이디 확인하여 저장된 value 안드로이드 화면에 배치 */
        showID.setText(userID);
        nametxt.setText(userName);
        showAge.setText(userAge+"");
        if(profile_img_string != null || profile_img_string != "default"){
            Picasso.with(this).load(profile_img_string).into(profile_img);
        }

       fin_profile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   checked_designer = 1;
               } else {
                   checked_designer = 0;
               }
           }
       });

        //닉네임클릭 시 닉네임 변경 가능
        nametxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nametxt.setFocusableInTouchMode(true);
                nametxt.setClickable(true);
                nametxt.setFocusable(true);
            }
        });
        //가능한 시술 정보를 저장
        sel_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"커트", "염색", "펌", "클리닉"};
                boolean a = list.contains("커트");
                boolean b = list.contains("염색");
                boolean c = list.contains("펌");
                boolean d = list.contains("클리닉");
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
                dialog.setTitle("가능한 시술을 골라주세요.").setMultiChoiceItems(
                        items
                        , new boolean[]{a, b, c, d}
                        , new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    list.add(items[which]);
                                } else {
                                    list.remove(items[which]);
                                }
                            }
                        }
                    )
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                               sel_method.setText(list.toString());
                        }
                    })
                    .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.create().show();
            }
        });
            // 받고싶은 시술 정보를 저장
        like_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"커트", "염색", "펌", "클리닉"};
                boolean a = list_customer.contains("커트");
                boolean b = list_customer.contains("염색");
                boolean c = list_customer.contains("펌");
                boolean d = list_customer.contains("클리닉");
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
                dialog.setTitle("관심있는 시술을 골라주세요.").setMultiChoiceItems(
                        items
                        , new boolean[]{a, b, c, d}
                        , new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    list_customer.add(items[which]);
                                } else {
                                    list_customer.remove(items[which]);
                                }
                            }
                        }
                )
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                like_style.setText(list_customer.toString());
                            }
                        })
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.create().show();
            }
        });
        // 남 여에 따라 선택할 수 있는 머리길이가 다름
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer_man = (TableRow)findViewById(R.id.customer_man);
                customer_woman = (TableRow) findViewById(R.id.customer_woman);
                customer_man.setVisibility(View.VISIBLE);
                customer_woman.setVisibility(View.GONE);
                kind_sex = "man";
            }
        });

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer_man = (TableRow)findViewById(R.id.customer_man);
                customer_woman = (TableRow) findViewById(R.id.customer_woman);
                customer_woman.setVisibility(View.VISIBLE);
                customer_man.setVisibility(View.GONE);
                kind_sex = "woman";
            }
        });

            spinner_man.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     length = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            spinner_woman.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     length = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
                /*남여 분류 끝*/
        // 헤어모델과 스테프에 따라 작성할 정보가 다름
        customer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer_lay = (TableLayout)findViewById(R.id.customer_lay);
                staff_lay = (TableLayout)findViewById(R.id.staff_lay);
                customer_lay.setVisibility(View.VISIBLE);
                staff_lay.setVisibility(View.GONE);
                kind_user = "3";
            }
        });
        staff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "예약 가능 설정을 활성화하여 저장하면 자동으로 디자이너 리스트에 추가됩니다.", Toast.LENGTH_SHORT).show();
                customer_lay = (TableLayout)findViewById(R.id.customer_lay);
                staff_lay = (TableLayout)findViewById(R.id.staff_lay);
                staff_lay.setVisibility(View.VISIBLE);
                customer_lay.setVisibility(View.GONE);
                kind_user = "7";
            }
        });

        place_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                startActivityForResult(intent,10);
            }
        });
        place_select_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                startActivityForResult(intent,15);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(kind_sex != "man"){
            man.setChecked(true);
        } else if(kind_sex == "woman"){
            woman.setChecked(true);
        }
        // 이 부분 확인해야 함 왜 man인데 man을 인식하지 못하는지 확인!!!!!!!!!!!!!!!!!!!!!!!!
        if(kind_user != "7"){
            staff_btn.setChecked(true);
            customer_lay = (TableLayout)findViewById(R.id.customer_lay);
            staff_lay = (TableLayout)findViewById(R.id.staff_lay);
            staff_lay.setVisibility(View.VISIBLE);
            customer_lay.setVisibility(View.GONE);
            experience.setText(experience_str);
            experience.setFocusable(false);
            sel_method.setText(list_str);
            place_select.setText(place);
            addr.setText(address_str);
            if(checked_designer == 1){
                fin_profile.setChecked(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 10){
            String address = data.getStringExtra("address");
            String address_mapinfo = data.getStringExtra("addr");
            addr.setText(address_mapinfo);
            place_select.setText(address);
        } else if (requestCode == 15 && resultCode == 10){
            String address = data.getStringExtra("address");
            String address_mapinfo = data.getStringExtra("addr");
            addr_customer.setText(address_mapinfo);
            place_select_customer.setText(address);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);
        ImageButton btnsave = (ImageButton) findViewById(R.id.btnSave);
        ImageButton bactbtn = (ImageButton)findViewById(R.id.btnBack);
        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kind_user == "7"){
//                    userID, profile_img_string, kind_user, kind_sex, list ;
                    String nick_str= nametxt.getText().toString();
                    String age_str = showAge.getText().toString();
                    String experience_str = experience.getText().toString();
                    String place = place_select.getText().toString();
                    String address_str = addr.getText().toString();
                    if(profile_img_string==null){
                        profile_img_string = "default";
                    }
//                    checked_designer;


                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");

                                        if(success) {
                                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
                                            builder.setMessage("프로필을 저장했습니다.")
                                                    .setPositiveButton("확인", null)
                                                    .create()
                                                    .show();
                                        } else {
                                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
                                            builder.setMessage("프로필 저장에 실패했습니다.")
                                                    .setNegativeButton("확인", null)
                                                    .create()
                                                    .show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            ProfileSaveRequest profileSaveRequest = new ProfileSaveRequest(userID, profile_img_string, nick_str, age_str, kind_sex, kind_user, experience_str, list.toString(), place, address_str, checked_designer, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
                            queue.add(profileSaveRequest);
                }

            }
        });

        return true;
    }
}




