package com.aiven.hfl.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiven.hfl.R;
import com.aiven.hfl.adapter.HttpLogAdapter;
import com.aiven.hfl.bean.HttpLogBean;
import com.aiven.hfl.util.DeviceUtil;

public class FloatView extends FrameLayout {

    private static final String TAG = "浮窗";

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private HttpLogAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView imgClose;
    private TextView tvTitle;
    private TextView tvClear;
    private FrameLayout fltTitle;
    private boolean moveFloatCirView = false;
    private boolean isMove = false;
    private boolean showCirView = true;
    private final int wWidth;
    private final int wHeight;

    private final int widthPix;
    private final int heightPix;

    private final int statusBarHeight;
    private final int titleBarHeight;
    private final int windownY;

    private int cx;
    private int cy;

    private final int CIR_WIDTH;
    private final int CIR_HEIGHT;
    private final int CIR_X;
    private final int CIR_Y;

    private int mDownRawX, mDownRawY;//手指按下时相对于屏幕的坐标
    private int mDownX, mDownY;//手指按下时相对于悬浮窗的坐标

    public FloatView(@NonNull Context context) {
        super(context);
        CIR_WIDTH = dp2px(24);
        CIR_HEIGHT = dp2px(24);
        statusBarHeight = (int)DeviceUtil.getStatusBarHeight(context);
        titleBarHeight = dp2px(44);
        windownY = statusBarHeight + titleBarHeight;
        widthPix = context.getResources().getDisplayMetrics().widthPixels;
        heightPix = context.getResources().getDisplayMetrics().heightPixels;
        //Log.d(TAG, "w: " + widthPix + "h: " + heightPix);
        CIR_X = CIR_WIDTH;
        CIR_Y = CIR_HEIGHT;
        // 设置悬浮窗口长宽数据
        wWidth = widthPix - dp2px(24);
        wHeight = heightPix - dp2px(50);
        init(context);
    }

    private void init(Context context) {
        int padding = dp2px(1);
        setPadding(padding, padding, padding, padding);
        initWindow(context);
    }

    private void initWindow(Context context) {
        windowManager = DeviceUtil.getWindowManager(context.getApplicationContext());
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
        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        cx = CIR_X;
        cy = CIR_Y;
        setViewToAPoint();
        initTitleBar(context);
        initLogView(context);
    }

    private void setViewToAPoint() {
        layoutParams.width = CIR_WIDTH;
        layoutParams.height = CIR_HEIGHT;
        layoutParams.x = cx;
        layoutParams.y = cy;
        setBackgroundResource(R.drawable.radius_50_blue_bg);
        removeAllViews();
    }

    private void initTitleBar(Context context) {
        fltTitle = new FrameLayout(context);
        fltTitle.setBackgroundColor(0xffededed);
        fltTitle.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, dp2px(32)));
        tvTitle = new TextView(context);
        tvTitle.setText("Http Log");
        tvTitle.setTextSize(14);
        tvTitle.setTextColor(Color.BLACK);
        LayoutParams tvTitleLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvTitleLp.gravity = Gravity.CENTER;
        tvTitle.setLayoutParams(tvTitleLp);
        tvClear = new TextView(context);
        tvClear.setText("Clear Log");
        tvClear.setTextSize(12);
        tvClear.setTextColor(0xff999999);
        LayoutParams tvClearLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvClearLp.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        tvClearLp.leftMargin = dp2px(12);
        tvClear.setLayoutParams(tvClearLp);
        imgClose = new ImageView(context);
        imgClose.setImageResource(R.drawable.ic_baseline_close_24);
        LayoutParams imgCloseLp = new LayoutParams(dp2px(24), dp2px(24));
        imgCloseLp.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        imgCloseLp.rightMargin = dp2px(12);
        imgClose.setLayoutParams(imgCloseLp);
        fltTitle.addView(tvTitle);
        fltTitle.addView(imgClose);
        fltTitle.addView(tvClear);
    }

    private void initLogView(Context context) {
        adapter = new HttpLogAdapter(context);
        recyclerView = new RecyclerView(context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        lp.topMargin = dp2px(50);
        recyclerView.setLayoutParams(lp);
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
                mDownRawX = (int) ev.getRawX();
                mDownRawY = (int) ev.getRawY();
                mDownX = mDownRawX;
                mDownY = mDownRawY;
//                mDownX = (int) ev.getX();
//                mDownY = (int) (ev.getY() + DeviceUtil.getStatusBarHeight(getContext()));
                break;
            case MotionEvent.ACTION_MOVE:
                if (showCirView) {
                    float absDeltaX = Math.abs(ev.getRawX() - mDownRawX);
                    float absDeltaY = Math.abs(ev.getRawY() - mDownRawY);
                    intercepted = absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                            absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop();
                }
                break;
        }
        return intercepted;
    }

    private boolean clickClose = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isMove = false;
            clickClose = false;
            if (!showCirView && isClickClose(event.getX(), event.getY())) {
                //Log.d(TAG, "点击关掉窗口");
                setViewToAPoint();
                windowManager.updateViewLayout(this, layoutParams);
                showCirView = true;
                clickClose = true;
                return true;
            } else if (!showCirView && isClickClear(event.getX(), event.getY())) {
                adapter.clear();
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            isMove = true;
            if (showCirView) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                layoutParams.x = layoutParams.x - x + mDownX;
                layoutParams.y = layoutParams.y - y + mDownY;
                mDownX = x;
                mDownY = y;
                windowManager.updateViewLayout(this, layoutParams);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isMove && !clickClose && showCirView) {
                layoutParams.width = wWidth;
                layoutParams.height = wHeight;
                cx = layoutParams.x;
                cy = layoutParams.y;
                layoutParams.x = (widthPix - wWidth) / 2;
                layoutParams.y = (heightPix - wHeight) / 2;
                if (!(fltTitle.getParent() instanceof FloatView)) {
                    addView(fltTitle);
                }
                if (!(recyclerView.getParent() instanceof FloatView)) {
                    addView(recyclerView);
                }
                setBackgroundResource(R.drawable.shape_float_window_background);
                windowManager.updateViewLayout(this, layoutParams);
                showCirView = false;
                return true;
            }
        }
        //Log.d(TAG, "不消费事件");
        return false;
    }

    private boolean isClickClose(float x, float y) {
//        return x >= imgClose.getX() && x <= (imgClose.getX() + imgClose.getWidth()) &&
//                y >= imgClose.getY() && y <= (imgClose.getY() + imgClose.getHeight());
        return isClickView(imgClose, x, y);
    }

    private boolean isClickClear(float x, float y) {
        return isClickView(tvClear, x, y);
    }

    private boolean isClickView(View view, float x, float y) {
        return x >= view.getX() && x <= (view.getX() + view.getWidth()) &&
                y >= view.getY() && y <= (view.getY() + view.getHeight());
    }

    public void clear() {
        adapter.clear();
    }

    public void setLogData(HttpLogBean httpLogBean) {
        adapter.appendData(httpLogBean);
    }

    public void appToBackground() {
        setVisibility(View.GONE);
    }

    public void appToForeground() {
        setVisibility(View.VISIBLE);
    }

    private float dp2px(float dp) {
        return getContext().getResources().getDisplayMetrics().density * dp;
    }

    private int dp2px(int dp) {
        return (int)dp2px((float) dp);
    }
}
