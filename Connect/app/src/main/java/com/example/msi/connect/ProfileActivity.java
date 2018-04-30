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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TableLayout staff_lay, customer_lay;
    TableRow customer_man, customer_woman;

    String length;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final List<String> list = new ArrayList<String>();
        final List<String> list_customer = new ArrayList<String>();


        Intent intent = getIntent();
        String profile_img_string = intent.getStringExtra("profile_img_string");
        String userID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");
        String userAge = intent.getStringExtra("userAge")+"";

        ImageView profile_img = (ImageView) findViewById(R.id.profile_img);
        TextView showID = (TextView) findViewById(R.id.showID);
        TextView showAge = (TextView) findViewById(R.id.showAge);
        EditText nametxt = (EditText)findViewById(R.id.nicknametxt);
        final TextView sel_method = (TextView) findViewById(R.id.sel_method);
        final TextView like_style = (TextView) findViewById(R.id.like_style);

        RadioGroup sex = (RadioGroup) findViewById(R.id.radioGroup_sex);
        RadioGroup kind = (RadioGroup) findViewById(R.id.radioGroup_kind);
        RadioButton customer_btn = (RadioButton) findViewById(R.id.customer_btn);
        RadioButton staff_btn = (RadioButton) findViewById(R.id.staff_btn);
        RadioButton man = (RadioButton) findViewById(R.id.man);
        RadioButton woman = (RadioButton) findViewById(R.id.woman);



        Spinner spinner_man = (Spinner)findViewById(R.id.spinner1);
        Spinner spinner_woman = (Spinner)findViewById(R.id.spinner2);
        showID.setText(userID);
        nametxt.setText(userName);
        showAge.setText(userAge+"");
        if(profile_img_string != null){
            Picasso.with(this).load(profile_img_string).into(profile_img);
        }

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
            }
        });

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer_man = (TableRow)findViewById(R.id.customer_man);
                customer_woman = (TableRow) findViewById(R.id.customer_woman);
                customer_woman.setVisibility(View.VISIBLE);
                customer_man.setVisibility(View.GONE);
            }
        });
        spinner_man.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                length = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner_woman.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                length = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
                /**/
        // 헤어모델과 스테프에 따라 작성할 정보가 다름
        customer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer_lay = (TableLayout)findViewById(R.id.customer_lay);
                staff_lay = (TableLayout)findViewById(R.id.staff_lay);
                customer_lay.setVisibility(View.VISIBLE);
                staff_lay.setVisibility(View.GONE);
            }
        });
        staff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "프로필을 완성하면 자동으로 디자이너 리스트에 추가됩니다.", Toast.LENGTH_SHORT).show();
                customer_lay = (TableLayout)findViewById(R.id.customer_lay);
                staff_lay = (TableLayout)findViewById(R.id.staff_lay);
                staff_lay.setVisibility(View.VISIBLE);
                customer_lay.setVisibility(View.GONE);
            }
        });

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
        ImageView bactbtn = (ImageView)findViewById(R.id.btnBack);
        bactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        return true;
    }
}




