package com.technowave.decathlon.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;

public class LatinKeyboardView extends KeyboardView {
    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    static final int KEYCODE_OPTIONS = -100;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public boolean onLongPress(Keyboard.Key key) {
        if (key.codes[0] != -3) {
            return super.onLongPress(key);
        }
        getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, (int[]) null);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void setSubtypeOnSpaceKey(InputMethodSubtype subtype) {
        ((LatinKeyboard) getKeyboard()).setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }
}
