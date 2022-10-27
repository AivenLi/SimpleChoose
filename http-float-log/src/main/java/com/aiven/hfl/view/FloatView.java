package com.aiven.hfl.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.aiven.hfl.R;
import com.aiven.hfl.util.DeviceUtil;

public class FloatView extends FrameLayout {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private int mDownRawX, mDownRawY;//手指按下时相对于屏幕的坐标
    private int mDownX, mDownY;//手指按下时相对于悬浮窗的坐标

    public FloatView(@NonNull Context context, int x, int y) {
        super(context);
        mDownX = x;
        mDownY = y;
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.shape_float_window_background);
        int padding = dp2px(1);
        setPadding(padding, padding, padding, padding);
        initWindow();
    }

    private void initWindow() {
        windowManager = DeviceUtil.getWindowManager(getContext().getApplicationContext());
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            layoutParams.type =  WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.windowAnimations = R.style.FloatWindowAnimation;
        layoutParams.gravity = Gravity.START | Gravity.TOP; // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        int width = dp2px(250);
        layoutParams.width = width;
        layoutParams.height = width * 9 / 16;
        layoutParams.x = mDownX;
        layoutParams.y = mDownY;
    }


    public boolean addViewToWindow() {
        if (windowManager != null) {
            if (!isAttachedToWindow()) {
                windowManager.addView(this, layoutParams);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean removeViewFromWindow() {
        if (windowManager != null) {
            if (isAttachedToWindow()) {
                windowManager.removeViewImmediate(this);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                mDownRawX = (int) ev.getRawX();
                mDownRawY = (int) ev.getRawY();
                mDownX = (int) ev.getX();
                mDownY = (int) (ev.getY() + DeviceUtil.getStatusBarHeight(getContext()));
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getRawX() - mDownRawX);
                float absDeltaY = Math.abs(ev.getRawY() - mDownRawY);
                intercepted = absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop();
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            layoutParams.x = x - mDownX;
            layoutParams.y = y - mDownY;
            windowManager.updateViewLayout(this, layoutParams);
        }
        return super.onTouchEvent(event);
    }

    private float dp2px(float dp) {
        return getContext().getResources().getDisplayMetrics().density * dp;
    }

    private int dp2px(int dp) {
        return (int)dp2px((float) dp);
    }
}
