package com.fleshkart.app.ui.animation;

/**
 * Abstract Animation class.
 */
public abstract class AbstractAnimation<T> {
    public final T target;

    public AbstractAnimation(T target) {
        this.target = target;
    }
    public abstract void setValues(T start, T end);
    public abstract void applyProgress(float progress);
}