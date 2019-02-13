package qxtx.idea.animate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;

/**
 * @CreateDate 2019/01/16 16:05.
 * @Author QXTX-GOSPELL
 */
public class IdeaAnimation {
    private final String TAG = "IdeaAnimation";
    private final WeakReference<View> target;
    private final String tag;
    private Animation animation;

    public IdeaAnimation(@NonNull View target, @NonNull Animation animation) {
        this(target, animation, null);
    }

    public IdeaAnimation(@NonNull View target, @NonNull Animation animation, @Nullable String tag) {
        IdeaAnimationManager manager = IdeaAnimationManager.getInstance();
        this.tag = tag == null ? manager.getCount() + "" : tag;
        this.animation = animation;
        this.target = new WeakReference<View>(target);

        manager.add(this);
    }

    public String getTag() {
        return tag;
    }

    public View getTarget() {
        return target.get();
    }

    public Animation getAnimation() {
        return animation;
    }

    public IdeaAnimation setBackgroundColor(int backgroundColor) {
        animation.setBackgroundColor(backgroundColor);
        return this;
    }

    public IdeaAnimation setDuration(long durationMs) {
        animation.setDuration(durationMs);
        return this;
    }

    public IdeaAnimation setFillBefore(boolean fillBefore) {
        animation.setFillBefore(fillBefore);
        return this;
    }

    public IdeaAnimation setFillAfter(boolean fillAfter) {
        animation.setFillAfter(fillAfter);
        return this;
    }

    public IdeaAnimation setFillEnabled(boolean fillEnabled) {
        animation.setFillEnabled(fillEnabled);
        return this;
    }

    public IdeaAnimation setInterpolator(Interpolator interpolator) {
        animation.setInterpolator(interpolator);
        return this;
    }

    public IdeaAnimation setInterpolator(Context context, int resId) {
        animation.setInterpolator(context, resId);
        return this;
    }

    public IdeaAnimation setListener(Animation.AnimationListener listener) {
        animation.setAnimationListener(listener);
        return this;
    }

    public IdeaAnimation setRepeat(int repeatCount) {
        return setRepeat(repeatCount, IdeaUtil.MODE_RESTART);
    }

    public IdeaAnimation setRepeat(int repeatCount, int repeatMode) {
        animation.setRepeatCount(repeatCount);
        animation.setRepeatMode(repeatMode);
        return this;
    }

    public IdeaAnimation setStartOffset(long startOffsetMs) {
        animation.setStartOffset(startOffsetMs);
        return this;
    }

    public IdeaAnimation setStartTime(long startTimeMs) {
        animation.setStartTime(startTimeMs);
        return this;
    }

    public IdeaAnimation setZAdjustment(int zaDjustment) {
        animation.setZAdjustment(zaDjustment);
        return this;
    }

    public IdeaAnimation setDerachWallpaper(boolean isDerachWallpaper) {
        animation.setDetachWallpaper(isDerachWallpaper);
        return this;
    }

    public void start() {
        startWithDelay(0);
    }

    public void startWithDelay(final long delay) {
        boolean viewExist = target != null && target.get() != null;
        if (viewExist) {
            target.get().postDelayed(() -> {
                target.get().startAnimation(animation);
            }, delay);
        }
    }

    public boolean hasEnded() {
        return animation.hasEnded();
    }

    public boolean hasStarted() {
        return animation.hasStarted();
    }

    public boolean isFillEnabled() {
        return animation.isFillEnabled();
    }

    public boolean isInitialized() {
        return animation.isInitialized();
    }

    public boolean willChangeBounds() {
        return animation.willChangeBounds();
    }

    public void release() {
        animation.cancel();
        IdeaAnimationManager.getInstance().remove(this);
    }
}
