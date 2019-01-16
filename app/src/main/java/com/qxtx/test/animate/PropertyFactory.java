package com.qxtx.test.animate;

/**
 * @CreateDate 2019/01/16 14:02.
 * @Author QXTX-GOSPELL
 */

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

/**
 * A static class that have two special methods name getCustom() and setCustom(T).
 *  It was useless unless you override it. You must override the method that getCustom() and setCustom(T).
 *   or be created in subclass if not such method in the parent
 **/
public class PropertyFactory<T> {
    public static final String PROPERTY_CUSTOM = "custom";
    private final Object target;

    public PropertyFactory() {
        target = null;
    }

    public PropertyFactory(Object target) {
        this.target = target;
    }

    public void setWidth(int width) {
        if (target instanceof View) {
            ((View) target).getLayoutParams().width = width;
            ((View) target).requestLayout();
        }
    }

    public void setHeight(int height) {
        if (target instanceof View) {
            ((View) target).getLayoutParams().height = height;
            ((View) target).requestLayout();
        }
    }

    public void setTextColor(String value) {
        if (target instanceof TextView) {
            ((TextView)target).setTextColor(Color.parseColor(value));
        }
    }

    public void setBackgroundColor(String value) {
        if (target instanceof View) {
            ((View)target).setBackgroundColor(Color.parseColor(value));
        }
    }

    public void setCustom(T value) {}

    public void setCustom(boolean value) {}

    public void setCustom(short value) {}

    public void setCustom(byte value) {}

    public void setCustom(int value) {}

    public void setCustom(float value) {}

    public void setCustom(double value) {}

    public void setCustom(long value) {}
}
