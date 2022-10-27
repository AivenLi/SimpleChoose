package com.aiven.hfl.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.viewpager.widget.ViewPager;

import com.aiven.hfl.view.FloatView;

public class FloatManager {

    private static FloatManager mInstance = null;
    private final FloatView mFloatView;
    private boolean isShowing = false;
    private int position = -1;
    private Class aClass;

    private FloatManager(Context context) {
        mFloatView = new FloatView(context, 0, 0);
    }

    public static FloatManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (FloatManager.class) {
                if (mInstance == null) {
                    mInstance = new FloatManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public void startFloat(View view) {
        if (isShowing) {
            return;
        }
        removeViewFromParent(view);
        mFloatView.removeAllViews();
        mFloatView.addView(view);
        mFloatView.addViewToWindow();
        isShowing = true;
    }

    public void stopFloat() {
        if (!isShowing) {
            return;
        }
        mFloatView.removeViewFromWindow();
        removeViewFromParent(mFloatView.getChildAt(0));
    }

    public void setFloatViewVisible() {
        if (isShowing) {
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class aClass) {
        this.aClass = aClass;
    }

    public Class getActClass() {
        return aClass;
    }


    private void removeViewFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewParent parentView = view.getParent();
        if (parentView instanceof FrameLayout) {
            ((FrameLayout)parentView).removeView(view);
        }
    }
}
