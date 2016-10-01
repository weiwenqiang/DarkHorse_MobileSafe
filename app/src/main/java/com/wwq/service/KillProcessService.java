package com.wwq.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自动清理进程
 * Created by 魏文强 on 2016/6/9.
 */
public class KillProcessService extends Service {
    private LockScreenReceiver receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //锁屏广播
        receiver = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);//锁屏的过滤器
        registerReceiver(receiver, filter);//注册广播

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //写业务逻辑
            }
        };
        //进行定时调度，调那个类，和间隔多长时间
        timer.schedule(timerTask, 0, 1000);
    }

    private class LockScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取到进程管理器
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            //获取到手机上面所有正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses ){
                activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //当应用程序退出的时候，需要把广播反注册掉
        unregisterReceiver(receiver);
        receiver = null;//手动回收
    }
}
