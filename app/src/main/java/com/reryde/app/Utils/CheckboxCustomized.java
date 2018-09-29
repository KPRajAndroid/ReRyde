package com.reryde.app.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.reryde.app.R;

/**
 * Created by Tranxit Technologies Pvt Ltd. on 17-03-2018.
 */

public class CheckboxCustomized extends CheckBox {
    public CheckboxCustomized(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CheckboxCustomized(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CheckboxCustomized(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckboxCustomized(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/ClanPro-NarrNews.otf", context);
        setTypeface(customFont);
    }
    @Override
    public void setChecked(boolean t){
        if(t)
        {
            this.setBackgroundResource(R.drawable.ic_checked);
        }
        else
        {
            this.setBackgroundResource(R.drawable.ic_unchecked);
        }
        super.setChecked(t);
    }
}
