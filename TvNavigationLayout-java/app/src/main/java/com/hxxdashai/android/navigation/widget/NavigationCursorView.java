package com.hxxdashai.android.navigation.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Mr.T on 2018/4/25.
 */

public class NavigationCursorView extends ImageView {

    private long mDuration = 0L;
    private int mLastLocation = 0;
    private final AnimatorSet set = new AnimatorSet();

    public NavigationCursorView(Context context) {
        this(context, null);
    }

    public NavigationCursorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationCursorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVisibility(View.INVISIBLE);
    }

    public final void fsatJumpTo(int location) {
        mDuration = 1L;
        jumpTo(location);
        mDuration = 200L;
    }

    public final void jumpTo(int location) {
        int realLocation = location - getWidth() / 2;
        if (mLastLocation == realLocation) return;
        if (set.isRunning()) set.cancel();
        createAnimator(realLocation).start();
        mLastLocation = realLocation;
    }

    public final AnimatorSet createAnimator(int location) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(this, "translationX", (float) mLastLocation, (float) location);
        ObjectAnimator rotationY = ObjectAnimator.ofFloat(this, "rotationY", 0.0F, mLastLocation > location ? -180.0F : 180.0F, 0.0F);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.0F, 0.2F, 1.0F);
        set.setDuration(mDuration);
        set.playTogether(translationX, rotationY, scaleX);
        return set;
    }
}