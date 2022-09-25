package com.aiven.updateapp.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aiven.updateapp.R;

/**
 * @author  : AivenLi
 * @date    : 2022/7/23 15:02
 * @desc    : 下载按钮
 * */
public class ProgressButton extends View {

    private Paint paint;
    /**
     * 进度条颜色，默认天蓝色 #00bfff
     * */
    private int progressColor;
    /**
     * 下载失败是进度颜色，默认灰色 #a0a0a0
     * */
    private int failedProgressColor;
    /**
     * 文字颜色
     * */
    private int normalTextColor;
    /**
     * 下载失败文字颜色
     * */
    private int failedTextColor;
    /**
     * 文字大小
     * */
    private int textSize;
    /**
     * 圆角半径
     * */
    private float radius;
    /**
     * 最大值、默认100
     * */
    private int maxValue;
    /**
     * 当前进度值，默认0
     * */
    private float currentValue;
    /**
     * 是否显示下载百分比
     * */
    private boolean showPercent;

    private int backgroundColor;

    private int borderColor;

    private float borderWidth;

    private boolean showBorder;

    private Path path;

    private Rect textRect;

    private String failedStr;

    private String startStr;

    private String pauseStr;

    private String doneStr;

    private int status = STATUS_START;

    public ProgressButton(Context context) {
        this(context, null);
    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        progressColor = typedArray.getColor(R.styleable.ProgressButton_progressColor, ContextCompat.getColor(context, R.color.default_progress_color));
        failedProgressColor = typedArray.getColor(R.styleable.ProgressButton_failedProgressColor, ContextCompat.getColor(context, R.color.default_failed_progress_color));
        normalTextColor = typedArray.getColor(R.styleable.ProgressButton_normalTextColor, ContextCompat.getColor(context, R.color.default_normal_text_color));
        failedTextColor = typedArray.getColor(R.styleable.ProgressButton_failedTextColor, ContextCompat.getColor(context, R.color.default_failed_text_color));
        textSize = typedArray.getDimensionPixelSize(R.styleable.ProgressButton_android_textSize, (int)(displayMetrics.density * 12));
        maxValue = typedArray.getInteger(R.styleable.ProgressButton_maxValue, 100);
        currentValue = typedArray.getInteger(R.styleable.ProgressButton_value, 0);
        showPercent = typedArray.getBoolean(R.styleable.ProgressButton_showPercent, true);
        radius = typedArray.getDimension(R.styleable.ProgressButton_android_radius, 0f);
        failedStr = typedArray.getString(R.styleable.ProgressButton_failedText);
        doneStr = typedArray.getString(R.styleable.ProgressButton_doneText);
        backgroundColor = typedArray.getColor(R.styleable.ProgressButton_backgroundColor, ContextCompat.getColor(context, R.color.white));
        borderColor = typedArray.getColor(R.styleable.ProgressButton_borderColor, progressColor);
        borderWidth = typedArray.getDimension(R.styleable.ProgressButton_borderWidth, displayMetrics.density);
        showBorder = typedArray.getBoolean(R.styleable.ProgressButton_showBorder, true);
        startStr = typedArray.getString(R.styleable.ProgressButton_startText);
        pauseStr = typedArray.getString(R.styleable.ProgressButton_pauseText);
        typedArray.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
        path = new Path();
        textRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        if (radius < 0.00f) {
            radius = 0.00f;
        }
        if (radius > width / 2f) {
            radius = width / 2f;
        }
        if (radius > height / 2f) {
            radius = height / 2f;
        }
        path.moveTo(radius, 0);
        path.lineTo(width - radius, 0);
        path.quadTo(width, 0, width, radius);
        path.lineTo(width, height - radius);
        path.quadTo(width, height, width - radius, height);
        path.lineTo(radius, height);
        path.quadTo(0, height, 0, height - radius);
        path.lineTo(0, radius);
        path.quadTo(0, 0, radius, 0);
        canvas.clipPath(path);
        paint.setColor(backgroundColor);
        canvas.drawRect(0, 0, width, height, paint);
        if (showBorder) {
            paint.setColor(borderColor);
            Paint.Style style = paint.getStyle();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);
            canvas.drawPath(path, paint);
            paint.setStyle(style);
        }
        float percent = currentValue / maxValue;
        float progressWidth = percent * width;
        if (progressWidth > 0.00f) {
            drawProgress(canvas, progressWidth, height, status == STATUS_FAILED ? failedProgressColor : progressColor);
        }
        if (status == STATUS_START) {
            drawText(canvas, startStr, normalTextColor, width, height);
        } else if (status == STATUS_DOWNLOADING) {
            if (showPercent) {
                drawPercentText(canvas, currentValue, width, height);
            }
        } else if (status == STATUS_PAUSE) {
            drawText(canvas, pauseStr, normalTextColor, width, height);
        } else if (status == STATUS_FAILED) {
            drawFailedText(canvas, width, height);
        } else if (status == STATUS_DONE) {
            drawText(canvas, doneStr, normalTextColor, width, height);
        }
    }

    /**
     * 绘制百分比文字
     * @param canvas
     * @param percent
     * @param width
     * @param height
     * */
    private void drawPercentText(Canvas canvas, float percent, int width, int height) {
        @SuppressLint("DefaultLocale")
        String percentStr = String.format("%.2f%%", percent);
        drawText(canvas, percentStr, normalTextColor, width, height);
    }

    /**
     * 绘制失败文字
     * @param canvas
     * @param width
     * @param height
     * */
    private void drawFailedText(Canvas canvas, int width, int height) {
        drawText(canvas, failedStr, failedTextColor, width, height);
    }

    /**
     * 绘制文字
     * @param canvas
     * @param text
     * @param textColor
     * @param width
     * @param height
     * */
    private void drawText(Canvas canvas, String text, int textColor, int width, int height) {
        if (text == null || "".equals(text)) {
            return;
        }
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.getTextBounds(text, 0, text.length(), textRect);
        canvas.drawText(text, (width - textRect.width()) / 2f, (height + textRect.height()) / 2f, paint);
    }

    /**
     * 绘制进度条
     * @param canvas 画板
     * @param progressWidth 进度条宽度
     * @param height 控件高度
     * @param color 颜色
     * */
    private void drawProgress(Canvas canvas, float progressWidth, int height, int color) {
        paint.setColor(color);
        canvas.drawRect(0, 0, progressWidth, height, paint);
    }

    public synchronized void setValue(float value) {
        if (value < 0.00f) {
            throw new IllegalArgumentException("The value can't be less 0");
        }
        if (value > maxValue) {
            value = maxValue;
        }
        this.currentValue = value;
        postInvalidate();
    }

    public synchronized float getValue() {
        return currentValue;
    }

    public synchronized void setFailedText(String reason) {
        failedStr = reason;
        postInvalidate();
    }

    public synchronized void setFailedTextNoInvalidate(String reason) {
        failedStr = reason;
    }

    /**
     * 未开始，点击开始
     * */
    public static int STATUS_START = 0;

    /**
     * 下载中，点击暂停
     * */
    public static int STATUS_DOWNLOADING = 1;

    /**
     * 暂停中，点击继续
     * */
    public static int STATUS_PAUSE = 2;

    /**
     * 失败，点击重试
     * */
    public static int STATUS_FAILED = 3;

    public static int STATUS_DONE = 4;

    /**
     * 设置状态
     * @param status
     * */
    public synchronized void setStatus(int status) {
        this.status = status;
        postInvalidate();
    }

    /**
     * 获取状态
     * */
    public synchronized int getStatus() {
        return status;
    }
}