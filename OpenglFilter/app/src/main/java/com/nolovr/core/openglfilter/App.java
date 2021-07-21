package com.nolovr.core.openglfilter;

import android.app.Application;
import android.media.projection.MediaProjection;


public class App extends Application {

    public int senceCamera          = 1;
    public int senceMediaProjection = 2;
    int sence = senceCamera;

    public MediaProjection mediaProjection;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
