package com.wwq.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.wwq.mobilesafe.R;
import com.wwq.service.AddressService;
import com.wwq.service.CallSafeService;
import com.wwq.service.WatchDogService;
import com.wwq.utils.ServiceStatusUtils;
import com.wwq.view.SettingClickView;
import com.wwq.view.SettingItemView;

/**
 * Created by 魏文强 on 2016/4/20.
 */
public class SettingActivity extends Activity {


    private SettingItemView sivUpdate;// 设置升级
    private SettingItemView sivAddress;// 设置归属地
    private SettingClickView scvAddressStyle;// 修改提示框风格
    private SettingClickView scvAddressLocation;// 修改归属地位置
    private SettingItemView sivCallsafe;//黑名单功能
    private SettingItemView sivWatchDog;//看门狗功能
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        initUpdateView();
        initAddressView();
        initAddressStyle();
        initAddressLocation();
        initBlackView();
        initWatchDogView();
    }

    /**
     * 初始化自动更新开关
     */
    private void initUpdateView() {
        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
        // sivUpdate.setTitle("自动更新设置");
        // sivUpdate.setDesc("自动更新已开启");
        boolean autoUpdate = mPref.getBoolean("auto_update", true);
        if (autoUpdate) {
            // sivUpdate.setDesc("自动更新已开启");
            sivUpdate.setChecked(true);
        } else {
            // sivUpdate.setDesc("自动更新已关闭");
            sivUpdate.setChecked(false);
        }
        sivUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sivUpdate.isChecked()) {
                    // 设置不勾选
                    sivUpdate.setChecked(false);
                    // sivUpdate.setDesc("自动更新已关闭");

                    mPref.edit().putBoolean("auto_update", false).commit();
                } else {
                    sivUpdate.setChecked(true);
                    // sivUpdate.setDesc("自动更新已开启");

                    mPref.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /**
     * 初始化归属地开关
     */
    private void initAddressView() {
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);
        //根据归属地服务是否运行来更新选择框
        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.wwq.service.AddressService");
        if (serviceRunning) {
            sivAddress.setChecked(true);
        } else {
            sivAddress.setChecked(false);
        }

        sivAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sivAddress.isChecked()) {
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this,
                            AddressService.class));// 停止归属地服务
                } else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this,
                            AddressService.class));// 开启归属地服务
                }
            }
        });
    }

    /**
     * 修改显示框的风格
     */
    private void initAddressStyle() {
        scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);

        scvAddressStyle.setTitle("归属地提示框风格");
        int style = mPref.getInt("address_style", 0);
        scvAddressStyle.setDesc(items[style]);
        scvAddressStyle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSingleChooseDailog();
            }
        });
    }

    /**
     * 弹出选择风格的单选框
     */
    final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

    protected void showSingleChooseDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("归属地提示框风格");

        int style = mPref.getInt("address_style", 0);


        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt("address_style", which).commit();//保存选择的风格
                dialog.dismiss();//让dialog消失

                scvAddressStyle.setDesc(items[which]);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 修改归属地
     */
    private void initAddressLocation() {
        scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");
        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
            }
        });
    }

    /**
     * 初始化黑名单功能
     */
    private void initBlackView() {
        sivCallsafe = (SettingItemView) findViewById(R.id.siv_callsafe);
        //根据归属地服务是否运行来更新选择框
        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.wwq.service.CallSafeService");
        if (serviceRunning) {
            sivCallsafe.setChecked(true);
        } else {
            sivCallsafe.setChecked(false);
        }

        sivCallsafe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sivCallsafe.isChecked()) {
                    sivCallsafe.setChecked(false);
                    stopService(new Intent(SettingActivity.this,
                            CallSafeService.class));// 停止归属地服务
                } else {
                    sivCallsafe.setChecked(true);
                    startService(new Intent(SettingActivity.this,
                            CallSafeService.class));// 开启归属地服务
                }
            }
        });
    }
    /**
     * 初始化黑名单功能
     */
    private void initWatchDogView() {
        sivWatchDog = (SettingItemView) findViewById(R.id.siv_watch_dog);
        //根据归属地服务是否运行来更新选择框
        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.wwq.service.WatchDogService");
        if (serviceRunning) {
            sivWatchDog.setChecked(true);
        } else {
            sivWatchDog.setChecked(false);
        }

        sivWatchDog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sivWatchDog.isChecked()) {
                    sivWatchDog.setChecked(false);
                    stopService(new Intent(SettingActivity.this,
                            WatchDogService.class));// 停止归属地服务
                } else {
                    sivWatchDog.setChecked(true);
                    startService(new Intent(SettingActivity.this,
                            WatchDogService.class));// 开启归属地服务
                }
            }
        });
    }
}
