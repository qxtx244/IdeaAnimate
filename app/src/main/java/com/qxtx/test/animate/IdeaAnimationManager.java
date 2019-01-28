package com.qxtx.test.animate;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @CreateDate 2019/01/16 16:08.
 * @Author QXTX-GOSPELL
 */
public class IdeaAnimationManager implements IManager<IdeaAnimation> {
    public static final String TAG = "IdeaAnimationManager";
    private static final int DEFAULT_DURATION = 500;
    private static final int ABSOLUTE = Animation.ABSOLUTE;
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
    public void add(IdeaAnimation animator) {
        animationList.add(animator);
    }

    @Override
    public void remove(IdeaAnimation animator) {
        animationList.remove(animator);
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

    public static IdeaAnimationFrame animateFrame(@NonNull ImageView target, @NonNull Drawable... drawables) {
        return baseFrame(target).addFrame(drawables);
    }

    public static IdeaAnimationFrame animateFrame(@NonNull ImageView target, @NonNull int... resIds) {
        return baseFrame(target).addFrame(target.getContext(), resIds);
    }

    public static IdeaAnimation bounce(@NonNull View target) {
        float jump = target.getHeight() / 2f;
        float high = jump < 50f ? 50f : jump;
        return bounce(target, high, IdeaUtil.VERTICAL);
    }

    public static IdeaAnimation bounce(@NonNull View target, float high, @IdeaUtil.Orientation int orientation) {
        float toXValue = orientation == IdeaUtil.VERTICAL ? 0f : high;
        float toYValue = orientation == IdeaUtil.VERTICAL ? high : 0f;

        TranslateAnimation animation = new TranslateAnimation(ABSOLUTE, -toXValue,
                ABSOLUTE, 0f,
                ABSOLUTE, -toYValue, ABSOLUTE, 0f);

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
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                .setDuration(DEFAULT_DURATION)
                .setRepeat(Animation.INFINITE, Animation.REVERSE);
        IdeaAnimation animY = baseScale(target, beatsLevel, beatsLevel,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                .setDuration(DEFAULT_DURATION)
                .setRepeat(Animation.INFINITE, Animation.REVERSE);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(animX.getAnimation());
        set.addAnimation(animY.getAnimation());
        target.post(() -> {
            target.startAnimation(set);
        });
    }

    public static void lineMove(@NonNull View target, @NonNull float[]... pointers) {
//        IdeaAnimation[] idea = new IdeaAnimation[pointers.length];
//        for (int i = 0; i < pointers.length; i++) {
//            float[] subPointer = pointers[i];
//            TranslateAnimation animation = new TranslateAnimation(
//                    ABSOLUTE, target.getTranslationX(), ABSOLUTE, subPointer[0],
//                    ABSOLUTE, target.getTranslationY(), ABSOLUTE, subPointer[1]);
//            idea[i] = new IdeaAnimation(target, animation)
//                    .setDuration(DEFAULT_DURATION)
//                    .setFillEnabled(true)
//                    .setFillAfter(true);
//        }
//
//        for (int i = 0; i < idea.length; i++) {
//            idea[i].startWithDelay(DEFAULT_DURATION * i);
//        }
    }

    public static IdeaAnimation doorOpen(@NonNull View target, @IdeaUtil.Direction int direction) {
        throw new IllegalStateException("Useless this method now!");
    }

    public static IdeaAnimation rotate(@NonNull View target, float toDegrees,
                                       int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        return baseRotate(target, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    public static IdeaAnimation rotate(@NonNull View target, float toDegrees) {
        return baseRotate(target, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    }

    public static IdeaAnimation rotateX(@NonNull View target, float toDegrees, boolean is3D) {
        throw new IllegalStateException("Useless this method now!");
    }

    public static IdeaAnimation rotateY(@NonNull View target, float toDegrees, boolean is3D) {
        throw new IllegalStateException("Useless this method now!");
    }

    public static IdeaAnimation scale(@NonNull View target, float toX, float toY,
                                      int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        return baseScale(target, toX, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    public static IdeaAnimation scale(@NonNull View target, float toX, float toY) {
        return baseScale(target, toX, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    }

    public static IdeaAnimation scaleX(@NonNull View target, float toX) {
        float toY = target.getScaleY();
        return baseScale(target, toX, toY, ABSOLUTE, 0.0f, ABSOLUTE, 0.0f);
    }

    public static IdeaAnimation scaleY(@NonNull View target, float toY) {
        float toX = target.getScaleX();
        return baseScale(target, toX, toY, ABSOLUTE, 0.0f, ABSOLUTE, 0.0f);
    }

    public static IdeaAnimation shake(@NonNull View target, @IdeaUtil.Orientation int orientation, int level) {
        throw new IllegalStateException("Useless this method now!");
    }

    public static IdeaAnimation swinging(@NonNull View target, float angle) {
        throw new IllegalStateException("Useless this method now!");
    }

    public static IdeaAnimation translate(@NonNull View target, int toXType, float toXValue, int toYType, float toYValue) {
        int absolute = ABSOLUTE;
        return baseTranslate(target, absolute, toXValue, toYType, toYValue);
    }

    public static IdeaAnimation translate(@NonNull View target, float toX, float toY) {
        return baseTranslate(target, ABSOLUTE, toX, ABSOLUTE, toY);
    }

    public static IdeaAnimation translateX(@NonNull View target, float toXValue) {
        return baseTranslate(target, ABSOLUTE, toXValue, ABSOLUTE, 0.0f);
    }

    public static IdeaAnimation translateY(@NonNull View target, float toYValue) {
        return baseTranslate(target, ABSOLUTE, 0.0f, ABSOLUTE, toYValue);
    }

    private static IdeaAnimation baseAlpha(@NonNull View v, float fromAlpha, float toAlpha) {
        AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
        return new IdeaAnimation(v, alpha)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(new LinearInterpolator());
    }

    private static IdeaAnimationFrame baseFrame(@NonNull ImageView v) {
        return new IdeaAnimationFrame(v).setDuration(DEFAULT_DURATION);
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

    private static IdeaAnimation baseRotate(@NonNull View v, float toDegrees,
                                            int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        float fromDegrees = v.getRotation();
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
        return new IdeaAnimation(v, rotate)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(new LinearInterpolator());
    }

    private static IdeaAnimation baseTranslate(@NonNull View v,
                                               int toXType, float toX, int toYType, float toY) {
        TranslateAnimation translate = new TranslateAnimation(ABSOLUTE, v.getTranslationX(), toXType, toX,
                ABSOLUTE, v.getTranslationY(), toYType, toY);

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

    public static final class IdeaAnimationFrame {
        private AnimationDrawable animation;
        private WeakReference<ImageView> target;
        private List<Drawable> drawables;
        private List<Integer> durations;
        private Thread tRepeat;

        private IdeaAnimationFrame(@NonNull ImageView target) {
            this.target = new WeakReference<ImageView>(target);
            animation = new AnimationDrawable();
            this.target.get().setImageDrawable(animation);
            drawables = new ArrayList<>();
            durations = new ArrayList<>();
        }

        public IdeaAnimationFrame addFrame(Drawable... drawables) {
            this.drawables.addAll(Arrays.asList(drawables));
            return this;
        }

        public IdeaAnimationFrame addFrame(Context context, @NonNull int... resId) {
            for (int id : resId) {
                drawables.add(context.getResources().getDrawable(id));
            }
            return this;
        }

        public IdeaAnimationFrame addDuration(@NonNull int... durations) {
            for (int i : durations) {
                this.durations.add(i);
            }
            return this;
        }

        public IdeaAnimationFrame setDuration(@NonNull int... durations) {
            int durationSize = durations.length;
            if (durationSize != drawables.size() || durationSize != 1) {
                Log.e(TAG, "Set duration failed. Invalid number of durations.");
                return this;
            }

            this.durations.clear();
            for (int i : durations) {
                this.durations.add(i);
            }
            return this;
        }

        public int getDuration(int index) {
            return durations.get(index);
        }

        public Drawable getFrame(int index) {
            return drawables.get(index);
        }

        public int getNumOfFrames() {
            return drawables.size();
        }

        public IdeaAnimationFrame setOneShot(boolean isOneShot) {
            animation.setOneShot(isOneShot);
            return this;
        }

        public IdeaAnimationFrame setVisible(boolean visible, boolean restartWhenVisible) {
            animation.setVisible(visible, restartWhenVisible);
            return this;
        }

        public void start() {
            start(0, false);
        }

        public void start(long delay) {
            start(delay, false);
        }

        public void start(boolean isReverse) {
            start(0, isReverse);
        }

        public void start(long delay, boolean isReverse) {
            if (durations.size() == 0) {
                addDuration(DEFAULT_DURATION);
            }

            int drawableSize = drawables.size();
            int durationSize = durations.size();
            if (drawableSize != durationSize && durationSize != 1) {
                Log.e(TAG, "Fail to start animation. Each frame of animation must be set duration!");
                return ;
            }

            for (int i = 0; i < drawableSize; i++) {
                int index = isReverse ? drawableSize - 1 - i : i;
                int duration = durationSize == 1 ? durations.get(0) : durations.get(index);
                animation.addFrame(drawables.get(index), duration);
            }

            if (target != null && target.get() != null) {
                target.get().postDelayed(animation::start, delay);
            }
        }

        public void startWithRepeat(int repeat) {
            if (repeat <= 0) {
                Log.e(TAG, "Can't to set animation repeat.");
                return ;
            }

            int drawableSize = drawables.size();
            setOneShot(true);
            long nextRepeatDelay = 0;
            for (int i = 0; i < drawableSize; i++) {
                nextRepeatDelay += animation.getDuration(i);
            }

            final long nextRepeat = nextRepeatDelay;
            tRepeat = new Thread(() -> {
                for (int i = 0; i < repeat + 1; i++) {
                    SystemClock.sleep(nextRepeat * i);
                    target.get().post(() -> start(0, false));
                }
            });
            tRepeat.start();
        }

        public void stop() {
            animation.stop();
            if (tRepeat != null) {
                tRepeat.interrupt();
                tRepeat = null;
            }
        }

        public boolean isOneShot() {
            return animation.isOneShot();
        }

        public boolean isRunning() {
            return animation.isRunning();
        }
    }
}
