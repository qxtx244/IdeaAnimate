package org.qxtx.idea.animate.frame;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @CreateDate 2019/02/14 9:45.
 * @Author QXTX-GOSPELL
 */

public class IdeaFrame {
    public static final String TAG = "IdeaAnimationManager";
    private static final int DEFAULT_DURATION = 500;

    private static IdeaAnimateFrame baseFrame(@NonNull ImageView v) {
        return new IdeaAnimateFrame(v).setDuration(DEFAULT_DURATION);
    }

    public static IdeaAnimateFrame frame(@NonNull ImageView target, @NonNull Drawable... drawables) {
        return baseFrame(target).addFrame(drawables);
    }

    public static IdeaAnimateFrame frame(@NonNull ImageView target, @NonNull int... resIds) {
        return baseFrame(target).addFrame(target.getContext(), resIds);
    }

    public static final class IdeaAnimateFrame {
        private final String TAG = "";
        private AnimationDrawable animation;
        private WeakReference<ImageView> target;
        private List<Drawable> drawables;
        private List<Integer> durations;
        private Thread tRepeat;

        private IdeaAnimateFrame(@NonNull ImageView target) {
            this.target = new WeakReference<ImageView>(target);
            animation = new AnimationDrawable();
            this.target.get().setImageDrawable(animation);
            drawables = new ArrayList<>();
            durations = new ArrayList<>();
        }

        public IdeaAnimateFrame addFrame(Drawable... drawables) {
            this.drawables.addAll(Arrays.asList(drawables));
            return this;
        }

        public IdeaAnimateFrame addFrame(Context context, @NonNull int... resId) {
            for (int id : resId) {
                drawables.add(context.getResources().getDrawable(id));
            }
            return this;
        }

        public IdeaAnimateFrame addDuration(@NonNull int... durations) {
            for (int i : durations) {
                this.durations.add(i);
            }
            return this;
        }

        public IdeaAnimateFrame setDuration(@NonNull int... durations) {
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

        public IdeaAnimateFrame setOneShot(boolean isOneShot) {
            animation.setOneShot(isOneShot);
            return this;
        }

        public IdeaAnimateFrame setVisible(boolean visible, boolean restartWhenVisible) {
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
