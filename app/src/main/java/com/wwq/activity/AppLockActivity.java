package com.wwq.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wwq.fragment.LockFragment;
import com.wwq.fragment.UnLockFragment;
import com.wwq.mobilesafe.R;

/**
 * Created by 魏文强 on 2016/6/11.
 */
public class AppLockActivity extends FragmentActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private UnLockFragment unLockFragment;
    private LockFragment lockFragment;

    private TextView tv_unlock;
    private TextView tv_lock;
    private FrameLayout fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
    }

    private void initUI() {
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        tv_unlock.setOnClickListener(this);
        tv_lock.setOnClickListener(this);
        //获取到fragment的管理者
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        unLockFragment = new UnLockFragment();
        lockFragment = new LockFragment();
        //替换界面，1 需要替换的界面ID，2 具体指定某一个Fragment
        transaction.replace(R.id.fl_content, unLockFragment).commit();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.tv_unlock:
                tv_unlock.setBackgroundResource(R.drawable.tab_right_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_left_default);
                transaction.replace(R.id.fl_content, unLockFragment);
                System.out.println("切换到unLockFragment");
                break;
            case R.id.tv_lock:
                tv_unlock.setBackgroundResource(R.drawable.tab_right_default);
                tv_lock.setBackgroundResource(R.drawable.tab_left_pressed);
                transaction.replace(R.id.fl_content, lockFragment);
                System.out.println("切换到lockFragment");
                break;
        }
        transaction.commit();
    }
}
