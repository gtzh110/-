package com.yuzhi.fine.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.yuzhi.fine.utils.TextContentUtils;


/**
 * Created by lemon on 2016/3/29.
 */
public class IngageEditText extends EditText {
    public IngageEditText(Context context) {
        super(context);
    }

    public IngageEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextContentUtils.setTextWatcherWithoutEmoji(this);
        (this).setTextColor(Color.parseColor("#FF333333"));
        setHighlightColor(Color.parseColor("#B4DF87"));

    }

    public IngageEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTypeface(Typeface tf) {
        /*if (BaseApplication.getTextType() != null){
            tf = BaseApplication.getTextType();
        }*/
        super.setTypeface(tf);
    }
}
