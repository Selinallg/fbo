package com.nolovr.core.openglfilter;

import android.content.Context;

import com.nnolovr.core.openglfilter.R;

public class ScreenFilter extends AbstractFilter {
    public ScreenFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.base_frag);
    }
}
