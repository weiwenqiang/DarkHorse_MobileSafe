package com.wwq.activity;

import android.content.Intent;
import android.os.Bundle;

import com.wwq.mobilesafe.R;

/**
 * Created by 魏文强 on 2016/4/22.
 */
public class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPreviousPage() {

    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup1Activity.this, Setup2Activity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
