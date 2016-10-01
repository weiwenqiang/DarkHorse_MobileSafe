package com.wwq.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wwq.mobilesafe.R;
import com.wwq.utils.ToastUtils;

/**
 * Created by 魏文强 on 2016/7/4.
 */
public class EnterPwdActivity extends Activity implements View.OnClickListener {
    private EditText et_pwd;
    private Button bt_0;
    private Button bt_1;
    private Button bt_2;
    private Button bt_3;
    private Button bt_4;
    private Button bt_5;
    private Button bt_6;
    private Button bt_7;
    private Button bt_8;
    private Button bt_9;
    private Button bt_clean_all;
    private Button bt_delete;
    private Button btn_ok;

    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        getPassParam();
        initUI();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 当用户输入后退健 的时候。我们进入到桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    private void getPassParam(){
        Intent intent = getIntent();
        if(intent != null){
            packageName = intent.getStringExtra("packageName");
        }
    }
    private void initUI() {
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd.setInputType(InputType.TYPE_NULL);
        bt_0 = (Button) findViewById(R.id.bt_0);
        bt_1 = (Button) findViewById(R.id.bt_1);
        bt_2 = (Button) findViewById(R.id.bt_2);
        bt_3 = (Button) findViewById(R.id.bt_3);
        bt_4 = (Button) findViewById(R.id.bt_4);
        bt_5 = (Button) findViewById(R.id.bt_5);
        bt_6 = (Button) findViewById(R.id.bt_6);
        bt_7 = (Button) findViewById(R.id.bt_7);
        bt_8 = (Button) findViewById(R.id.bt_8);
        bt_9 = (Button) findViewById(R.id.bt_9);
        bt_clean_all = (Button) findViewById(R.id.bt_clean_all);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        bt_0.setOnClickListener(this);
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        bt_4.setOnClickListener(this);
        bt_5.setOnClickListener(this);
        bt_6.setOnClickListener(this);
        bt_7.setOnClickListener(this);
        bt_8.setOnClickListener(this);
        bt_9.setOnClickListener(this);
        bt_clean_all.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String str = et_pwd.getText().toString();
        switch (v.getId()) {
            case R.id.bt_0:
                et_pwd.setText(str + "0");
                break;
            case R.id.bt_1:
                et_pwd.setText(str + "1");
                break;
            case R.id.bt_2:
                et_pwd.setText(str + "2");
                break;
            case R.id.bt_3:
                et_pwd.setText(str + "3");
                break;
            case R.id.bt_4:
                et_pwd.setText(str + "4");
                break;
            case R.id.bt_5:
                et_pwd.setText(str + "5");
                break;
            case R.id.bt_6:
                et_pwd.setText(str + "6");
                break;
            case R.id.bt_7:
                et_pwd.setText(str + "7");
                break;
            case R.id.bt_8:
                et_pwd.setText(str + "8");
                break;
            case R.id.bt_9:
                et_pwd.setText(str + "9");
                break;
            case R.id.bt_clean_all:
                et_pwd.setText("");
                break;
            case R.id.bt_delete:
                if(str.length()>0){
                    et_pwd.setText(str.substring(0, str.length() - 1));
                }
                break;
            case R.id.btn_ok:
                if(str.equals("123")){
                    Intent intent = new Intent();
                    // 发送广播。停止保护
                    intent.setAction("com.wwq.watchdog.stopprotect");
                    // 跟狗说。现在停止保护短信
                    intent.putExtra("packageName", packageName);

                    sendBroadcast(intent);

                    finish();
                }else{
                    ToastUtils.showToast(EnterPwdActivity.this, "密码错误!");
                }
                break;
        }
    }
}
