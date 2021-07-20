package com.nolovr.core.openglfilter;

import android.content.Context;

import com.nnolovr.core.openglfilter.R;

public class RecordFilter extends AbstractFilter {
//    输出屏幕
    public RecordFilter(Context context){
        super(context, R.raw.base_vert, R.raw.base_frag);
    }

}
