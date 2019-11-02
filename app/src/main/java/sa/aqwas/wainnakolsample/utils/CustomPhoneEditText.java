package com.vhorus.saloni.barberapp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import com.vhorus.saloni.barberapp.R;
import com.lamudi.phonefield.PhoneEditText;

/**
 * Created by Joey on 1/31/2018.
 */
public class CustomPhoneEditText extends PhoneEditText {

    public CustomPhoneEditText(Context context) {
        this(context, null);
    }

    public CustomPhoneEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPhoneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void updateLayoutAttributes() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
        setPadding(0, getContext().getResources().getDimensionPixelSize(R.dimen.padding_large), 0, 0);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.custom_phone_edittext;
    }
}
