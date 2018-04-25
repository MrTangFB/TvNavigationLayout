package com.hxxdashai.android.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.hxxdashai.android.navigation.widget.NavigationCursorView;
import com.hxxdashai.android.navigation.widget.NavigationLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.T on 2018/4/25.
 */

public class MainActivity extends Activity {

    private NavigationLinearLayout mNavigationLinearLayout;
    private NavigationCursorView mNavigationCursorView;
    private TextView mContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationLinearLayout = (NavigationLinearLayout) findViewById(R.id.mNavigationLinearLayout_id);
        mNavigationCursorView = (NavigationCursorView) findViewById(R.id.mNavigationCursorView_id);
        mContent = (TextView) findViewById(R.id.mContent);
        List<String> data = new ArrayList<>();
        data.add("我的电视");
        data.add("影视");
        data.add("教育");
        data.add("游戏");
        data.add("应用");
        data.add("动漫");
        data.add("少儿");
        data.add("VIP专区");
        mNavigationLinearLayout.setDataList(data);
        mNavigationLinearLayout.setNavigationListener(mNavigationListener);
        mNavigationLinearLayout.setNavigationCursorView(mNavigationCursorView);
        mNavigationLinearLayout.requestFocus();
        mContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            mNavigationLinearLayout.jumpTo(keyCode);
                    }
                return false;
            }
        });
    }

    private NavigationLinearLayout.NavigationListener mNavigationListener = new NavigationLinearLayout.NavigationListener() {
        @Override
        public void onNavigationChange(int pos, int keyCode) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT: //模拟刷新内容区域
                    mContent.setText("我是内容显示区域，当前页面为：" + pos + "，左右切换内容，上键回到导航栏");
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    break;
                case KeyEvent.KEYCODE_MENU://模拟重新编辑刷新了导航栏栏目数据
                    List<String> data = new ArrayList<>();
                    data.add("分栏1");
                    data.add("分栏2");
                    data.add("分栏3");
                    data.add("分栏4");
                    data.add("分栏5");
                    data.add("分栏6");
                    data.add("分栏7");
                    data.add("分栏8");
                    mNavigationLinearLayout.setDataList(data);
                    break;
            }
        }
    };
}
