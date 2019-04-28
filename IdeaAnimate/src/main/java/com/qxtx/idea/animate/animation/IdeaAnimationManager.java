package com.qxtx.idea.animate.animation;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.qxtx.idea.animate.IManager;
import com.qxtx.idea.animate.IdeaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateDate 2019/01/16 16:08.
 * @Author QXTX-GOSPELL
 *
 * A class for take animation easily.
 *
 * @see IdeaAnimation
 * @see DoorAction
 * @see IdeaUtil
 * @see Rotate3D
 */
public class IdeaAnimationManager implements IManager<IdeaAnimation> {
    public static final String TAG = "IdeaAnimationManager";
    private static final int DEFAULT_DURATION = 500;
    private static IdeaAnimationManager manager;
    private List<IdeaAnimation> animationList;

    private IdeaAnimationManager() {
        animationList = new ArrayList<>();
    }

    /**
     * Get single instance of {@link IdeaAnimationManager}.
     * @return {@link IdeaAnimationManager} The object which call this
     */
    public static IdeaAnimationManager getInstance() {
        if (manager == null) {
            synchronized (IdeaAnimationManager.class) {
                if (manager == null) {
                    manager = new IdeaAnimationManager();
                }
            }
        }
        return manager;
    }

    @Override
    public void add(IdeaAnimation idea) {
        animationList.add(idea);
    }

    @Override
    public void remove(IdeaAnimation idea) {
        animationList.remove(idea);
    }

    @Override
    public void remove(@NonNull String tag) {
        for (IdeaAnimation idea : animationList) {
            if (idea.getTag().equals(tag)) {
                animationList.remove(idea);
            }
        }
    }

    @Override
    public void remove(int index) {
        animationList.remove(index);
    }

    @Override
    public int getCount() {
        return animationList.size();
    }

    @Override
    public List<IdeaAnimation> getAnimateList() {
        return animationList;
    }

    @Override
    public List<IdeaAnimation> get(@NonNull String tag) {
        List<IdeaAnimation> ideas = new ArrayList<>();
        for (IdeaAnimation idea : animationList) {
            if (idea.getTag().equals(tag)) {
                ideas.add(idea);
            }
        }
        return ideas;
    }

    public static IdeaAnimation alpha(@NonNull View target, float fromAlpha, float toAlpha) {
        return baseAlpha(target, fromAlpha, toAlpha);
    }

    public static IdeaAnimation alphaHide(@NonNull View target) {
        return baseAlpha(target, target.getAlpha(), 0f);
    }

    public static IdeaAnimation alphaShow(@NonNull View target) {
        return baseAlpha(target, target.getAlpha(), 1f);
    }

    public static IdeaAnimation bounce(@NonNull View target) {
        float jump = target.getHeight() / 2f;
        float high = jump < 50f ? 50f : jump;
        return bounce(target, high, IdeaUtil.VERTICAL);
    }

    public static IdeaAnimation bounce(@NonNull View target, float high, @IdeaUtil.Orientation int orientation) {
        float toXValue = orientation == IdeaUtil.VERTICAL ? 0f : high;
        float toYValue = orientation == IdeaUtil.VERTICAL ? high : 0f;

        TranslateAnimation animation = new TranslateAnimation(IdeaUtil.ABSOLUTE, -toXValue,
                IdeaUtil.ABSOLUTE, 0f,
                IdeaUtil.ABSOLUTE, -toYValue, IdeaUtil.ABSOLUTE, 0f);

        return new IdeaAnimation(target, animation)
                .setDuration(DEFAULT_DURATION * 2)
                .setInterpolator(new BounceInterpolator());
    }

    public static IdeaAnimation breathe(@NonNull View target) {
        float fromAlpha = target.getAlpha();
        float toAlpha = fromAlpha <= 0.5f ? 1f : 0f;
        return baseAlpha(target, fromAlpha, toAlpha)
                .setRepeat(IdeaUtil.INFINITE, IdeaUtil.MODE_REVERSE);
    }

    public static void doorClose(@NonNull View target, @IdeaUtil.Direction int direction) {
        baseDoor(target, direction, false);
    }

    public static void doorOpen(@NonNull View target, @IdeaUtil.Direction int direction) {
        baseDoor(target, direction, true);
    }

    public static IdeaAnimation flicker(@NonNull View target, int count) {
        count = count < 0 ? 0 : count * 2;
        float fromAlpha = target.getAlpha();
        float toAlpha = fromAlpha == 0f ? 1f : 0f;
        return baseAlpha(target, fromAlpha, toAlpha)
                .setRepeat(count, IdeaUtil.MODE_REVERSE)
                .setDuration(DEFAULT_DURATION * 3);
    }

