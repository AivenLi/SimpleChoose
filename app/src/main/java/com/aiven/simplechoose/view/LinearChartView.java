package com.aiven.simplechoose.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.aiven.simplechoose.R;

import java.util.List;

/**
 * 折线图
 * */
public class LinearChartView extends View {

    private Paint paint;

    /**
     * 坐标轴宽度
     * */
    private float axisWidth;

    /**
     * 坐标轴颜色
     * */
    private int axisColor;

    /**
     * 坐标轴字体大小
     * */
    private float axisTextSize;

    /**
     * 坐标轴字体颜色
     * */
    private int axisTextColor;

    /**
     * 折线宽度
     * */
    private float chartWidth;

    /**
     * 折线颜色
     * */
    private int chartColor;

    /**
     * 折线连接点半径
     * */
    private float chartPointRadius;

    /**
     * 折线连接点颜色
     * */
    private int chartPointColor;

    /**
     * 折线连接点是否画个小圆点，默认有
     * */
    private boolean showChartPoint;

    /**
     * 连接点字体大小（Y轴的值）
     * */
    private float chartPointTextSize;

    /**
     * 连接点字体颜色（Y轴的值）
     * */
    private int chartPointTextColor;

    /**
     * 是否显示连接点的值（Y轴的值）
     * */
    private boolean showChartPointText;

    private float downX = 0f;
    private float moveX = 0f;
    private float prevX = 0f;

    /**
     * X轴和Y轴的单位
     * */
    private String xText;
    private String yText;

    /**
     * Y轴最大值
     * */
    private int axisYMaxValue;

    /**
     * 是否一页显示所有数据，默认为true
     * */
    private boolean showInPage;

    /**
     * 如果不是一页显示所有数据，则需要指定一页显示多少条数据
     * */
    private int pageSize;

    private Rect rect;

    /**
     * 屏幕密度
     * */
    private final float density;

    /**
     * 箭头距离
     * */
    private final float arrowMarginX;

    /**
     * Y轴终点Y坐标
     * */
    private float axisYTop;
    
    /**
     * 原点X坐标
     * */
    private float startX;
    
    /**
     * 原点Y坐标
     * */
    private float startY;
    
    /**
     * X轴终点X坐标
     * */
    private float axisXEnd;

    /**
     * Y轴高度
     * */
    private float sumY;

    /**
     * X轴长度
     * */
    private float sumX;

    private float xOri;
    private float yOri;
    private float xEnd;
    
    /**
     * Y轴刻度值，以该值来计算Y轴的刻度，所以必须指定
     * */
    private List<AxisY> axisYList;

    private List<AxisX> axisXList;

    private VelocityTracker velocityTracker;
    private Scroller scroller;

    public LinearChartView(Context context) {
        this(context, null);
    }

