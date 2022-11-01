package com.aiven.hfl.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.aiven.hfl.bean.HttpLogBean;
import com.aiven.hfl.view.FloatView;


public class FloatManager {

    public static final int HTTP_LOG_WHAT = 12345;
    private static FloatManager mInstance = null;
    private final FloatView mFloatView;
    private boolean isShowing = false;
    private int position = -1;
    private Class aClass;
    private Handler handler;

    private FloatManager(Context context) {
        mFloatView = new FloatView(context);
        initHandler();
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

    public void release() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        mInstance = null;
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == HTTP_LOG_WHAT) {
                    HttpLogBean data = (HttpLogBean) msg.obj;
                    if (data == null) {
                        mFloatView.clear();
                    } else {
                        mFloatView.setLogData(data);
                    }
                }
            }
        };
    }

    public Handler getHandler() {
        return handler;
    }

    public void startFloat() {
        if (isShowing) {
            return;
        }
        mFloatView.addViewToWindow();
        isShowing = true;
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

    public void appToBackground() {
        mFloatView.appToBackground();
    }

    public void appToForeground() {
        mFloatView.appToForeground();
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
