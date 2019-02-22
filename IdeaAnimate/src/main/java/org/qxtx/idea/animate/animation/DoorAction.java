package org.qxtx.idea.animate.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.ref.WeakReference;

/**
 * @CreateDate 2019/02/14 10:12.
 * @Author QXTX-GOSPELL
 *
 * Animation type of {@link IdeaAnimation}.
 *
 * @see IdeaAnimationManager
 */
public final class DoorAction extends Animation {
    private Camera camera;
    private WeakReference<View> v;
    private float pivotX = 0f, pivotY = 0f;
    private float toValue;
    private boolean isHor;
    private boolean isOpen;

    DoorAction(View v, float pivotX, float pivotY, float toValue, boolean isHor, boolean isOpen) {
        this.v = new WeakReference<View>(v);
        this.isHor = isHor;
        this.pivotX = pivotX / 2f;
        this.pivotY = pivotY / 2f;
        this.isOpen = isOpen;
        camera = new Camera();
        this.toValue = toValue;
    }

    public DoorAction fillAfter(boolean fillAfter) {
        setFillAfter(fillAfter);
        return this;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        camera.save();
        Matrix matrix = t.getMatrix();
        float value = (isOpen ? interpolatedTime : interpolatedTime - 1f) * toValue;
        if (isHor) {
            camera.rotateY(value);
        } else {
            camera.rotateX(value);
        }
        camera.getMatrix(matrix);
        camera.restore();

        t.getMatrix().preRotate(toValue, pivotX, pivotY);
        t.getMatrix().postRotate(toValue, pivotX, pivotY);
    }

    public DoorAction duration(long duration) {
        setDuration(duration);
        return this;
    }

    public void start(long delay) {
        if (v == null || v.get() == null) {
            return ;
        }

        v.get().postDelayed(() -> {
            v.get().startAnimation(this);
        }, delay);
    }

    @Override
    public void cancel() {
        super.cancel();
        camera = null;
    }
}