    public static void heartBeats(@NonNull View target, int level) {
        level = level > 10 ? 10 : level;
        level = level <= 0 ? 1 : level;
        float beatsLevel = (float)level * 0.2f;
        IdeaAnimation animX = baseScale(target, beatsLevel, beatsLevel,
                IdeaUtil.RELATIVE_TO_SELF, 0.5f, IdeaUtil.RELATIVE_TO_SELF, 0.5f)
                .setDuration(DEFAULT_DURATION)
                .setRepeat(IdeaUtil.INFINITE, IdeaUtil.MODE_REVERSE);
        IdeaAnimation animY = baseScale(target, beatsLevel, beatsLevel,
                IdeaUtil.RELATIVE_TO_SELF, 0.5f, IdeaUtil.RELATIVE_TO_SELF, 0.5f)
                .setDuration(DEFAULT_DURATION)
                .setRepeat(IdeaUtil.INFINITE, IdeaUtil.MODE_REVERSE);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(animX.getAnimation());
        set.addAnimation(animY.getAnimation());
        target.post(() -> {
            target.startAnimation(set);
        });
    }

    public static void lineMove(@NonNull View target, @NonNull float[]... pointers) {
        throw new IllegalStateException("Useless this method now!");
    }

    public static IdeaAnimation rotate(@NonNull View target, float toAngle,
                                       int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        return baseRotate(target, target.getRotation(), toAngle, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    public static IdeaAnimation rotate(@NonNull View target, float toAngle) {
        return baseRotate(target, target.getRotation(), toAngle,
                IdeaUtil.RELATIVE_TO_SELF, 0.5f, IdeaUtil.RELATIVE_TO_SELF, 0.5f);
    }

    public static void rotateX(@NonNull View target, float toAngle, boolean is3D) {
        float fromZ = target.getTranslationZ();
        float toZ = fromZ;
        if (is3D) {
            toZ += -target.getWidth();
        }
        float[] fromValues = new float[] {target.getRotationX(), 0.0f, fromZ};
        float[] toValues = new float[] {toAngle, 0.0f, toZ};
        new Rotate3D(target, Rotate3D.TYPE_ROTATE_X, fromValues, toValues)
                .duration(DEFAULT_DURATION * 2)
                .start(0);
    }

    public static void rotateY(@NonNull View target, float toAngle, boolean is3D) {
        float fromZ = target.getTranslationZ();
        float toZ = fromZ;
        if (is3D) {
            toZ -= target.getWidth();
        }

        float[] fromValues = new float[] {0f, target.getRotationY(), fromZ};
        float[] toValues = new float[] {0f, toAngle, toZ};
       new Rotate3D(target, Rotate3D.TYPE_ROTATE_Y, fromValues, toValues)
               .duration(DEFAULT_DURATION * 2)
               .start(0);
    }

    public static void rotateZ(@NonNull View target, float toAngle, boolean is3D) {
        float[] fromValues = new float[] {0.0f, 0.0f, target.getRotation()};
        float[] toValues = new float[] {0.0f, 0.0f, toAngle};
        new Rotate3D(target, Rotate3D.TYPE_ROTATE_Z, fromValues, toValues)
                .duration(DEFAULT_DURATION * 2)
                .start(0);
    }

    public static IdeaAnimation scale(@NonNull View target, float toX, float toY,
                                      int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        return baseScale(target, toX, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    public static IdeaAnimation scale(@NonNull View target, float toX, float toY) {
        return baseScale(target, toX, toY, IdeaUtil.RELATIVE_TO_SELF, 0.5f, IdeaUtil.RELATIVE_TO_SELF, 0.5f);
    }

    public static IdeaAnimation scaleX(@NonNull View target, float toX) {
        float toY = target.getScaleY();
        return baseScale(target, toX, toY, IdeaUtil.ABSOLUTE, 0.0f, IdeaUtil.ABSOLUTE, 0.0f);
    }

    public static IdeaAnimation scaleY(@NonNull View target, float toY) {
        float toX = target.getScaleX();
        return baseScale(target, toX, toY, IdeaUtil.ABSOLUTE, 0.0f, IdeaUtil.ABSOLUTE, 0.0f);
    }

    public static IdeaAnimation shake(@NonNull View target, @IdeaUtil.Orientation int orientation, int level) {
        boolean isHor = orientation == IdeaUtil.HORIZONTAL;
        float shakeValue = ((level > 10 ? 100f : (float)level) * 10f) / 2f;
        float fromX = isHor ? target.getTranslationX() - shakeValue : 0f;
        float fromY = isHor ? 0f : target.getTranslationY() - shakeValue;
        float toX = isHor ? shakeValue : 0f;
        float toY = isHor ? 0f : shakeValue;

        TranslateAnimation animation = new TranslateAnimation(IdeaUtil.ABSOLUTE, fromX, IdeaUtil.ABSOLUTE, toX,
                IdeaUtil.ABSOLUTE, fromY, IdeaUtil.ABSOLUTE, toY);
        return new IdeaAnimation(target, animation)
                .setDuration(100)
                .setInterpolator(new LinearInterpolator())
                .setRepeat(2, IdeaUtil.MODE_REVERSE);
    }

    public static IdeaAnimation swinging(@NonNull View target, float angle) {
        float rotate = target.getRotation();
        float fromAngle = rotate - angle;
        float toAngle = rotate + angle;
        return baseRotate(target, fromAngle, toAngle,
                IdeaUtil.RELATIVE_TO_SELF, 0.5f, IdeaUtil.RELATIVE_TO_SELF, 0.5f)
                .setRepeat(2, IdeaUtil.MODE_REVERSE);
    }

    public static IdeaAnimation swinging(@NonNull View target) {
        float rotate = target.getRotation();
        float fromAngle = rotate - 15f;
        float toAngle = rotate + 15f;
        return baseRotate(target, fromAngle, toAngle,
                IdeaUtil.RELATIVE_TO_SELF, 0.5f, IdeaUtil.RELATIVE_TO_SELF, 0.5f)
                .setRepeat(2, IdeaUtil.MODE_REVERSE)
                .setDuration(200);
    }

    public static IdeaAnimation translate(@NonNull View target, int toXType, float toXValue, int toYType, float toYValue) {
        return baseTranslate(target, IdeaUtil.ABSOLUTE, toXValue, toYType, toYValue);
    }

    public static IdeaAnimation translate(@NonNull View target, float toX, float toY) {
        return baseTranslate(target, IdeaUtil.ABSOLUTE, toX, IdeaUtil.ABSOLUTE, toY);
    }

    public static IdeaAnimation translateX(@NonNull View target, float toXValue) {
        return baseTranslate(target, IdeaUtil.ABSOLUTE, toXValue, IdeaUtil.ABSOLUTE, 0.0f);
    }

    public static IdeaAnimation translateY(@NonNull View target, float toYValue) {
        return baseTranslate(target, IdeaUtil.ABSOLUTE, 0.0f, IdeaUtil.ABSOLUTE, toYValue);
    }

    private static IdeaAnimation baseAlpha(@NonNull View v, float fromAlpha, float toAlpha) {
        AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
        return new IdeaAnimation(v, alpha)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(new LinearInterpolator());
    }

    private static void baseDoor(@NonNull View target, @IdeaUtil.Direction int direction, boolean isOpen) {
        boolean isHor = direction == IdeaUtil.LEFT || direction == IdeaUtil.RIGHT;
        float pivotX = 0f;
        float pivotY = 0f;
        float toValue = 0f;

        switch (direction) {
            case IdeaUtil.LEFT:
                toValue = isOpen ? 180f : -180f;
                pivotX = 0f;
                break;
            case IdeaUtil.TOP:
                toValue = isOpen ? -180f : 180f;
                pivotY = 0f;
                break;
            case IdeaUtil.RIGHT:
                toValue = isOpen ? -180f : 180f;
                pivotX = (float)target.getWidth();
                break;
            case IdeaUtil.BOTTOM:
                toValue = isOpen ? 180f : -180f;
                pivotY = (float)target.getHeight();
                break;
        }

        new DoorAction(target, pivotX, pivotY, toValue, isHor, isOpen)
                .duration(DEFAULT_DURATION)
                .fillAfter(true)
                .start(0);
    }

    private static IdeaAnimation baseRotate(@NonNull View v, float fromAngle, float toAngle,
                                            int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        RotateAnimation rotate = new RotateAnimation(fromAngle, toAngle, pivotXType, pivotXValue, pivotYType, pivotYValue);
        return new IdeaAnimation(v, rotate)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(new LinearInterpolator());
    }

    private static IdeaAnimation baseScale(@NonNull View v, float toX, float toY,
                                           int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        float fromX = v.getScaleX();
        float fromY = v.getScaleY();
        ScaleAnimation scale = new ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
        return new IdeaAnimation(v, scale)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(new LinearInterpolator());
    }

    private static IdeaAnimation baseTranslate(@NonNull View v,
                                               int toXType, float toX, int toYType, float toY) {
        TranslateAnimation translate = new TranslateAnimation(IdeaUtil.ABSOLUTE, v.getTranslationX(), toXType, toX,
                IdeaUtil.ABSOLUTE, v.getTranslationY(), toYType, toY);

        return new IdeaAnimation(v, translate)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(new LinearInterpolator());
    }

    @Override
    public void release() {
        if (animationList != null) {
            for (IdeaAnimation idea : animationList) {
                idea.release();
            }
            animationList.clear();
            animationList = null;
        }

        manager = null;
    }
}
