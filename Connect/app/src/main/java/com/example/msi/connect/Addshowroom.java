package com.example.msi.connect;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Addshowroom extends AppCompatActivity {

    EditText content_txt;
    TextView id_txt;
    ImageView camera, album, picture_img;
    ImageView rotate_btn;
    String userID;
    private String mCurrentPhotoPath;
    int camera_code = 0;
    int gallery_code = 11;
    int mDegree = 90;

    public Bitmap rotateImg(Bitmap bitmap, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    void pickPickure(){
        Intent intent = new Intent(android.content.Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, gallery_code);
    }

    private File createImageFile() throws IOException { //https://developer.android.com/training/camera/photobasics.html 라이브러리 이용
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    void requirePermission(){//권한 요청 메소드
        String [] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE,Manifest.permission.INTERNET};
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        for(String permission :permissions){
            if(ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
                listPermissionsNeeded.add(permission);  //권한이 허가가 안됐을 경우 요청할 권한을 모집
            }
        }
        if(!listPermissionsNeeded.isEmpty()){        //권한을 요청함
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
        }
    }

    private void dialog(final View context){
        AlertDialog.Builder builder = new AlertDialog.Builder(Addshowroom.this);
        builder.setTitle("카메라 선택");
        builder.setNegativeButton("촬영(일반)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean camera = ContextCompat.checkSelfPermission(context.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                boolean write = ContextCompat.checkSelfPermission(context.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (camera && write) {
                    Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        File photofile = createImageFile();
                        Uri photouri = FileProvider.getUriForFile(Addshowroom.this, "com.example.msi.connect.fileprovider", photofile);
                        intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                        startActivityForResult(intent_camera, camera_code); //카메라와 저장공간 허가 시 requestCode 0 을 가지고 ActivityForResult 생성
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        builder.setPositiveButton("촬영(얼굴 가리기)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Newcamera.class);
                startActivityForResult(intent, 20);
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addshowroom);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        camera = (ImageView) findViewById(R.id.camera);
        album = (ImageView) findViewById(R.id.select_image);
        id_txt = (TextView) findViewById(R.id.id_txt);
        picture_img = (ImageView) findViewById(R.id.picture_img);
        content_txt = (EditText) findViewById(R.id.editText);
        rotate_btn = (ImageView) findViewById(R.id.rotate_btn);

        id_txt.setText(userID);

        requirePermission();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog(v);
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPickure();
            }
        });

        rotate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Bitmap bitmap = ((BitmapDrawable) picture_img.getDrawable()).getBitmap();
               picture_img.setImageBitmap(rotateImg(bitmap,mDegree));
            }
        });
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == camera_code && resultCode == RESULT_OK){
            picture_img.setImageBitmap( rotateImg(BitmapFactory.decodeFile(mCurrentPhotoPath),90));
            picture_img.setVisibility(View.VISIBLE);
            rotate_btn.setVisibility(View.VISIBLE);
        } else if (requestCode == gallery_code && resultCode == RESULT_OK){
            Uri uri = data.getData();
            picture_img.setImageURI(uri);
            picture_img.setVisibility(View.VISIBLE);
            rotate_btn.setVisibility(View.VISIBLE);
        }
//        if(requestCode == gallery_code && resultCode == RESULT_OK){
//            Uri uri = data.getData();
//            picture_img.setImageURI(uri);
//        }
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
        View actionbar = inflater.inflate(R.layout.showroom_actionbar, null);
        actionBar.setCustomView(actionbar);
        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);
        ImageButton btnsave = (ImageButton)findViewById(R.id.btnsave_showroom);
        ImageButton backbtn = (ImageButton)findViewById(R.id.btnBack_showroom);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = content_txt.getText().toString();
                Bitmap bitmap = ((BitmapDrawable) picture_img.getDrawable()).getBitmap();
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 350, true);
                String img = BitMapToString(resized);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Addshowroom.this);
                                builder.setMessage("글을 등록했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create()
                                        .show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                AddpictureRequest addpictureRequest = new AddpictureRequest(userID, content, img, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Addshowroom.this);
                queue.add(addpictureRequest);
            }
        });
        return true;
    }
}
