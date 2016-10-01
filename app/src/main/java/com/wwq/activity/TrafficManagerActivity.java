package com.wwq.activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

import com.wwq.mobilesafe.R;

/**
 * Created by 魏文强 on 2016/7/21.
 */
public class TrafficManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        //获取到手机的下载的流量
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //获取到手机的上传流量
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
    }
}
