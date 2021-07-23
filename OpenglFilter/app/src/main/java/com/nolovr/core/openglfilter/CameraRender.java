package com.nolovr.core.openglfilter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRender implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {
    private static final String            TAG = "CameraRender";
    private final        App               app;
    private              CameraHelper      cameraHelper;
    private              CameraView        cameraView;
    private              SurfaceTexture    mCameraTexure;
    private              H264MediaRecorder mRecorder;
    //    int
    private              CameraFilter      cameraFilter;
    private              SoulFilter        soulFilter;
    private              BeautyFilter      beautyFilter;
    private              RecordFilter      recordFilter;
    //    private SplitFilter splitFilter;
    private              int[]             textures;
    float[] mtx = new float[16];

    // mediaProjection 截屏
    VirtualDisplay  virtualDisplay;
    MediaProjection mediaProjection = null;

    public void setMediaProjection(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
    }


    public CameraRender(CameraView cameraView) {
        this.cameraView = cameraView;
        app = (App) cameraView.getContext().getApplicationContext();
        LifecycleOwner lifecycleOwner = (LifecycleOwner) cameraView.getContext();
//        打开摄像头
        if (app.sence == app.senceCamera) {
            cameraHelper = new CameraHelper(lifecycleOwner, this);
        }

    }

    //textures 主线程    1   EGL线程
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated: ");

//surface
        if (app.sence != app.senceCamera) {
            if (mediaProjection == null) {
                Log.e(TAG, "onSurfaceCreated: mediaProjection==null");
                return;
            }
            // ①手动创建一个Surface start ;
            textures = new int[1];
            int mTextureId = textures[0];
            mCameraTexure = new SurfaceTexture(mTextureId);
            Surface mSurface = new Surface(mCameraTexure);
            // ①手动创建一个Surface end ;


            //② mediaProjection
            //创建场地
            virtualDisplay = mediaProjection.createVirtualDisplay(
                    "-display",
                    App.width, App.height, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mSurface, null, null);
        } else {
            textures = new int[1];
            mCameraTexure.attachToGLContext(textures[0]);
        }


//        1
//        让 SurfaceTexture   与 Gpu  共享一个数据源  0-31
//        mCameraTexure.attachToGLContext(textures[0]);
//监听摄像头数据回调，
        mCameraTexure.setOnFrameAvailableListener(this);
        Context context = cameraView.getContext();
        cameraFilter = new CameraFilter(context);
        recordFilter = new RecordFilter(context);
//        beautyFilter = new BeautyFilter(context);
//        soulFilter = new SoulFilter(context);
//        splitFilter = new SplitFilter(context);
        File file = new File(Environment.getExternalStorageDirectory(), "input.mp4");
        if (file.exists()) {
            file.delete();
        }

        String path = file.getAbsolutePath();
        mRecorder = new H264MediaRecorder(cameraView.getContext(), path,
                EGL14.eglGetCurrentContext(),
                App.width, App.height);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: ");
//
        recordFilter.setSize(width, height);
        cameraFilter.setSize(width, height);
//        beautyFilter.setSize(width, height);
//        soulFilter.setSize(width, height);
//        splitFilter.setSize(width,height);
    }

    //  有数据的时候给
    @Override
    public void onDrawFrame(GL10 gl) {
//        Log.i(TAG, "线程: " + Thread.currentThread().getName());
//        摄像头的数据  ---》
//        更新摄像头的数据  给了  gpu
        mCameraTexure.updateTexImage();
//        不是数据
        mCameraTexure.getTransformMatrix(mtx);
        cameraFilter.setTransformMatrix(mtx);
//int   数据   byte[]


//id     FBO所在的图层   纹理  摄像头 有画面      有1  没有  画面       录屏
        int id = cameraFilter.onDraw(textures[0]);
        Log.d(TAG, "onDrawFrame: cameraFilter id=" + id + "|" + mCameraTexure.getTimestamp());
// 加载   新的顶点程序 和片元程序  显示屏幕  id  ----》fbo--》 像素详细
//        显示到屏幕
//        id =  soulFilter.onDraw(id);
//        id = splitFilter.onDraw(id);

//        是一样的
        boolean isOpen = true;
        if (beautyFilter != null) {
//            行     打开 还是不打开 美颜滤镜    资源泄露
            id = beautyFilter.onDraw(id);
        }

        id = recordFilter.onDraw(id);

//        拿到了fbo的引用   ---》  编码视频      输出  直播推理
//        起点
//           起点数据  主动调用   opengl的函数  录制
        mRecorder.fireFrame(id, mCameraTexure.getTimestamp());
        Log.d(TAG, "onDrawFrame: fireFrame id=" + id);
    }

    //
    @Override
    public void onUpdated(Preview.PreviewOutput output) {
        Log.d(TAG, "onUpdated: ");
        // TODO: 2021/7/21 很重要，纹理在这里获取
        // Camera 的时候最先调用

//        摄像头预览到的数据 在这里
        mCameraTexure = output.getSurfaceTexture();
    }

    //当有数据 过来的时候
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onFrameAvailable: " + surfaceTexture);
//一帧 一帧回调时
        cameraView.requestRender();
    }

    public void startRecord(float speed) {
        try {
            mRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        mRecorder.stop();
    }

    public void enableBeauty(final boolean isChecked) {

        cameraView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (isChecked) {
                    if (beautyFilter == null) {
                        beautyFilter = new BeautyFilter(cameraView.getContext());
                    }
                    if (beautyFilter != null) {
                        beautyFilter.setSize(cameraView.getWidth(), cameraView.getHeight());
                    }
                } else {
                    if (beautyFilter != null) {
                        beautyFilter.release();
                        beautyFilter = null;
                    }
                }
            }
        });
//        Opengl 线程  来做   fbo
    }

}
