package org.qxtx.idea.animate.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.TypeConverter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Property;

import junit.framework.Assert;

import org.qxtx.idea.animate.IdeaUtil;
import org.qxtx.idea.animate.PropertyFactory;

import java.lang.reflect.Method;

/** Solution method of animator. **/
public final class IdeaAnimator {
    private final String TAG = "IdeaAnimator";
    private final String tag;
    private final ValueAnimator animator;
    private boolean allowStart;

    public IdeaAnimator() {
        this(null, null);
    }

    public IdeaAnimator(@NonNull String tag) {
        this(null, tag);
    }

    public IdeaAnimator(@NonNull Object target) {
        this(target, null);
    }

    public IdeaAnimator(@Nullable Object target, @Nullable String tag) {
        IdeaAnimatorManager manager = IdeaAnimatorManager.getInstance();
        this.tag = tag == null ? manager.getCount() + "" : tag;
        allowStart = true;

        if (target == null) {
            animator = new ValueAnimator();
        } else {
            animator = new ObjectAnimator();
            animator.setTarget(target);
            if (target instanceof PropertyFactory) {
                ((ObjectAnimator)animator).setPropertyName(PropertyFactory.PROPERTY_CUSTOM);
            }
        }

        manager.add(this);
    }

    public String getTag() {
        return tag;
    }

    /**
     * You can get the internal animator object to call getter.
     * @return {@link ValueAnimator} The object of animator
     */
    @Deprecated
    public ValueAnimator getAnimator() {
        return animator;
    }

    /*/**************** Public implement *****************/

    public IdeaAnimator addListenerAdapter(AnimatorListenerAdapter adapter) {
        animator.addListener(adapter);
        return this;
    }

