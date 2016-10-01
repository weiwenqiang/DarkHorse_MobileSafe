package com.wwq.view;

import com.wwq.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 设置中心自定义组合控件
 * @author 魏文强
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_Title;
	private TextView tv_Desc;

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SettingClickView(Context context) {
		super(context);
		initView();
	}
	/**
	 * 初始化布局
	 */
	private void initView() {
		// 将自定义好的布局文件设置给当前的SettingClickView
		View.inflate(getContext(), R.layout.view_setting_click, this);
		tv_Title = (TextView) findViewById(R.id.tv_title);
		tv_Desc = (TextView) findViewById(R.id.tv_desc);
	}

	public void setTitle(String title) {
		tv_Title.setText(title);
	}

	public void setDesc(String desc) {
		tv_Desc.setText(desc);
	}
}
