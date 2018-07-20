package com.uname.your_application.global.customViews;


import android.content.Context;
import android.util.AttributeSet;

public class OverriddenEditText extends android.support.v7.widget.AppCompatEditText{

    public OverriddenEditText(Context context) {
        super(context);
    }

    public OverriddenEditText( Context context, AttributeSet attribute_set )
    {
        super( context, attribute_set );
    }

    public OverriddenEditText(Context context, AttributeSet attribute_set, int def_style_attribute )
    {
        super( context, attribute_set, def_style_attribute );
    }

    @Override
    public boolean performClick() {
        // do nothing
        return true;
    }
}
