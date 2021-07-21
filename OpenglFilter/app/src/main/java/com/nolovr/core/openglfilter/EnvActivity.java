package com.nolovr.core.openglfilter;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import com.nnolovr.core.openglfilter.R;

public class EnvActivity extends AppCompatActivity {

    MediaProjectionManager mediaProjectionManager;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env);
        checkPermission();
        app = (App) getApplication();
        if (app.sence == app.senceMediaProjection) {
            // TODO: 2021/7/21
            // 截屏
            mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, 1);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != 1) return;
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            return;
        }


        app.mediaProjection = mediaProjection;

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();

//        MediaProjection录屏  ----》
        // TODO: 2021/7/21 录屏开始
//        HandlerThread handlerThread = new HandlerThread("codec-gl");
//        handlerThread.start();
//        Handler mHandler = new Handler(handlerThread.getLooper());
//
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

    }


    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 1);

        }
        return false;
    }
}