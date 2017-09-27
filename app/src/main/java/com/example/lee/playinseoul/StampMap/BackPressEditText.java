package com.example.lee.playinseoul.StampMap;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lee.playinseoul.R;

import org.w3c.dom.Text;

/**
 * Created by xyom on 2016-09-03.
 */
public class BackPressEditText extends EditText
{
    Context mContext;
    public BackPressEditText( Context context ) {
        super( context );
        mContext=context;
    }

    public BackPressEditText( Context context, AttributeSet attrs ) {
        super( context, attrs );
        mContext=context;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public boolean onKeyPreIme( int keyCode, KeyEvent event ) {
        if( event.getAction() == KeyEvent.ACTION_DOWN ) {
            if( keyCode == KeyEvent.KEYCODE_BACK ) {

                ((Activity) mContext).findViewById(R.id.writeTitle).setVisibility(View.VISIBLE);
                ((Activity) mContext).findViewById(R.id.write_image).setVisibility(View.VISIBLE);
                ((Activity) mContext).findViewById(R.id.contentText).setVisibility(View.VISIBLE);
                ((Activity) mContext).findViewById(R.id.titleText).setVisibility(View.VISIBLE);

            }
        }

        return super.onKeyPreIme( keyCode, event );
    }
}