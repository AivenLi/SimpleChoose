package com.example.simplechoose.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.simplechoose.R;


/**
 * @author : AivenLi
 * @date : 2022/7/30 16:43
 */
public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {

    private float radius;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;
    private Path path;

    public RoundImageView(@NonNull Context context) {
        this(context, null);
    }

    public RoundImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        radius = typedArray.getDimension(R.styleable.RoundImageView_android_radius, 0.00f);
        if (radius == 0.00f) {
            topLeftRadius = typedArray.getDimension(R.styleable.RoundImageView_android_topLeftRadius, 0.00f);
            topRightRadius = typedArray.getDimension(R.styleable.RoundImageView_android_topRightRadius, 0.00f);
            bottomLeftRadius = typedArray.getDimension(R.styleable.RoundImageView_android_bottomLeftRadius, 0.00f);
            bottomRightRadius = typedArray.getDimension(R.styleable.RoundImageView_android_bottomRightRadius, 0.00f);
        }
        if (radius != 0.00f || topLeftRadius != 0.00f || topRightRadius != 0.00f || bottomLeftRadius != 0.00f || bottomRightRadius != 0.00f) {
            path = new Path();
        } else {
            path = null;
        }
        typedArray.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (path != null) {
            int width = getWidth();
            int height = getHeight();
            if (radius != 0.00f) {
                path.moveTo(radius, 0);
                path.lineTo(width - radius, 0);
                path.quadTo(width, 0, width, radius);
                path.lineTo(width, height - radius);
                path.quadTo(width, height, width - radius, height);
                path.lineTo(radius, height);
                path.quadTo(0, height, 0, height - radius);
                path.lineTo(0, radius);
                path.quadTo(0, 0, radius, 0);
            } else {
                path.moveTo(topLeftRadius, 0);
                path.lineTo(width - topRightRadius, 0);
                path.quadTo(width, 0, width, topRightRadius);
                path.lineTo(width, height - bottomRightRadius);
                path.quadTo(width, height, width - bottomRightRadius, height);
                path.lineTo(bottomLeftRadius, height);
                path.quadTo(0, height, 0, height - bottomLeftRadius);
                path.lineTo(0, topLeftRadius);
                path.quadTo(0, 0, topLeftRadius, 0);
            }
            canvas.clipPath(path);
            canvas.save();
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }
}