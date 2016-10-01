package com.wwq.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by 魏文强 on 2016/4/22.
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector mDectector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDectector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            // 监听手势滑动事件
            // 滑动起始点，终点，x水平速度，y垂直速度
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 判断纵向滑动幅度是否过大，过大的话不允许切换界面
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    Toast.makeText(BaseSetupActivity.this, "不能这样划欧",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (Math.abs(velocityX) < 100) {
                    Toast.makeText(BaseSetupActivity.this, "滑动的太慢",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                // 向右划，上一页
                if (e2.getRawX() - e1.getRawX() > 200) {
                    showPreviousPage();
                    return true;
                }
                // 向左划，下一页
                if (e1.getRawX() - e2.getRawX() > 200) {
                    showNextPage();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public void next(View view) {
        showNextPage();
    }

    public void previous(View view) {
        showPreviousPage();
    }

    // 子类必须实现
    public abstract void showPreviousPage();

    public abstract void showNextPage();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDectector.onTouchEvent(event);// 委托手势识别器处理触摸事件
        return super.onTouchEvent(event);
    }
}
