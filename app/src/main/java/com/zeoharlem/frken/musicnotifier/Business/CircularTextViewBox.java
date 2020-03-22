package com.zeoharlem.frken.musicnotifier.Business;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CircularTextViewBox extends androidx.appcompat.widget.AppCompatTextView {
    public CircularTextViewBox(Context context) {
        super(context);
    }

    public CircularTextViewBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularTextViewBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int r   = Math.max(getMeasuredWidth(),getMeasuredHeight());
        setMeasuredDimension(r, r);
    }
}
