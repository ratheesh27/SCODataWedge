package com.technowave.decathlon.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;



public class LatinKeyboard extends Keyboard {
    private Key mEnterKey;
    private Key mLanguageSwitchKey;
    private Key mModeChangeKey;
    private Key mSavedLanguageSwitchKey;
    private Key mSavedModeChangeKey;
    private Key mSpaceKey;

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public LatinKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    /* access modifiers changed from: protected */
    public Key createKeyFromXml(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
        if (key.codes[0] == 10) {
            this.mEnterKey = key;
        } else if (key.codes[0] == 32) {
            this.mSpaceKey = key;
        } else if (key.codes[0] == -2) {
            this.mModeChangeKey = key;
            this.mSavedModeChangeKey = new LatinKey(res, parent, x, y, parser);
        } else if (key.codes[0] == -101) {
            this.mLanguageSwitchKey = key;
            this.mSavedLanguageSwitchKey = new LatinKey(res, parent, x, y, parser);
        }
        return key;
    }

    /* access modifiers changed from: package-private */
    public void setLanguageSwitchKeyVisibility(boolean visible) {
        if (visible) {
            this.mModeChangeKey.width = this.mSavedModeChangeKey.width;
            this.mModeChangeKey.x = this.mSavedModeChangeKey.x;
            this.mLanguageSwitchKey.width = this.mSavedLanguageSwitchKey.width;
            this.mLanguageSwitchKey.icon = this.mSavedLanguageSwitchKey.icon;
            this.mLanguageSwitchKey.iconPreview = this.mSavedLanguageSwitchKey.iconPreview;
            return;
        }
        this.mModeChangeKey.width = this.mSavedModeChangeKey.width + this.mSavedLanguageSwitchKey.width;
        this.mLanguageSwitchKey.width = 0;
        this.mLanguageSwitchKey.icon = null;
        this.mLanguageSwitchKey.iconPreview = null;
    }

    /* access modifiers changed from: package-private */
    public void setImeOptions(Resources res, int options) {
        if (this.mEnterKey != null) {

        }
    }

    /* access modifiers changed from: package-private */
    public void setSpaceIcon(Drawable icon) {
        if (this.mSpaceKey != null) {
            this.mSpaceKey.icon = icon;
        }
    }

    static class LatinKey extends Key {
        public LatinKey(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

        public boolean isInside(int x, int y) {
            if (this.codes[0] == -3) {
                y -= 10;
            }
            return super.isInside(x, y);
        }
    }
}
