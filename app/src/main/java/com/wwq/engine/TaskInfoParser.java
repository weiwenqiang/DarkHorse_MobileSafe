package com.wwq.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;

import com.wwq.entity.TaskInfo;
import com.wwq.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏文强 on 2016/6/6.
 */
public class TaskInfoParser {
    public static List<TaskInfo> getTaskInfos(Context context) {
        //获取到包管理器
        PackageManager packageManager = context.getPackageManager();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        //获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取到手机上面所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcessInfos) {
            TaskInfo taskInfo = new TaskInfo();
            //获取到进程的名字
            String processName = runningAppProcessInfo.processName;
            taskInfo.setPackageName(processName);
            try {
                //获取到内存基本信息
                Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
                //Dirty 弄脏，获取到总共弄脏多少内存（当前应用程序占用多少内存）
                int totalPrivateDirty = memoryInfos[0].getTotalPrivateDirty() * 1024;
                taskInfo.setMemorySize(totalPrivateDirty);
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()+packageInfo.applicationInfo.uid;
                taskInfo.setAppName(appName);

                //获取到当前应用程序的标记
                int flags = packageInfo.applicationInfo.flags;//比如答案
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {//比如改卷器
                    taskInfo.setUserApp(false);//系统应用
                } else {
                    taskInfo.setUserApp(true);//用户引用
                }
            } catch (Exception e) {
                e.printStackTrace();
                //系统核心库是C写的，里面有些系统没有图标，必须给一个默认的图标
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setAppName("系统应用");
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
