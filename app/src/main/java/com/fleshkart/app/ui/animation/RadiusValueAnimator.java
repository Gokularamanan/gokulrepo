package com.fleshkart.app.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import com.fleshkart.app.utils.Logger;


/**
 * RadiusValueAnimator for the Radius. Allows for easy cancel
 * @param <T>
 */
public class RadiusValueAnimator<T> extends android.animation.ValueAnimator {

    private static String TAG = "RadiusValueAnimator";


    // User's one and only listener.
    AnimatorListener mListener = null;
    // User's one and only update listener.
    AnimatorUpdateListener mUpdateListener = null;

    String mName;
    AbstractAnimation<T> mAnimation = null;
    T mStart = null;
    T mEnd = null;
    /**
     * InOutAnimator with 0 and 1 as values
     * @param name
     */
    public RadiusValueAnimator(String name, AbstractAnimation<T> obj) {
        super();
        super.setFloatValues(0f, 1f);
        mAnimation = obj;
        mName = name;

        // Only allow 1 listener
        super.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mListener != null) {
                    mListener.onAnimationStart(animation);
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                if (mListener != null) {
                    mListener.onAnimationCancel(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (mListener != null) {
                    mListener.onAnimationRepeat(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onAnimationEnd(animation);
                }

            }
        });

        super.addUpdateListener( new AnimatorUpdateListener() {
                                     @Override
                                     public void onAnimationUpdate(android.animation.ValueAnimator animator) {
                                         mAnimation.applyProgress(animator.getAnimatedFraction());
                                         if (mUpdateListener != null) {
                                             mUpdateListener.onAnimationUpdate(animator);
                                         }
                                     }
                                 }
        );

    }

    /**
     * setListener - set the current animation's one and
     * only user listener. This simplification helps make this animation
     * easily cancellable without the past problems of race condition
     * and states overwriting each other.
     * @param listener
     */
    public RadiusValueAnimator setListener(AnimatorListener listener) {
        mListener = listener;
        return this;
    }

    public RadiusValueAnimator<T> setUpdateListener(AnimatorUpdateListener updateListener) {
        mUpdateListener = updateListener;
        return this;
    }

    /**
     * animateTo a final value
     * @param toValue
     * @param duration
     * @return
     */
    public void animateTo(T toValue, int duration) {
        goTo(toValue, duration).start();
    }

    /**
     * goTo a final value. Uses current value as the start Value
     * @param toValue
     * @param duration
     * @return
     */
    public RadiusValueAnimator goTo(T toValue, int duration) {
        return goTo(mAnimation.target, toValue, duration, null, null);
    }

    /**
     * go from fromValue to toValue. Just set up the animation. Don't start it yet.
     * @param fromValue
     * @param toValue
     * @param duration
     * @param listener
     * @return
     */
    public RadiusValueAnimator goTo(T fromValue, T toValue, int duration, AnimatorUpdateListener updateListener, AnimatorListener listener) {

        if (Logger.DEVELOPMENT) {
            Logger.d(TAG, "goTo from=", fromValue, " to=", toValue, " duration=", duration);
        }
        // Don't overwrite the listeners
        if (listener == null) {
            listener = mListener;
        }
        if (updateListener == null) {
            updateListener = mUpdateListener;
        }

        // Make sure it's stopped before we modify any values
        cancel();

        setDuration(duration);
        mListener = listener;
        mUpdateListener = updateListener;
        mAnimation.setValues(fromValue, toValue);
        return this;
    }

    /**
     * cancel the running animator.
     * Removes the listener
     */
    @Override
    public void cancel() {
        cancel(null);
    }

    /**
     * cancel the running animator. Sets a new
     * end listener. This will prevent any unintended
     * listeners from being run.
     * @param endListener - a new listener to set
     * null to cancel existing.
     * call getOutListener or getInListener to set existing one.
     */
    public void cancel(AnimatorListener endListener) {
        setListener(endListener);
        super.cancel();
    }


    /**
     * finish the current animation
     * And calls the end listener
     */
    public void end() {
        super.end();
        if (mListener != null) {
            mListener.onAnimationEnd(this);
        }
    }

}
