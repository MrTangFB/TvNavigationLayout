package com.hxxdashai.android.navigation.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import com.hxxdashai.android.navigation.R
import com.hxxdashai.android.navigation.util.SoundUtil

/**
 * Created by Mr.T on 2018/3/27.
 */
class NavigationLinearLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        const val STATE_NO_SELECT = 666//默认状态
        const val STATE_HAS_SELECT_NO_fOCUS = 667//选中无焦点
        const val STATE_HAS_SELECT_HAS_fOCUS = 668//选中有焦点

        const val MODE_SAME = "same"//item固定宽模式
        const val MODE_SELF = "self"//item自适应宽模式
    }

    private var fontSize: Float = 0.0F//字体大小
    private var enlargeRate: Float = 0.0f//放大倍率
    private var fontColorNormal: Int = 0//默认字体颜色
    private var fontColorSelect: Int = 0//选中字体颜色
    private var fontColorLight: Int = 0//选中字体发光颜色
    private var defaultPos: Int = 0//默认选中的pos
    private var orderMode: String = ""//item排列模式，"same":固定宽模式，"self":自适应宽模式
    private var itemSpace: Int = 0//"same":item宽度，"self":item距左或右宽度(实际每个item间距是两个itemSpace值)

    init {
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.home_Navigation)
            fontSize = attributes.getDimension(R.styleable.home_Navigation_home_FontSize, 30f)
            enlargeRate = attributes.getFloat(R.styleable.home_Navigation_home_EnlargeRate, 1.1f)
            fontColorNormal = attributes.getColor(R.styleable.home_Navigation_home_FontColorNormal, Color.WHITE)
            fontColorSelect = attributes.getColor(R.styleable.home_Navigation_home_FontColorSelect, Color.BLUE)
            fontColorLight = attributes.getColor(R.styleable.home_Navigation_home_FontColorLight, Color.RED)
            defaultPos = attributes.getInteger(R.styleable.home_Navigation_home_DefaultPos, 0)
            orderMode = attributes.getString(R.styleable.home_Navigation_home_OrderMode) ?: MODE_SELF
            itemSpace = attributes.getDimensionPixelSize(R.styleable.home_Navigation_home_ItemSpace, 10)
            attributes.recycle()
        }
        isFocusable = true
    }

    /**
     * 设置数据，数据改变后需重新调用(导航栏编辑等功能)
     */
    var mDataList: MutableList<String> = ArrayList()
        set(value) {
            field = value
            initView()
        }
    private var mToLeftMap: MutableMap<Int, Int> = HashMap()//每个item中点到父布局左边的距离
    var mNowPos: Int = -1
    /**
     * 监听导航事件
     */
    var mNavigationListener: NavigationListener? = null
        set(value) {
            field = value
            field?.onNavigationChange(mNowPos, KeyEvent.KEYCODE_DPAD_LEFT)
        }
    /**
     * 导航光标
     */
    var mNavigationCursorView: NavigationCursorView? = null
        set(value) {
            field = value
            mToLeftMap[mNowPos]?.let { field?.fsatJumpTo(it) }
        }

    private fun initView() {
        if (mToLeftMap.isNotEmpty()) mToLeftMap.clear()//还原状态
        if (mDataList.size > childCount) {
            do {
                addView(getItemView())
            } while (mDataList.size > childCount)
        } else if (mDataList.size < childCount) {
            do {
                removeViewAt(childCount - 1)
            } while (mDataList.size < childCount)
        }
        if (mNowPos != -1 && mNowPos < childCount) changeItemState(mNowPos, STATE_NO_SELECT)//还原状态
        for (i in 0..(childCount - 1)) {
            val child = getChildAt(i) as TextView
            child.text = mDataList[i]
            child.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    child.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mToLeftMap[i] = child.width / 2 + child.left + this@NavigationLinearLayout.left//每个item中点到父布局左边的距离
                    if (defaultPos == i) {//TODO 如果编辑导航后不要重置pos，可根据实际修改逻辑
                        mNowPos = defaultPos//默认要展示的pos
                        changeItemState(mNowPos, if (this@NavigationLinearLayout.isFocused) STATE_HAS_SELECT_HAS_fOCUS else STATE_HAS_SELECT_NO_fOCUS)//修改默认要展示的pos的状态
                        mToLeftMap[mNowPos]?.let { mNavigationCursorView?.fsatJumpTo(it) }//移动光标
                        mNavigationListener?.onNavigationChange(mNowPos, KeyEvent.KEYCODE_DPAD_LEFT)//展示内容数据，仅仅展示数据，写左右都没问题
                    }
                }
            })
        }
    }

    private fun changeItemState(pos: Int, state: Int) {
        val child = getChildAt(pos)
        if (child != null)
            when (state) {
                STATE_NO_SELECT -> {
                    //if (child.scaleX != 1f) ViewCompat.animate(child).scaleX(1f).scaleY(1f).translationZ(0f).start()//TODO BUG
                    ViewCompat.animate(child).scaleX(1f).scaleY(1f).translationZ(0f).start()
                    (child as TextView).setShadowLayer(0f, 0f, 0f, fontColorLight)
                    child.isSelected = false
                }
                STATE_HAS_SELECT_NO_fOCUS -> {
                    if (child.scaleX != 1f) ViewCompat.animate(child).scaleX(1f).scaleY(1f).translationZ(0f).start()
                    if (!child.isSelected) {
                        (child as TextView).setShadowLayer(25f, 0f, 0f, fontColorLight)
                        child.isSelected = true
                    }
                }
                STATE_HAS_SELECT_HAS_fOCUS -> {
                    ViewCompat.animate(child).scaleX(enlargeRate).scaleY(enlargeRate).translationZ(0f).start()
                    if (!child.isSelected) {
                        (child as TextView).setShadowLayer(25f, 0f, 0f, fontColorLight)
                        child.isSelected = true
                    }
                }
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (mNowPos > 0) {
                        changeItemState(mNowPos, STATE_NO_SELECT)
                        changeItemState(--mNowPos, STATE_HAS_SELECT_HAS_fOCUS)
                        mToLeftMap[mNowPos]?.let { mNavigationCursorView?.jumpTo(it) }
                        mNavigationListener?.onNavigationChange(mNowPos, keyCode)
                    }//如果有跳出导航栏的左右事件需求可在次此处else回调出去
                    SoundUtil.playClickSound(this@NavigationLinearLayout)
                    return true//TODO 系统声音会被屏蔽掉
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    if (mNowPos < childCount - 1) {
                        changeItemState(mNowPos, STATE_NO_SELECT)
                        changeItemState(++mNowPos, STATE_HAS_SELECT_HAS_fOCUS)
                        mToLeftMap[mNowPos]?.let { mNavigationCursorView?.jumpTo(it) }
                        mNavigationListener?.onNavigationChange(mNowPos, keyCode)
                    }
                    SoundUtil.playClickSound(this@NavigationLinearLayout)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {//TODO 方向类型的事件，不想系统自动找焦点，可试试return true
                    mNavigationListener?.onNavigationChange(mNowPos, keyCode)
                }
                KeyEvent.KEYCODE_MENU -> {//TODO 非方向类型事件
                    mNavigationListener?.onNavigationChange(mNowPos, keyCode)
                    return true//TODO bug
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        changeItemState(mNowPos, if (gainFocus) STATE_HAS_SELECT_HAS_fOCUS else STATE_HAS_SELECT_NO_fOCUS)
        mNavigationCursorView?.visibility = if (gainFocus) View.VISIBLE else View.INVISIBLE
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    private fun getItemView(): TextView {
        val states = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
        val colors = intArrayOf(fontColorSelect, fontColorNormal)
        val colorStateList = ColorStateList(states, colors)
        val textView = TextView(context)
        textView.textSize = fontSize
        textView.setTextColor(colorStateList)
        textView.includeFontPadding = false
        when (orderMode) {
            MODE_SAME -> {
                val layoutParams = LayoutParams(itemSpace, LayoutParams.WRAP_CONTENT)
                textView.layoutParams = layoutParams
                textView.gravity = Gravity.CENTER
            }
            MODE_SELF -> {
                textView.setPadding(itemSpace, 0, itemSpace, 0)
            }
        }
        return textView
    }

    /**
     * 导航栏目被动切换时调用
     */
    fun jumpTo(keyCode: Int) {
        if ((mNowPos > 0 && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) || (mNowPos < childCount - 1 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            changeItemState(mNowPos, STATE_NO_SELECT)
            changeItemState(if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) --mNowPos else ++mNowPos, STATE_HAS_SELECT_NO_fOCUS)
            mToLeftMap[mNowPos]?.let { mNavigationCursorView?.jumpTo(it) }
            mNavigationListener?.onNavigationChange(mNowPos, KeyEvent.KEYCODE_DPAD_LEFT)//仅仅展示数据，写左右都没问题
        }
    }

    interface NavigationListener {

        /**
         * @param pos     选中的序号
         * @param keyCode 点击的按键
         */
        fun onNavigationChange(pos: Int, keyCode: Int)
    }
}