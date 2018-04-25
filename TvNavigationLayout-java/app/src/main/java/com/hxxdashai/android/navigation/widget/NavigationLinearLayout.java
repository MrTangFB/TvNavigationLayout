package com.hxxdashai.android.navigation.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxxdashai.android.navigation.R;
import com.hxxdashai.android.navigation.util.SoundUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.T on 2018/4/25.
 */

public class NavigationLinearLayout extends LinearLayout {

    public static final int STATE_NO_SELECT = 666;//默认状态
    public static final int STATE_HAS_SELECT_NO_fOCUS = 667;//选中无焦点
    public static final int STATE_HAS_SELECT_HAS_fOCUS = 668;//选中有焦点

    public static final String MODE_SAME = "same";//item固定宽模式
    public static final String MODE_SELF = "self";//item自适应宽模式

    private float fontSize;//字体大小
    private float enlargeRate;//放大倍率
    private int fontColorNormal;//默认字体颜色
    private int fontColorSelect;//选中字体颜色
    private int fontColorLight;//选中字体发光颜色
    private int defaultPos;//默认选中的pos
    private String orderMode;//item排列模式，"same":固定宽模式，"self":自适应宽模式
    private int itemSpace;//"same":item宽度，"self":item距左或右宽度(实际每个item间距是两个itemSpace值)

    private List<String> mDataList = new ArrayList();
    private SparseIntArray mToLeftMap = new SparseIntArray();
    private int mNowPos = -1;
    private NavigationListener mNavigationListener;
    private NavigationCursorView mNavigationCursorView;

    public NavigationLinearLayout(Context context) {
        this(context, null);
    }

