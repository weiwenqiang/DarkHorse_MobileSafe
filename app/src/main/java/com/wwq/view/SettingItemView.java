package com.wwq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wwq.mobilesafe.R;

/**
 * Created by 魏文强 on 2016/4/22.
 */
public class SettingItemView extends RelativeLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private TextView tv_Title;
    private TextView tv_Desc;
    private CheckBox cbStatus;
    private String mDescOff;
    private String mDescOn;
    private String mTitle;
    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle = attrs.getAttributeValue(NAMESPACE, "setting_title");//根据属性名称，获取属性的值
        mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
        initView();
//		int attributeCount = attrs.getAttributeCount();
//		for(int i=0; i<attributeCount; i++){
//			String attributeName = attrs.getAttributeName(i);
//			String attributeValue = attrs.getAttributeValue(i);
//
//			System.out.println(attributeName+"="+attributeValue);
//		}

    }

    public SettingItemView(Context context) {
        super(context);
        initView();
    }
    /**
     * 初始化布局
     */
    private void initView(){
        //将自定义好的布局文件设置给当前的SettingItemView
        View.inflate(getContext(), R.layout.view_setting_item, this);
        tv_Title = (TextView) findViewById(R.id.tv_title);
        tv_Desc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);

        setTitle(mTitle);
    }
    public void setTitle(String title){
        tv_Title.setText(title);
    }
    public void setDesc(String desc){
        tv_Desc.setText(desc);
    }
    /**
     * 判断当前的勾选状态
     * @return
     */
    public boolean isChecked(){
        return cbStatus.isChecked();
    }
    public void setChecked(boolean check){
        cbStatus.setChecked(check);
        //根据选择的状态，更新文本描述
        if(check){
            setDesc(mDescOn);
        }else{
            setDesc(mDescOff);
        }
    }
}
