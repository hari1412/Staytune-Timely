package com.buenatech.staytune.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;


public class CustomTabLayout extends SmartTabLayout {

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected TextView createDefaultTabView(CharSequence title) {
        TextView textView = super.createDefaultTabView(title);
        textView.setTypeface(Typeface.DEFAULT);

            textView.setPadding(36, 24, 36, 24);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            textView.setLayoutParams(params);

        return textView;
    }

}
