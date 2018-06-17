package com.example.msi.connect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;

import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Newcamera extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

            private Bitmap bmp_result;
            private Mat m_matRoi;
            private Rect mRectRoi;

    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    private OrientationEventListener mOrientEventListener;
    private android.widget.RelativeLayout.LayoutParams mRelativeParams;
    RelativeLayout camera_lay;
    private Button mBtnOcrStart;
    private Button mBtnFinish;
    Button capture_save, capture_cancel;
    private TextView mTextOcrResult;
    private double m_dWscale;
    private double m_dHscale;
    private SurfaceView mSurfaceRoi;
    int rotate_cam = 1;
    private int mRoiWidth;
    private int mRoiHeight;
    private int mRoiX;
    private int mRoiY;
    Bitmap captureImg;
    ImageView capture_view;
    RelativeLayout capture_lay;

    private mOrientHomeButton mCurrOrientHomeButton = mOrientHomeButton.Right;
//    Camera mCamera;
    private enum mOrientHomeButton {Right, Bottom, Left, Top}

    //    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public static native long loadCascade(String cascadeFileName);

    public static native void detect(long cascadeClassifier_face,
                                     long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    public int convertDpToPixel(float dp) {
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public void rotateViews(int degree) {
        degree = degree - 180;

        mBtnOcrStart.setRotation(degree);
        mBtnFinish.setRotation(degree);
        mTextOcrResult.setRotation(degree);

        switch (degree) {
            // 가로
            case 90:
            case 270:
                //ROI 크기 조정 비율 변경
                m_dWscale = (double) 1 / 2;
                m_dHscale = (double) 1 / 2;
                //결과 TextView 위치 조정
                mRelativeParams = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mRelativeParams.setMargins(0, convertDpToPixel(20), 0, 0);
                mRelativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                mTextOcrResult.setLayoutParams(mRelativeParams);
                break;
            // 세로
            case 0:
            case 180:
                m_dWscale = (double) 2 / 4;    //h (반대)
                m_dHscale = (double) 4 / 4;    //w
                mRelativeParams = new android.widget.RelativeLayout.LayoutParams(convertDpToPixel(300), ViewGroup.LayoutParams.WRAP_CONTENT);
                mRelativeParams.setMargins(convertDpToPixel(5), 0, 0, 0);
                mRelativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
                mTextOcrResult.setLayoutParams(mRelativeParams);
                break;
        }
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;
        AssetManager assetManager = this.getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Log.d(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
        }
    }

    @Override
    public void recreate() {
        super.recreate();
        if (rotate_cam == 1) {
            rotate_cam = 0;
        } else {
            rotate_cam = 1;
        }
    }

    public void onClickButton(View v) {

        switch (v.getId()) {
            //Start 버튼 클릭 시
            case R.id.btn_ocrstart:
                // 버튼 속성 변경
                bmp_result = Bitmap.createBitmap(m_matRoi.cols(), m_matRoi.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(m_matRoi, bmp_result);
                capture_lay.setVisibility(View.VISIBLE);
                capture_view.setImageBitmap(bmp_result);
//                capture_view.setRotation(270);

//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp_result.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] bytes = stream.toByteArray();
//                Intent intent1 = new Intent(Newcamera.this, CaptureActivity.class);
//                intent1.putExtra("BMP", bytes);
//                startActivity(intent1);
                mOpenCvCameraView.disableView();
                camera_lay.setVisibility(View.INVISIBLE);
//                finish();
                break;

            // 카메라 화면 전환 버튼 클릭 시
            case R.id.btn_finish:
                if (rotate_cam == 1) {
                    rotate_cam = 0;
                    Intent intent = new Intent(Newcamera.this, Newcamera.class);
                    intent.putExtra("rotate_cam", rotate_cam);
                    setResult(75,intent);
                    finish();
                } else {
                    rotate_cam = 1;
                    Intent intent = new Intent(Newcamera.this, Newcamera.class);
                    intent.putExtra("rotate_cam", rotate_cam);
                    setResult(75,intent);
                    finish();
                }
                break;
            case R.id.capture_save:
                ImageConvert imageConvert = new ImageConvert();
                BitmapDrawable d = (BitmapDrawable)((ImageView) findViewById(R.id.capture_view)).getDrawable();
                Bitmap bmp = d.getBitmap();
                Bitmap resized = Bitmap.createScaledBitmap(bmp, 250, 300, true);
                String img = imageConvert.BitMapToString(resized);
                Intent intent1 = new Intent(Newcamera.this, Addshowroom.class);
                intent1.putExtra("img", img);
                setResult(20, intent1);
                finish();
                break;
        }
    }

    private void read_cascade_file() {
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");
        Log.d(TAG, "read_cascade_file:");
        cascadeClassifier_face = loadCascade("haarcascade_frontalface_alt.xml");
        Log.d(TAG, "read_cascade_file:");
        cascadeClassifier_eye = loadCascade("haarcascade_eye_tree_eyeglasses.xml");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_newcamera);

        Intent intent = getIntent();
        rotate_cam = intent.getIntExtra("rotate_cam", 1);
        camera_lay = (RelativeLayout) findViewById(R.id.camera_lay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else read_cascade_file();
        } else read_cascade_file();

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mOpenCvCameraView.setCameraIndex(rotate_cam); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
       // 캡쳐 레이아웃 선언
        capture_lay = (RelativeLayout) findViewById(R.id.capture_lay);
        capture_view = (ImageView) findViewById(R.id.capture_view);
        capture_save = (Button) findViewById(R.id.capture_save);
        capture_cancel = (Button) findViewById(R.id.capture_save);

        //뷰 선언
        mBtnOcrStart = (Button) findViewById(R.id.btn_ocrstart);
        mBtnFinish = (Button) findViewById(R.id.btn_finish);
        mTextOcrResult = (TextView) findViewById(R.id.text_ocrresult);
        mSurfaceRoi = (SurfaceView) findViewById(R.id.surface_roi);

        mOrientEventListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int arg0) {
                //방향센서값에 따라 화면 요소들 회전
                // 0˚ (portrait)
                if (arg0 >= 315 || arg0 < 45) {
                    rotateViews(90);
                    mCurrOrientHomeButton = mOrientHomeButton.Top;
                    // 90˚
                } else if (arg0 >= 45 && arg0 < 135) {
                    rotateViews(0);
                    mCurrOrientHomeButton = mOrientHomeButton.Left;
                    // 180˚
                } else if (arg0 >= 135 && arg0 < 225) {
                    rotateViews(270);
                    mCurrOrientHomeButton = mOrientHomeButton.Bottom;
                    // 270˚ (landscape)
                } else {
                    rotateViews(180);
                    mCurrOrientHomeButton = mOrientHomeButton.Right;
                }

                //ROI 선 조정
                mRelativeParams = new android.widget.RelativeLayout.LayoutParams(mRoiWidth + 5, mRoiHeight + 5);
                mRelativeParams.setMargins(mRoiX, mRoiY, 0, 0);
                mSurfaceRoi.setLayoutParams(mRelativeParams);
                //ROI 영역 조정
                mRelativeParams = new android.widget.RelativeLayout.LayoutParams(mRoiWidth - 5, mRoiHeight - 5);
                mRelativeParams.setMargins(mRoiX + 5, mRoiY + 5, 0, 0);
                mSurfaceRoi.setLayoutParams(mRelativeParams);
            }
        };

        //방향센서 핸들러 활성화
        mOrientEventListener.enable();
        //방향센서 인식 오류 시, Toast 메시지 출력 후 종료
        if (!mOrientEventListener.canDetectOrientation()) {
            Toast.makeText(this, "Can't Detect Orientation",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();
        // 가로, 세로 사이즈 획득
        mRoiWidth = (int) (matInput.size().width * m_dWscale);
        mRoiHeight = (int) (matInput.size().height * m_dHscale);
        // 사이즈로 중심에 맞는 X , Y 좌표값 계산
        mRoiX = (int) (matInput.size().width - mRoiWidth) / 2;
        mRoiY = (int) (matInput.size().height - mRoiHeight) / 2;

        if (matResult != null) matResult.release();
        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
//        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        Core.flip(matInput, matInput, 1);
        detect(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(),
                matResult.getNativeObjAddr());

        mRectRoi = new Rect(mRoiX, mRoiY, mRoiWidth, mRoiHeight);
        m_matRoi = matResult.submat(mRectRoi);

        m_matRoi.copyTo(matResult.submat(mRectRoi));
        return matResult;
    }

    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    //    String[] PERMISSIONS  = {"android.permission.CAMERA"};
    String[] PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }
        //모든 퍼미션이 허가되었음
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else {
                        read_cascade_file();
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Newcamera.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}
