package com.wwq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 魏文强 on 2016/4/17.
 */
public class FocusedTextView extends TextView {
    //用代码直接new对象时，走此方法
    public FocusedTextView(Context context) {
        super(context);
    }
    //有属性时走此方法
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //有style样式的话会走此方法
    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
