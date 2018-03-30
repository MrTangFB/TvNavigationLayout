package com.hxxdashai.android.navigation.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

/**
 * Created by Mr.T on 2018/3/29.
 */
class NavigationCursorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {

    init {
        visibility = View.INVISIBLE
    }

    private var mDuration = 0L
    private var mLastLocation = 0
    private val set = AnimatorSet()

    /**
     * 光标切换位置时候调用(忽略动画)初始化时候调用
     */
    fun fsatJumpTo(location: Int) {
        mDuration = 1L
        jumpTo(location)
        mDuration = 200L
    }

    /**
     * 光标切换位置时候调用(有动画)
     */
    fun jumpTo(location: Int) {
        val realLocation = location - width / 2
        if (mLastLocation == realLocation) return
        if (set.isRunning) set.cancel()
        createAnimator(realLocation).start()
        mLastLocation = realLocation
    }

    fun createAnimator(location: Int): AnimatorSet {
        val translationX = ObjectAnimator.ofFloat(this, "translationX", mLastLocation.toFloat(), location.toFloat())
        val rotationY = ObjectAnimator.ofFloat(this, "rotationY", 0.0f, if (mLastLocation > location) -180.0f else 180.0f, 0.0f)
        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.2f, 1f)
        set.duration = mDuration
        set.playTogether(translationX, rotationY, scaleX)
        return set
    }
}