    public NavigationLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.home_Navigation);
            fontSize = attributes.getDimension(R.styleable.home_Navigation_home_FontSize, 30.0F);
            enlargeRate = attributes.getFloat(R.styleable.home_Navigation_home_EnlargeRate, 1.1F);
            fontColorNormal = attributes.getColor(R.styleable.home_Navigation_home_FontColorNormal, Color.WHITE);
            fontColorSelect = attributes.getColor(R.styleable.home_Navigation_home_FontColorSelect, Color.BLUE);
            fontColorLight = attributes.getColor(R.styleable.home_Navigation_home_FontColorLight, Color.RED);
            defaultPos = attributes.getInteger(R.styleable.home_Navigation_home_DefaultPos, 0);
            String mode = attributes.getString(R.styleable.home_Navigation_home_OrderMode);
            if (TextUtils.isEmpty(mode)) mode = "self";
            orderMode = mode;
            itemSpace = attributes.getDimensionPixelSize(R.styleable.home_Navigation_home_ItemSpace, 10);
            attributes.recycle();
        }
        setFocusable(true);
    }

    /**
     * 设置数据，数据改变后需重新调用(导航栏编辑等功能)
     */
    public void setDataList(List data) {
        mDataList = data;
        initView();
    }

    /**
     * 监听导航事件
     */
    public void setNavigationListener(NavigationListener listener) {
        mNavigationListener = listener;
        if (mNavigationListener != null) {
            mNavigationListener.onNavigationChange(mNowPos, KeyEvent.KEYCODE_DPAD_LEFT);
        }
    }

    /**
     * 导航光标
     */
    public void setNavigationCursorView(NavigationCursorView view) {
        mNavigationCursorView = view;
        int left = mToLeftMap.get(mNowPos);
        if (left != 0 && mNavigationCursorView != null) {
            mNavigationCursorView.fsatJumpTo(left);
        }
    }

    private void initView() {
        if (mToLeftMap.size() != 0) {
            mToLeftMap.clear();
        }
        if (mDataList.size() > getChildCount()) {
            do {
                addView(getItemView());
            } while (mDataList.size() > getChildCount());
        } else if (mDataList.size() < getChildCount()) {
            do {
                removeViewAt(getChildCount() - 1);
            } while (mDataList.size() < getChildCount());
        }
        if (mNowPos != -1 && mNowPos < getChildCount()) {
            changeItemState(mNowPos, STATE_NO_SELECT);
        }
        for (int i = 0; i < getChildCount() - 1; i++) {
            final int finalI = i;
            final TextView child = (TextView) getChildAt(i);
            child.setText(mDataList.get(i));
            child.getViewTreeObserver().addOnGlobalLayoutListener((new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    child.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int left = child.getWidth() / 2 + child.getLeft() + getLeft();//每个item中点到父布局左边的距离
                    mToLeftMap.put(finalI, left);
                    if (defaultPos == finalI) {
                        mNowPos = defaultPos;
                        changeItemState(mNowPos, isFocused() ? STATE_HAS_SELECT_HAS_fOCUS : STATE_HAS_SELECT_NO_fOCUS);
                        if (mNavigationCursorView != null) {
                            mNavigationCursorView.fsatJumpTo(left);
                        }
                        if (mNavigationListener != null) {
                            mNavigationListener.onNavigationChange(mNowPos, KeyEvent.KEYCODE_DPAD_LEFT);
                        }
                    }
                }
            }));
        }
    }

    private void changeItemState(int pos, int state) {
        View child = getChildAt(pos);
        if (child != null) {
            switch (state) {
                case STATE_NO_SELECT:
                    //if (child.scaleX != 1f)//TODO BUG
                    ViewCompat.animate(child).scaleX(1.0F).scaleY(1.0F).translationZ(0.0F).start();
                    ((TextView) child).setShadowLayer(0.0F, 0.0F, 0.0F, fontColorLight);
                    child.setSelected(false);
                    break;
                case STATE_HAS_SELECT_NO_fOCUS:
                    if (child.getScaleX() != 1.0F)
                        ViewCompat.animate(child).scaleX(1.0F).scaleY(1.0F).translationZ(0.0F).start();
                    if (!child.isSelected()) {
                        ((TextView) child).setShadowLayer(25.0F, 0.0F, 0.0F, fontColorLight);
                        child.setSelected(true);
                    }
                    break;
                case STATE_HAS_SELECT_HAS_fOCUS:
                    ViewCompat.animate(child).scaleX(enlargeRate).scaleY(enlargeRate).translationZ(0.0F).start();
                    if (!child.isSelected()) {
                        ((TextView) child).setShadowLayer(25.0F, 0.0F, 0.0F, fontColorLight);
                        child.setSelected(true);
                    }
                    break;
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mNowPos > 0) {
                        changeItemState(mNowPos, STATE_NO_SELECT);
                        mNowPos--;
                        changeItemState(mNowPos, STATE_HAS_SELECT_HAS_fOCUS);
                        int left = mToLeftMap.get(mNowPos);
                        if (left != 0 && mNavigationCursorView != null)
                            mNavigationCursorView.jumpTo(left);
                        if (mNavigationListener != null) {
                            mNavigationListener.onNavigationChange(mNowPos, keyCode);
                        }
                    }
                    SoundUtil.playClickSound(this);
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mNowPos < getChildCount() - 1) {
                        changeItemState(mNowPos, STATE_NO_SELECT);
                        mNowPos++;
                        changeItemState(mNowPos, STATE_HAS_SELECT_HAS_fOCUS);
                        int left = mToLeftMap.get(mNowPos);
                        if (left != 0 && mNavigationCursorView != null)
                            mNavigationCursorView.jumpTo(left);
                        if (mNavigationListener != null) {
                            mNavigationListener.onNavigationChange(mNowPos, keyCode);
                        }
                    }
                    SoundUtil.playClickSound(this);
                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mNavigationListener != null) {
                        mNavigationListener.onNavigationChange(mNowPos, keyCode);
                    }
                    break;
                case KeyEvent.KEYCODE_MENU:
                    if (mNavigationListener != null) {
                        mNavigationListener.onNavigationChange(mNowPos, keyCode);
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        changeItemState(mNowPos, gainFocus ? STATE_HAS_SELECT_HAS_fOCUS : STATE_HAS_SELECT_NO_fOCUS);
        if (mNavigationCursorView != null)
            mNavigationCursorView.setVisibility(gainFocus ? View.VISIBLE : View.INVISIBLE);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    private TextView getItemView() {
        int[][] states = {{android.R.attr.state_selected}, new int[0]};
        int[] colors = new int[]{fontColorSelect, fontColorNormal};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        TextView textView = new TextView(getContext());
        textView.setTextSize(fontSize);
        textView.setTextColor(colorStateList);
        textView.setIncludeFontPadding(false);
        switch (orderMode) {
            case MODE_SAME:
                LayoutParams layoutParams = new LayoutParams(itemSpace, LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER);
                break;
            case MODE_SELF:
                textView.setPadding(itemSpace, 0, itemSpace, 0);
        }
        return textView;
    }

    public void jumpTo(int keyCode) {
        if (mNowPos > 0 && keyCode == KeyEvent.KEYCODE_DPAD_LEFT || mNowPos < getChildCount() - 1 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            changeItemState(mNowPos, STATE_NO_SELECT);
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                mNowPos--;
            else
                mNowPos++;
            changeItemState(mNowPos, STATE_HAS_SELECT_NO_fOCUS);
            int left = mToLeftMap.get(mNowPos);
            if (left != 0 && mNavigationCursorView != null)
                mNavigationCursorView.jumpTo(left);
            if (mNavigationListener != null) {
                mNavigationListener.onNavigationChange(mNowPos, keyCode);
            }
        }
    }

    public interface NavigationListener {

        void onNavigationChange(int pos, int keyCode);
    }
}