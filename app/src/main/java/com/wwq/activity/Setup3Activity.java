package com.wwq.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.wwq.mobilesafe.R;
import com.wwq.utils.ToastUtils;

/**
 * Created by 魏文强 on 2016/4/22.
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText etPhone;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        etPhone = (EditText) findViewById(R.id.et_phone);
        String phone = mPref.getString("safe_phone", "");
        etPhone.setText(phone);
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(Setup3Activity.this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in,
                R.anim.tran_previous_out);
    }

    @Override
    public void showNextPage() {
        String phone = etPhone.getText().toString().trim();//过滤空格
        if (TextUtils.isEmpty(phone)) {
//			Toast.makeText(this, "安全号码不能为空！", 0).show();
            ToastUtils.showToast(this, "安全号码不能为空！");
            return;
        }
        mPref.edit().putString("safe_phone", phone).commit();//保存安全号码

        startActivity(new Intent(Setup3Activity.this, Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    public void selectContact(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String phone = data.getStringExtra("phone");
            phone = phone.replaceAll("-", "").replaceAll(" ", "");
            etPhone.setText(phone);//把电话号码设置给输入框
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
