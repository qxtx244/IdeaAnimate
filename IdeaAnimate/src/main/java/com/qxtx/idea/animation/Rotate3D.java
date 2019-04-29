package com.qxtx.idea.animation;

import android.graphics.Camera;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.ref.WeakReference;

/**
 * CreatedDate   2019/02/14 10:10.
 * Author  QXTX-GOSPELL
 *
 * A class type for {@link IdeaAnimationManager}.
 *
 * @see IdeaAnimationManager
 */
public final class Rotate3D extends Animation {
    public static final String TYPE_TRANSLATE = "translation";
    public static final String TYPE_ROTATE_X = "rotateX";
    public static final String TYPE_ROTATE_Y = "rotateY";
    public static final String TYPE_ROTATE_Z = "rotateZ";
    private Camera camera;
    private final String type;
    private float[] fromValues;
    private float[] toValues;
    private WeakReference<View> view;
    private float centerX, centerY;

    Rotate3D(@NonNull View view, String type, float[] fromValues, @NonNull float[] toValues) {
        this.toValues = new float[] {0f, 0f, 0f};
        this.fromValues = new float[] {0f, 0f, 0f};
        this.type = type;
        this.view = new WeakReference<View>(view);
        System.arraycopy(fromValues, 0, this.fromValues, 0, fromValues.length);
        System.arraycopy(toValues, 0, this.toValues, 0, toValues.length);
        camera =  new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float deltaX = fromValues[0] + (toValues[0] - fromValues[0]) * interpolatedTime;
        float deltaY = fromValues[1] + (toValues[1] - fromValues[1]) * interpolatedTime;
        float deltaZ = fromValues[2] + (toValues[2] - fromValues[2]) * interpolatedTime;

        camera.save();
        switch (type) {
            case TYPE_TRANSLATE:
                camera.translate(deltaX, deltaY, deltaZ);
                break;
            case TYPE_ROTATE_X:
                camera.rotate(-deltaX, 0f, 0f);
                camera.translate(0.0f, 0.0f, deltaZ);
                break;
            case TYPE_ROTATE_Y:
                camera.rotate(0f, -deltaY, 0f);
                camera.translate(0.0f, 0.0f, interpolatedTime < 0.5f ? deltaZ : deltaZ - interpolatedTime * deltaZ);
                break;
            case TYPE_ROTATE_Z:
                camera.rotate(0f, 0f, -deltaZ);
                break;
        }

        centerX = view.get().getWidth() / 2f;
        centerY = view.get().getHeight() / 2f;

        camera.getMatrix(t.getMatrix());
        camera.restore();

        t.getMatrix().preTranslate(-centerX, -centerY);
        t.getMatrix().postTranslate(centerX, centerY);
    }

    public Rotate3D duration(long duration) {
        setDuration(duration);
        return this;
    }

    public void start(long delay) {
        if (view == null || view.get() == null) {
            return ;
        }

        view.get().postDelayed(() -> {
            view.get().startAnimation(this);
        }, delay);
    }

    @Override
    public void cancel() {
        super.cancel();
        camera = null;
    }
}
