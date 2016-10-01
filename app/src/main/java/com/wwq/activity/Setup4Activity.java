package com.wwq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wwq.mobilesafe.R;

/**
 * Created by 魏文强 on 2016/4/26.
 */
public class Setup4Activity extends BaseSetupActivity {

    private SharedPreferences mPref;
    private CheckBox cbProtect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        cbProtect = (CheckBox) findViewById(R.id.cb_protect);
        //根据sp保存的状态，更新CheckBox
        boolean protect = mPref.getBoolean("protect", false);
        if (protect) {
            cbProtect.setText("防盗保护已经开启");
            cbProtect.setChecked(true);
        } else {
            cbProtect.setText("防盗保护没有开启");
            cbProtect.setChecked(false);
        }
        //当checkbox发生变化时，回调此方法
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbProtect.setText("防盗保护已经开启");
                    mPref.edit().putBoolean("protect", true).commit();
                } else {
                    cbProtect.setText("防盗保护没有开启");
                    mPref.edit().putBoolean("protect", false).commit();
                }
            }
        });
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in,
                R.anim.tran_previous_out);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup4Activity.this, LostFindActivity.class));
        finish();

        mPref.edit().putBoolean("configed", true).commit();
        // 更新sp，表示已经展示过设置向导拉，下次就进来就不展示了
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}

