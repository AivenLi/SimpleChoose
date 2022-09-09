package com.example.simplechoose.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView

class AdapterImageView(
    @NonNull context: Context,
    @Nullable attrs: AttributeSet?,
    defStyle: Int
) : androidx.appcompat.widget.AppCompatImageView(
    context,
    attrs,
    defStyle
) {

    constructor(@NonNull context: Context) : this(context, null)

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (drawable != null) {
            val width: Float = MeasureSpec.getSize(widthMeasureSpec).toFloat()
            val height = Math.ceil((width * drawable.intrinsicHeight / drawable.intrinsicWidth).toDouble())
            setMeasuredDimension(width.toInt(), height.toInt())
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}