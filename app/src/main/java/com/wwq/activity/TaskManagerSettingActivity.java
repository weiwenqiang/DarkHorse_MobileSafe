package com.wwq.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wwq.mobilesafe.R;
import com.wwq.service.KillProcessService;
import com.wwq.utils.ServiceStatusUtils;
import com.wwq.utils.SharedPreferencesUtils;

/**
 * Created by 魏文强 on 2016/6/9.
 */
public class TaskManagerSettingActivity extends Activity {

//    private SharedPreferences sp;

    private CheckBox cb_status;
    private CheckBox cb_kill_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
//        sp = getSharedPreferences("config", 0);//0表示私有的
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ServiceStatusUtils.isServiceRunning(TaskManagerSettingActivity.this, "com.wwq.service.KillProcessService")){
            cb_kill_process.setChecked(true);
        }else{
            cb_kill_process.setChecked(false);
        }
    }

    private void initUI() {
        cb_status = (CheckBox) findViewById(R.id.cb_status);
//        cb_status.setChecked(sp.getBoolean("is_show_system", false));
        cb_status.setChecked(SharedPreferencesUtils.getBoolean(
                TaskManagerSettingActivity.this, "is_show_system", false));
        cb_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor edit = sp.edit();
                SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show_system", isChecked);
//                    edit.putBoolean("is_show_system", true);
//                edit.commit();
            }
        });

        cb_kill_process = (CheckBox) findViewById(R.id.cb_status_kill_process);
        final Intent intent = new Intent(this, KillProcessService.class);
        cb_kill_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });
    }
}