    public LinearChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = context.getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearChartView);
        axisWidth = typedArray.getDimension(R.styleable.LinearChartView_axisWidth, dp2px(1.0f));
        axisColor = typedArray.getColor(R.styleable.LinearChartView_axisColor, Color.BLACK);
        axisTextSize = typedArray.getDimension(R.styleable.LinearChartView_axisTextSize, dp2px(10.0f));
        axisTextColor = typedArray.getColor(R.styleable.LinearChartView_axisTextColor, Color.BLACK);
        axisYMaxValue = typedArray.getInt(R.styleable.LinearChartView_axisYMaxValue, 0);
        xText = typedArray.getString(R.styleable.LinearChartView_axisTextX);
        yText = typedArray.getString(R.styleable.LinearChartView_axisTextY);
        chartWidth = typedArray.getDimension(R.styleable.LinearChartView_chartWidth, dp2px(0.5f));
        chartColor = typedArray.getColor(R.styleable.LinearChartView_chartColor, 0xff999999);
        chartPointRadius = typedArray.getDimension(R.styleable.LinearChartView_chartPointRadius, dp2px(1.0f));
        chartPointColor = typedArray.getColor(R.styleable.LinearChartView_chartPointColor, 0xff999999);
        showChartPoint = typedArray.getBoolean(R.styleable.LinearChartView_showChartPoint, true);
        chartPointTextSize = typedArray.getDimension(R.styleable.LinearChartView_charPointTextSize, dp2px(10.0f));
        chartPointTextColor = typedArray.getColor(R.styleable.LinearChartView_chartPointTextColor, 0xff999999);
        showChartPoint = typedArray.getBoolean(R.styleable.LinearChartView_showChartPoint, true);
        showChartPointText = typedArray.getBoolean(R.styleable.LinearChartView_showChartPointText, true);
        showInPage = typedArray.getBoolean(R.styleable.LinearChartView_showInPage, true);
        pageSize = typedArray.getInt(R.styleable.LinearChartView_pageSize, 10);
        typedArray.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
        rect = new Rect();
        scroller = new Scroller(context);
        arrowMarginX = dp2px(axisWidth * 1.4f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAxis(canvas);
        drawValue(canvas);
    }

    private void drawAxis(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        paint.setTextSize(axisTextSize);
        paint.setStyle(Paint.Style.FILL);
        paint.getTextBounds(yText, 0, yText.length(), rect);
        paint.setColor(axisColor);
        paint.setStrokeWidth(axisWidth);
        axisYTop = rect.height() + dp2px(10);
        paint.getTextBounds(xText, 0, xText.length(), rect);
        startY = height - rect.height() - dp2px(10f);
        float xTextY = startY + rect.height() / 2f;
        axisXEnd = rect.width() + dp2px(10);
        startX = getAxisYMaxTitleRect();
        /**
         * Y轴
         * */
        xOri = startX + arrowMarginX;
        yOri = startY;
        canvas.drawLine(startX + arrowMarginX, startY, startX + arrowMarginX, axisYTop, paint);
        /**
         * Y轴箭头，2分之根号2约等于0.707
         * */
        canvas.drawLine(startX, arrowMarginX + axisYTop, startX + arrowMarginX + ((axisWidth / 2f) * 0.707f), axisYTop, paint);
        canvas.drawLine(startX + arrowMarginX - ((axisWidth / 2f) * 0.707f), axisYTop, startX + arrowMarginX * 2, axisYTop + arrowMarginX, paint);
        /**
         * Y轴单位
         * */
        paint.setColor(axisTextColor);
        canvas.drawText(yText, startX + arrowMarginX, rect.height() + dp2px(5), paint);
        /**
         * X轴
         * */
        xEnd = width - axisXEnd - dp2px(4f);
        paint.setColor(axisColor);
        canvas.drawLine(startX + arrowMarginX - axisWidth / 2f, startY, width - axisXEnd, startY, paint);
        canvas.drawLine(width - axisXEnd - arrowMarginX, startY - arrowMarginX, width - axisXEnd, startY + ((axisWidth / 2f) * 0.707f), paint);
        canvas.drawLine(width - axisXEnd - arrowMarginX, startY + arrowMarginX, width - axisXEnd, startY - ((axisWidth / 2f) * 0.707f), paint);
        paint.setTextSize(axisTextSize);
        paint.setColor(axisTextColor);
        canvas.drawText(xText, width - axisXEnd + dp2px(4), xTextY, paint);
        /**
         * 原点值
         * */
        paint.getTextBounds("0", 0, 1, rect);
        canvas.drawText("0", startX - rect.width(), startY + axisYList.get(0).rectHeight, paint);
        /**
         * 画Y轴刻度
         * */
        sumY = initSumY();
        sumX = width - initSumX();
        paint.setColor(axisColor);
        for (AxisY axisY: axisYList) {
            float y = axisY.value / axisYMaxValue * sumY;
            paint.setColor(axisColor);
            canvas.drawLine(startX + arrowMarginX, startY - y, startX + arrowMarginX * 2f, startY - y, paint);
            paint.setColor(axisTextColor);
            canvas.drawText(axisY.title, startX - axisY.rectWidth, startY - y + axisY.rectHeight / 2f, paint);
        }
    }

    private float maxLeftScrollerDis;
    private void drawValue(Canvas canvas) {
        int layoutId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.clipRect(xOri, 0, xEnd, getHeight());
        float step = sumX / (pageSize + 1);
        maxLeftScrollerDis = (axisXList.size() + 1) * step - sumX;
        paint.setTextSize(axisTextSize);
        for (int i = 0; i < axisXList.size(); ++i) {
            AxisX axisX = axisXList.get(i);
            float y = startY - axisX.yValue / axisYMaxValue * sumY;
            float x = moveX + startX + ((i + 1) * step);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(chartWidth);
            paint.setColor(chartColor);
            if (i + 1 < axisXList.size()) {
                AxisX axisX1 = axisXList.get(i + 1);
                canvas.drawLine(
                        x,
                        y,
                        moveX + startX + (i + 2) * step,
                        startY - axisX1.yValue / axisYMaxValue * sumY,
                        paint
                );
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(1);
            paint.setTextSize(axisTextSize);
            paint.getTextBounds(axisX.title, 0, axisX.title.length(), rect);
            if (x >= xOri - rect.width() / 2f && x <= xEnd + rect.width() / 2f) {
                paint.setColor(axisTextColor);
                canvas.drawText(axisX.title, x - rect.width() / 2f, startY + rect.height() + dp2px(2f), paint);
                if (showChartPointText) {
                    paint.setTextSize(chartPointTextSize);
                    paint.setColor(chartPointTextColor);
                    @SuppressLint("DefaultLocale")
                    String s = String.format("%.2f", axisX.yValue);
                    paint.getTextBounds(s, 0, s.length(), rect);
                    canvas.drawText(s, x - rect.width() / 2f, y - rect.height(), paint);
                }
                paint.setColor(axisColor);
                paint.setStrokeWidth(axisWidth);
                canvas.drawLine(x, startY, x, startY - arrowMarginX, paint);
                paint.setColor(chartPointColor);
                canvas.drawCircle(x, startY - axisX.yValue / axisYMaxValue * sumY, chartPointRadius, paint);
            }
        }

        canvas.restoreToCount(layoutId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        //velocityTracker = VelocityTracker.obtain();
      //  MotionEvent motionEvent = MotionEvent.obtain(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = prevX + event.getX() - downX;
                if (moveX > 0) {
                    moveX = 0;
                } else if (-moveX > maxLeftScrollerDis) {
                    moveX = -maxLeftScrollerDis;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            //    velocityTracker.addMovement(motionEvent);
                prevX = moveX;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
       // velocityTracker.recycle();
        return true;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                scrollTo(scroller.getCurrX(), 0);
                postOnAnimationFun();
            }
        }
    };

    private void postOnAnimationFun() {
        postOnAnimation(runnable);
    }

    private void startFling(int v) {
        scroller.fling(getScrollX(), 0, v, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        postOnAnimationFun();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (x > 0) {
            x = 0;
        } else if (-x > maxLeftScrollerDis) {
            x = -((int)maxLeftScrollerDis);
        }
        super.scrollTo(x, y);
    }

    private float initSumY() {
        return startY - axisYTop - arrowMarginX - dp2px(4.0f);
    }

    private float initSumX() {
        return startX - axisXEnd - dp2px(4.0f);
    }

    private int getAxisYMaxTitleRect() {
        paint.setTextSize(axisTextSize);
        int max = 0;
        for (AxisY axisY: axisYList) {
            paint.getTextBounds(axisY.title, 0, axisY.title.length(), rect);
            axisY.rectWidth = rect.width();
            axisY.rectHeight = rect.height();
            if (max < rect.width()) {
                max = rect.width();
            }
        }
        return max;
    }

    public synchronized void setAxisYList(List<AxisY> axisYList) {
        this.axisYList = axisYList;
        postInvalidate();
    }

    public synchronized void setAxisXList(List<AxisX> axisXList) {
        this.axisXList = axisXList;
        postInvalidate();
    }

    public synchronized void setAxisYMaxValue(int maxValue) {
        axisYMaxValue = maxValue;
        postInvalidate();
    }

    private float dp2px(float dp) {
        return density * dp;
    }

    private int dp2px(int dp) {
        return Math.round(density * dp);
    }

    public static class AxisY {
        float value;
        String title;
        int rectWidth;
        int rectHeight;

        public AxisY() {}
        public AxisY(float value, String title) {
            this.value = value;
            this.title = title;
        }
    }

    public static class AxisX {
        float xValue;
        float yValue;
        String title;
        int rectWidth;
        int rectHeight;
        public AxisX() {}
        public AxisX(float xValue, float yValue, String title) {
            this.xValue = xValue;
            this.yValue = yValue;
            this.title  = title;
        }
    }
}
