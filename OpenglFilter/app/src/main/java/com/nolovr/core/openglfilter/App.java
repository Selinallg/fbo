package com.nolovr.core.openglfilter;

import android.app.Application;
import android.media.projection.MediaProjection;


public class App extends Application {

// Camera
    //    private int width  = 480;
//    private int height = 640;

    // MediaProjection
    public static int width  = 720;
    public static int height = 1280;

    public int senceCamera          = 1;
    public int senceMediaProjection = 2;
    int sence = senceMediaProjection;

    public MediaProjection mediaProjection;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
