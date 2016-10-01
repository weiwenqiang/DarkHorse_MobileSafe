package com.wwq.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.util.Log;

/**
 * 服务状态工具类
 *
 * @author 魏文强
 */
public class ServiceStatusUtils {
    /*
     * 检查服务是否在运行
     */
    public static boolean isServiceRunning(Context ctx, String serviceName) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取系统所有正在运行的服务，最多返回100个
        List<RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();//
            Log.d("ServiceStatusUtils", className);
            if (className.equals(serviceName)) {
                return true;//服务存在
            }
        }
        return false;
    }
    //返回进程的总个数
    public static int getProcessCount(Context context) {
//      PackageManager packageManager = getPackageManager();
        //得到进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取到当前手机上面所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        //获取手机上面一共有多少个进程
        return runningAppProcessInfoList.size();
    }
    //获取
    public static long getAvailMem(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取到内存的基本信息，因为是用C写的，所以没有返回值
        activityManager.getMemoryInfo(memoryInfo);
        //获取到剩余内存
        return memoryInfo.availMem;
    }

    public static long getTotalMem(){
        //获取到总内存
//        long totalMem = memoryInfo.totalMem;//高版本API，低版本崩溃
        long totalMem = 0;
        try {
            //读配置文件
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String readLine = reader.readLine();
            StringBuffer sb = new StringBuffer();
            for(char c : readLine.toCharArray()){
                if(c >='0' && c<='9'){
                    sb.append(c);
                }
            }
            totalMem = Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalMem;
    }
}
