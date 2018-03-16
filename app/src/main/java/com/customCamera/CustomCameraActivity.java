package com.customCamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cameraRotation.R;

import static android.content.ContentValues.TAG;

public class CustomCameraActivity extends Activity implements View.OnClickListener,
        SurfaceHolder.Callback {

    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    boolean hasFlash = false;
    int isLighOn = 0;
    boolean isFlashOn;
    Camera mCamera;
    Camera.Parameters mParameters;
    LinearLayout lin_flash;
    ImageView img_flash;
    private Button Btn_Flashlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_camera);

        Btn_Flashlight = (Button) findViewById(R.id.btn_Flashlight);
        lin_flash = findViewById(R.id.lin_flash);
        img_flash = findViewById(R.id.img_flash);

        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        surfaceView = (SurfaceView) this.findViewById(R.id.hiddenSurfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        getCamera();

        Btn_Flashlight.setOnClickListener(this);
        lin_flash.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == Btn_Flashlight) {
            if (hasFlash) {
                if (isLighOn == 1) {
                    turnOffFlash();
                    isLighOn = 0;
                } else {
                    turnOnFlash();
                    isLighOn = 1;
                }
            } else {
                Dialog();
            }

        } else if (v == lin_flash) {
            if (hasFlash) {
                if (isLighOn == 1) {
                    turnOffFlash();
                    isLighOn = 0;

                    img_flash.setImageDrawable(getResources().getDrawable(R.drawable.flash_off_white));
                } else {
                    turnOnFlash();
                    isLighOn = 1;

                    img_flash.setImageDrawable(getResources().getDrawable(R.drawable.flash_on_indicator_white));
                }
            } else {
                Dialog();
            }
        }


    }

    @SuppressLint("LongLogTag")
    private void getCamera() {

        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mParameters = mCamera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }

    private void turnOnFlash() {
        if (!isFlashOn) {
            if (mCamera == null || mParameters == null) {
                return;
            }
            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();
            isFlashOn = true;
        }
    }

    private void turnOffFlash() {
        if (isFlashOn) {
            if (mCamera == null || mParameters == null) {
                return;
            }
            mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
            mCamera.stopPreview();
            isFlashOn = false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected IO Exception in setPreviewDisplay()", e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub   
    }

    public void Dialog() {

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            final AlertDialog alert = new AlertDialog.Builder(CustomCameraActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    alert.dismiss();
                }
            });
            alert.show();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        turnOffFlash();
    }
}