    /**
     * Listen the status update of animator and control it. If target is not set, It must be called.
     * It maybe covered by {@link #setPropertyValuesHolder(PropertyValuesHolder...)} while set change the same property,
     *  but not conflict with difference property.
     * @param listener Update listener
     * @return  {@link IdeaAnimator} The object called with this
     */
    public IdeaAnimator addUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
        animator.addUpdateListener(listener);
        return this;
    }

    public long getStartDelay() {
        return animator.getStartDelay();
    }

    /**
     * It always work in a {@link ObjectAnimator}. As other animator, it work by set a listener to cancel while animate end,
     *  and it will be not able to cancel after remove this listener.
     * @param autoCancel if cancel animate when the animate end
     * @return {@link IdeaAnimator} The object called with this
     */
    public IdeaAnimator setAutoCancel(boolean autoCancel) {
        if (animator instanceof ObjectAnimator) {
            ((ObjectAnimator) animator).setAutoCancel(autoCancel);
        } else {
            if (autoCancel) {
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        cancel();
                    }
                });
            }
        }
        return this;
    }

    /**
     * Remove all listeners but not contain listener type of {@link ValueAnimator.AnimatorUpdateListener},
     *  but you can call {@link #removeAllUpdateListeners} if you need.
     */
    public void removeAllListeners() {
        animator.removeAllListeners();
    }

    /**
     * Remove all updateListeners. If you need to remove other listener,
     *  call {@link #removeListener} or {@link #removeAllListeners}.
     */
    public void removeAllUpdateListeners() {
        animator.removeAllUpdateListeners();
    }

    public void removePauseListener(Animator.AnimatorPauseListener listener) {
        animator.removePauseListener(listener);
    }

    public void removeListener(Animator.AnimatorListener listener) {
        animator.removeListener(listener);
    }

    public void removeUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
        animator.removeUpdateListener(listener);
    }

    /**
     * WTF method?
     * @param property
     * @param converter
     * @param evaluator
     * @param values
     * @return {@link IdeaAnimator} The object called with this
     */
    public <T, V, P> IdeaAnimator ofObject(Property<T, P> property, TypeConverter<V, P> converter, TypeEvaluator<V> evaluator, V... values) {
        PropertyValuesHolder holder = PropertyValuesHolder.ofObject(property, converter, evaluator, values);
        return setPropertyValuesHolder(holder);
    }

    /**
     * WTF method?
     * @param property
     * @param values
     * @return
     */
    public IdeaAnimator ofFloat(Property<?, Float> property, float... values) {
        PropertyValuesHolder holder = PropertyValuesHolder.ofFloat(property, values);
        return setPropertyValuesHolder(holder);
    }

    /**
     * What the fuck method ?
     * @param property
     * @param values
     * @return {@link IdeaAnimator} The object called with this
     */
    public IdeaAnimator ofInt(Property<?, Integer> property, int... values) {
        PropertyValuesHolder holder = PropertyValuesHolder.ofInt(property, values);
        return setPropertyValuesHolder(holder);
    }

    public void setAllowStart(boolean allowStart) {
        this.allowStart = allowStart;
    }

    /**
     * Set a Path for animator. It is best not call together with any of {@link #setIntValues}, {@link #setFloatValues}
     *  or {@link #setObjectValues}, or make conflict. You only need to call this and {@link #setDuration(long)}
     *  to make a simply animator. You only need to call this with a {@link #setDuration(long)} to make a simply animator.
     * @param path Path of animator change with X&Y
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setPath(Path path) {
        PropertyValuesHolder xHolder = PropertyValuesHolder.ofObject("x", new TypeConverter<PointF, Float>(PointF.class, Float.TYPE) {
            @Override
            public Float convert(PointF value) {
                return value.x;
            }
        }, path);

        PropertyValuesHolder yHolder = PropertyValuesHolder.ofObject("y", new TypeConverter<PointF, Float>(PointF.class, Float.TYPE) {
            @Override
            public Float convert(PointF value) {
                return value.y;
            }
        }, path);

        animator.setValues(xHolder, yHolder);
        return this;
    }

    /**
     * Take animator come to the relative position of fraction immediately.
     *  It work at sdk version number of 22 or higher.
     *  @param fraction Value range of 0f~1f
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setCurrentFraction(float fraction) {
        if (Build.VERSION.SDK_INT >= 22) {
            fraction = fraction < 0f ? 0f : fraction;
            fraction = fraction > 1f ? 1f : fraction;
            animator.setCurrentFraction(fraction);
        } else {
            Log.e(TAG, "Fail to setCntFraction because of the version lower than Android 5.1");
        }
        return this;
    }

    /**
     * Set duration for animator. It was not include the delay time before start.
     *  It must be called to a animator.
     * @param durationMs The length of the animator, in milliseconds. This value cannot be negative
     * @return  {@link IdeaAnimator} The object called with this
     */
    public IdeaAnimator setDuration(long durationMs) {
        durationMs = durationMs < 0 ? 0 : durationMs;
        animator.setDuration(durationMs);
        return this;
    }

    /**
     * It will be auto set a {@link android.animation.FloatEvaluator},
     *   so you don't need to call {@link #setTypeEvaluator(TypeEvaluator)} if you don't want to set it.
     *   It must be called if animator need values of type float. <b>Specially, It will auto set interpolator of {@link android.view.animation.AccelerateDecelerateInterpolator}</b>.
     *  @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setFloatValues(@NonNull float... values) {
        animator.setFloatValues(values);
        return this;
    }

    /**
     * Speed control for animator. Auto set {@link android.view.animation.AccelerateDecelerateInterpolator} if you not to call this.
     * @param interpolator  A timeInterpolator to make speed control for Animator
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setInterpolator(@NonNull TimeInterpolator interpolator) {
        animator.setInterpolator(interpolator);
        return this;
    }

    /**
     * It will be auto set the {@link android.animation.IntEvaluator},
     *   so you don't need to call {@link #setTypeEvaluator(TypeEvaluator)} if you don't want to set it.
     *   It must be called if animator need values of type int. <b>Specially, It will auto set interpolator
     *   of {@link android.view.animation.AccelerateDecelerateInterpolator}</b>.
     * @param values A value array type of int
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setIntValues(@NonNull int... values) {
        Assert.assertTrue("number of value must be at least one.", values.length > 0);
        animator.setIntValues(values);
        return this;
    }

    /**
     * It must be use with {@link #setTypeEvaluator(TypeEvaluator)} because animator
     *   don't have any objectEvaluator implement to make change with the object value.
     *   It must be called if animator need values from object.
     * @param values  A value array type of Object
     * @param evaluator  A typeEvaluator to take a scheme to set values to change animator
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setObjectValues(@NonNull TypeEvaluator evaluator, @NonNull Object... values) {
        Assert.assertTrue("number of value must be at least one.", values.length > 0);
        animator.setObjectValues(values);
        animator.setEvaluator(evaluator);
        return this;
    }

    /**
     * Set property for animator. It was only set for {@link ObjectAnimator}.
     * @param property the property of target
     * @return {@link IdeaAnimator} The object call with this
     */
    public IdeaAnimator setProperty(Property property) {
        if (animator instanceof ObjectAnimator) {
            if (!(((ObjectAnimator) animator).getTarget() instanceof PropertyFactory)) {
                ((ObjectAnimator) animator).setProperty(property);
            } else {
                Log.e(TAG, "Invalid call. This method can't used for this animator that target is instance of PropertyFactory<T>");
            }
        } else {
            Log.e(TAG, "Invalid call. This method was only used for ObjectAnimator.");
        }
        return this;
    }

    /**
     * Set a propertyValuesHolder. It is best not set {@link #setIntValues}, {@link #setFloatValues} or {@link #setObjectValues}
     *  and it together, or make conflict. You only need to call this and {@link #setDuration} to make a simply animator.
     * It can be instead of {@link #setPath} and it can use with more simply.
     * @param holders  A PropertyValuesHolder include property, values and change path
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setPropertyValuesHolder(PropertyValuesHolder... holders) {
        animator.setValues(holders);
        return this;
    }

    /**
     * Set property for animator that search by name. It was only set for ObjectAnimator. If it was not found
     *  from target, it will be use {@link PropertyFactory} to make a custom property instance of it.
     *  You can take target with a {@link PropertyFactory} to customize your animator to create more possible.
     *  If propertyName is NULL but target is instance of {@link PropertyFactory}, property will be
     *  {@link PropertyFactory#PROPERTY_CUSTOM} instead of NULL, and not change otherwise. You should be ensure
     *  the setter of property was exist in your propertyFactory object while the property is not
     *  {@link PropertyFactory#PROPERTY_CUSTOM}.
     * @param propertyName the property name of target
     * @return {@link IdeaAnimator} The object call with this
     */
    public IdeaAnimator setPropertyName(String propertyName) {
        if (animator instanceof ObjectAnimator) {
            ObjectAnimator objectAnimator = (ObjectAnimator) animator;
            Object target = objectAnimator.getTarget();
            Object newTarget = shouldBeUsePropertyFactory(target, propertyName);
            objectAnimator.setTarget(newTarget);

            String instanceProperty = ((newTarget instanceof PropertyFactory) && (propertyName == null))
                    ? PropertyFactory.PROPERTY_CUSTOM : propertyName;
            objectAnimator.setPropertyName(instanceProperty);
        } else {
            Log.e(TAG, "Invalid call. It was only set for ObjectAnimator.");
        }
        return this;
    }

    public IdeaAnimator setRepeat(int repeatCount) {
        return setRepeat(repeatCount, IdeaUtil.MODE_RESTART);
    }

    /**
     * Set animator repeat with a repeat mode.
     * The times of animator execute is count+1
     * @param count Times of animator repeat
     * @param mode The mode of animator repeat. see {@link IdeaUtil.RepeatMode}
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setRepeat(int count, @IdeaUtil.RepeatMode int mode) {
        animator.setRepeatCount(count);
        animator.setRepeatMode(mode);
        return this;
    }

    /**
     * It can be make animator delay to execute.
     * @param delay  Delay time to execute animator
     * @return  {@link IdeaAnimator} The object called with this
     **/
    public IdeaAnimator setStartDelay(long delay) {
        animator.setStartDelay(delay);
        return this;
    }

    /**
     * Set a {@link TypeEvaluator} to make control the objectAnimator.
     * @param evaluator  A typeEvaluator to take a scheme to set values to change animator
     * @return  {@link IdeaAnimator} The object called with this
     */
    public IdeaAnimator setTypeEvaluator(@NonNull TypeEvaluator evaluator) {
        animator.setEvaluator(evaluator);
        return this;
    }

    public void setupEndValues() {
        animator.setupEndValues();
    }

    public void setupStartValues() {
        animator.setupStartValues();
    }

    public void start() {
        if (allowStart) {
            animator.start();
        }
    }

    public void startDelay(long delay) {
        animator.setStartDelay(delay);
        animator.start();
    }

    public boolean isAllowStart() {
        return allowStart;
    }

    public boolean isPause() {
        return animator.isPaused();
    }

    /** It means animator is running if true. **/
    public boolean isRunning() {
        return animator.isRunning();
    }

    /** When animator is called {@link #start} but wait for delay. **/
    public boolean isStarted() {
        return animator.isStarted();
    }

    /** Make animator end in the final state immediately **/
    public void end() {
        if (animator != null) {
            animator.end();
        }
    }

    /**
     * It will be force interrupt animator immediately
     *  so it is impossible to ensure that animator execute fully.
     *  If you don't want to stop with a unknown state, you should call {@link #end()}
     *  to make animation end in the final state;
     */
    public void cancel() {
        if (animator != null) {
            animator.cancel();
        }
    }

    /** Call {@link #cancel} and remove animator from IdeaAnimatorManager **/
    public void release() {
        cancel();
        IdeaAnimatorManager.getInstance().remove(tag);
    }

    private Object shouldBeUsePropertyFactory(Object target, String property) {
        if (!(target instanceof PropertyFactory) && !canSetProperty(target, property)) {
            return new PropertyFactory(target);
        } else {
            return target;
        }
    }

    /**
     * A static method to check whether all of the method of getter and setter do exist.
     * @param obj  A object
     * @param property  Property name
     * @return true:One or all of the method of setter and getter do exist.  false:One or all of the method of getter and setter do not exist
     **/
    public static boolean canSetProperty(Object obj, String property) {
        Class c = obj.getClass();
        Method[] methods = c.getMethods();
        String firstStr = property.substring(0, 1).toUpperCase();
        String name = firstStr + property.substring(1);
        for (Method method1 : methods) {
            if (method1.getName().equals("set" + name)) {
                return true;
            }
        }
        return false;
    }
}
