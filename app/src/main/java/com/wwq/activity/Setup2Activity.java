package com.wwq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.wwq.mobilesafe.R;
import com.wwq.utils.ToastUtils;
import com.wwq.view.SettingItemView;

/**
 * Created by 魏文强 on 2016/4/22.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SharedPreferences mPref;
    private SettingItemView sivSim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivSim = (SettingItemView) findViewById(R.id.siv_sim);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        String sim = mPref.getString("sim", null);
        if(!TextUtils.isEmpty(sim)){
            sivSim.setChecked(true);
        }else{
            sivSim.setChecked(false);
        }

        sivSim.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(sivSim.isChecked()){
                    sivSim.setChecked(false);
                    mPref.edit().remove("sim").commit();//删除绑定的sim卡
                }else{
                    sivSim.setChecked(true);
                    //保存sim卡信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();//获取sim卡序列号
                    System.out.println("sim卡序列号:"+simSerialNumber);

                    mPref.edit().putString("sim", simSerialNumber).commit();//将sim卡序列号，保存在sp中
                }
            }
        });
    }
    public void showPreviousPage(){
        startActivity(new Intent(Setup2Activity.this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }
    public void showNextPage(){
        //如果SIM卡没有绑定，就不允许进入下一个页面
        String sim = mPref.getString("sim", null);
        if(TextUtils.isEmpty(sim)){
            ToastUtils.showToast(this, "必须绑定SIM卡!");
            return;
        }
        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
