package com.wwq.activity;

import com.wwq.db.AddressDao;
import com.wwq.mobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 归属地查询页面
 * @author 魏文强
 */
public class AddressActivity extends Activity {
	private EditText etNumber;
	private TextView tvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);

		etNumber = (EditText) findViewById(R.id.et_number);
		tvResult = (TextView) findViewById(R.id.tv_result);
		// 监听EditText的变化
		etNumber.addTextChangedListener(new TextWatcher() {
			// 文字发生变化时的回调
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String address = AddressDao.getAddress(s.toString());
				tvResult.setText(address);
			}

			// 文字变化前的回调
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			// 文字变化结束之后的回调
			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	public void query(View view) {
		String number = etNumber.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			String address = AddressDao.getAddress(number);
			tvResult.setText(address);
		} else {
			// 插补器
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

			/*
			 * 自定义插补器重写方法 shake.setInterpolator(new Interpolator() {
			 * 
			 * @Override public float getInterpolation(float x) { //公式 float
			 * y=(float)(Math.cos((x + 1) * Math.PI) / 2.0f) + 0.5f; return y; }
			 * });
			 */
			etNumber.startAnimation(shake);
			vibrate();
		}
	}

	private void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		// vibrator.vibrate(2000);震动时间
		vibrator.vibrate(new long[] { 1000, 2000, 1000, 3000 }, -1);// -1只执行一次，不循环,0从头开始循环,从数组第几个值开始循环

//		vibrator.cancel();取消震动
	}
}
