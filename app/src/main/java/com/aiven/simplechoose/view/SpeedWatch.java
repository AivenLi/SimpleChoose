package com.aiven.simplechoose.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aiven.simplechoose.R;


public class SpeedWatch extends View {

    private static final float PI = 3.1415926f;
    private float density;
    private int startColor;
    private int endColor;
    private int indicatorColor;
    private int maxValue;
    private float watchWidth;
    private int watchColor;
    private int curValue = 0;
    private String title;
    private float titleSize;
    private int titleColor;
    private float valueSize;
    private int valueColor;
    private Paint paint;
    private Paint textPaint;
    private SweepGradient sweepGradient;
    private Handler handler;
    private long currentDelay = 0L;
    private static final long BASE_DELAY = 10L;
    public SpeedWatch(Context context) {
        this(context, null);
    }

    public SpeedWatch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedWatch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = context.getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpeedWatch);
        startColor = typedArray.getColor(R.styleable.SpeedWatch_android_startColor, 0xffffffff);
        endColor = typedArray.getColor(R.styleable.SpeedWatch_android_endColor, 0xffff0000);
        indicatorColor = typedArray.getColor(R.styleable.SpeedWatch_indicatorColor, 0xffd81e06);
        title = typedArray.getString(R.styleable.SpeedWatch_titleText);
        titleColor = typedArray.getColor(R.styleable.SpeedWatch_titleTextColor, 0xffffffff);
        titleSize = typedArray.getDimension(R.styleable.SpeedWatch_titleTextSize, dp2px(16));
        valueSize = typedArray.getDimension(R.styleable.SpeedWatch_valueTextSize, dp2px(12f));
        valueColor = typedArray.getColor(R.styleable.SpeedWatch_valueTextColor, 0xffffffff);
        maxValue = typedArray.getInt(R.styleable.SpeedWatch_maxValue, 300);
        watchWidth = typedArray.getDimension(R.styleable.SpeedWatch_watchWidth, dp2px(4f));
        watchColor = typedArray.getColor(R.styleable.SpeedWatch_watchColor, 0xffffffff);
        typedArray.recycle();
        paint = new Paint();
        textPaint = new Paint();
        paint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {

                if (msg.what == 1024) {
                    curValue = (int)msg.obj;
                    invalidate();
                }
            }
        };
    }

    StringBuilder stringBuilder = new StringBuilder();
    private Rect rect = new Rect();
    private Path path = new Path();
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        float radius;
        float xOri = width / 2f;
        float yOri = height / 2f;
        if (width > height) {
            radius = height / 2f;
        } else {
            radius = width / 2f;
        }
        radius -= watchWidth;
        canvas.rotate(120f, xOri, yOri);
        paint.setStrokeWidth(watchWidth);
        paint.setStyle(Paint.Style.STROKE);
        if (sweepGradient == null) {
            sweepGradient = new SweepGradient(xOri, yOri, startColor, endColor);
        }
        paint.setShader(sweepGradient);
        canvas.drawArc(xOri - radius, yOri - radius, xOri + radius, yOri + radius, 0, 300, false, paint);
        float step = 300f / (maxValue / 10f);
        float lineLen = dp2px(8f);
        paint.setColor(valueColor);
        paint.setStrokeWidth(dp2px(2f));
        paint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(valueSize);
        textPaint.setStrokeWidth(1);
        textPaint.setStyle(Paint.Style.FILL);
        int value = 0;
        float textAngle = -120;
        path.reset();
        float indicatorAngle = ((curValue / (float)maxValue * 300f) + 120f) * -1f;
        path.moveTo(xOri + radius * cos(indicatorAngle), yOri - radius * sin(indicatorAngle));
        for (float angle = -0.5f; angle > -299.5f; angle -= step, value += 10, textAngle -= step) {
            float sin = sin(angle);
            float cos = cos(angle);
            float sx = radius * cos;
            float sy = radius * sin;
            float ex = (radius - lineLen) * cos;
            float ey = (radius - lineLen) * sin;
            /**
             * 转换坐标系
             * */
            sx = xOri + sx;
            sy = yOri - sy;
            ex = xOri + ex;
            ey = yOri - ey;
            canvas.drawLine(sx, sy, ex, ey, paint);
            canvas.save();
            canvas.rotate(-120f, xOri, yOri);
            ex = xOri + (radius - lineLen - lineLen) * cos(textAngle);
            ey = yOri - (radius - lineLen - lineLen) * sin(textAngle);
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append(value);
            String s = stringBuilder.toString();
            textPaint.getTextBounds(s, 0, s.length(), rect);
            ex -= rect.width() / 2f;
            ey += rect.height() / 2f;
            canvas.drawText(stringBuilder.toString(), ex, ey, textPaint);
            canvas.restore();
        }
        float sin = sin(-299.5f);
        float cos = cos(-299.5f);
        float sx = radius * cos;
        float sy = radius * sin;
        float ex = (radius - lineLen) * cos;
        float ey = (radius - lineLen) * sin;
        /**
         * 转换坐标系
         * */
        sx = xOri + sx;
        sy = yOri - sy;
        ex = xOri + ex;
        ey = yOri - ey;
        canvas.drawLine(sx, sy, ex, ey, paint);
        canvas.save();
        canvas.rotate(-120f, xOri, yOri);
        ex = xOri + (radius - lineLen - lineLen) * cos(textAngle);
        ey = yOri - (radius - lineLen - lineLen) * sin(textAngle);
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(value);
        String s = stringBuilder.toString();
        textPaint.getTextBounds(s, 0, s.length(), rect);
        ex -= rect.width() / 2f;
        ey += rect.height() / 2f;
        canvas.drawText(stringBuilder.toString(), ex, ey, textPaint);
        canvas.restore();
        canvas.restore();
        paint.setColor(indicatorColor);
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float r = dp2px(10f);
        path.lineTo(xOri + dp2px(2f) * cos(indicatorAngle - 90), yOri - dp2px(2f) * sin(indicatorAngle - 90));
        path.lineTo(xOri + dp2px(2f) * cos(indicatorAngle + 90), yOri - dp2px(2f) * sin(indicatorAngle + 90));
        path.close();
        canvas.drawPath(path, paint);
        canvas.drawCircle(xOri, yOri, r, paint);
        textPaint.setTextSize(titleSize);
        textPaint.setColor(titleColor);
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(curValue);
        if (title != null && !"".equals(title)) {
            stringBuilder.append(title);
        }
        s = stringBuilder.toString();
        textPaint.getTextBounds(s, 0, s.length(), rect);
        canvas.drawText(s, xOri - rect.width() / 2f, yOri + r + rect.height() * 2f, textPaint);
    }


    public synchronized void setCurValue(int value) {
        if (handler != null) {
            if (value > maxValue) {
                value = maxValue;
            } else {
                value = Math.max(value, 0);
            }
            if (value == curValue) {
                return;
            }
            int min = Math.min(curValue, value);
            int max = curValue + value - min;
            for (; min <= max; ++min, currentDelay += BASE_DELAY) {
                Log.d("码表", "更新码表：" + min);
                handler.sendMessageDelayed(handler.obtainMessage(1024, min), currentDelay);
            }
        }
    }

    private float sin(double angle) {
        return (float)(Math.sin(angleToArc(angle)));
    }

    private float cos(double angle) {
        return (float) Math.cos(angleToArc(angle));
    }

    private double angleToArc(double angle) {
        return angle * PI / 180.0;
    }

    private float dp2px(float dp) {
        return density * dp;
    }
}
