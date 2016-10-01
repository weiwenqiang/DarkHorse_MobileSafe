package com.wwq.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wwq.utils.ToastUtils;

import java.util.List;

/**
 * Created by 魏文强 on 2016/6/10.
 */
public class KillProcessAllReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //得到手机上面正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcessInfoList){
            //杀死所有的进程
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }
        ToastUtils.showToast(context, "清理完毕");
    }
}
