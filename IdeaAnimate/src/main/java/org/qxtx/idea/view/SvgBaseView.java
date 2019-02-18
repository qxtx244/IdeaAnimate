package org.qxtx.idea.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

/**
 * @CreateDate 2019/02/18 11:22.
 * @Author QXTX-GOSPELL
 */

public class SvgBaseView extends View {
    public SvgBaseView(Context context) {
        super(context);
    }

    public SvgBaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SvgBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SvgBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
