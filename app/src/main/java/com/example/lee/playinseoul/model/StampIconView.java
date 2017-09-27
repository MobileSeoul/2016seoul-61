package com.example.lee.playinseoul.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.lee.playinseoul.R;

/**
 * Created by Hong Tae Joon on 2016-10-10.
 */

public class StampIconView extends View {
    int color = Color.parseColor("#7667B2");

    public StampIconView(Context context) {
        super(context);
    }

    public StampIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StampIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = getWidth()/2;
        if(getWidth() > getHeight()) radius = getHeight()/2;
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, paint);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_visited_list);
        int d = bitmap.getWidth()/2;
        Rect src = new Rect(0,0,bitmap.getWidth()-1, bitmap.getHeight()-1);
        Rect dest = new Rect(getWidth()/4,getHeight()/4,getWidth() * 3 / 4, getHeight() * 3 / 4);
        canvas.drawBitmap(bitmap, src, dest, null);
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }
}
