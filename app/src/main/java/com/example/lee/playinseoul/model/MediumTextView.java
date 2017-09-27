package com.example.lee.playinseoul.model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Hong Tae Joon on 2016-10-02.
 */

public class MediumTextView extends TextView {
    private static Typeface typeface;
    public MediumTextView(Context context) {
        super(context);
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(typeface==null) typeface = Typeface.createFromAsset(context.getAssets(), "fonts/medium.ttf"); // 외부폰트 사용;
        setTypeface(typeface);
        setIncludeFontPadding(false);
    }

    public MediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
