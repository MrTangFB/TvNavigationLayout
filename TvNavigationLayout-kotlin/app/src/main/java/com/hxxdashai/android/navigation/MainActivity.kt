package com.hxxdashai.android.navigation

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.hxxdashai.android.navigation.widget.NavigationLinearLayout
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Mr.T on 2018/3/29.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNavigationLinearLayout_id.mDataList = arrayListOf("我的电视", "影视", "教育", "游戏", "应用", "动漫", "少儿", "VIP专区")
        mNavigationLinearLayout_id.mNavigationListener = mNavigationListener
        mNavigationLinearLayout_id.mNavigationCursorView = mNavigationCursorView_id
        mNavigationLinearLayout_id.requestFocus()
        mContent.setOnKeyListener { _, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {//此处需要换页面时候的焦点查询
                        mNavigationLinearLayout_id.jumpTo(keyCode)
                    }
                }
            }
            false
        }
    }

    private val mNavigationListener = object : NavigationLinearLayout.NavigationListener {
        override fun onNavigationChange(pos: Int, keyCode: Int) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {//模拟刷新内容区域
                    mContent.text = "我是内容显示区域，当前页面为：$pos，左右切换内容，上键回到导航栏"
                }
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {

                }
                KeyEvent.KEYCODE_MENU -> {//模拟重新编辑刷新了导航栏栏目数据
                    mNavigationLinearLayout_id.mDataList = arrayListOf("分栏1", "分栏2", "分栏3", "分栏4", "分栏5", "分栏6", "分栏7", "分栏8")
                }
            }
        }
    }
}