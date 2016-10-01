package com.wwq.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.wwq.entity.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏文强 on 2016/5/22.
 */
public class AppInfos {


    public static List<AppInfo> getAppInfos(Context context) {
        List<AppInfo> packageAppInfos = new ArrayList<AppInfo>();
        //获取到包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获取到安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo installedPackage : installedPackages) {
            AppInfo appInfo = new AppInfo();
            //获取到应用程序的图标
            Drawable drawable = installedPackage.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);
            //获取到应用程序的名字
            String apkName = installedPackage.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);
            //获取到应用程序的包名
            String packageNamee = installedPackage.packageName;
            appInfo.setApkPackName(packageNamee);
            //获取到apk资源的路径
            String sourceDir = installedPackage.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //apk的长度，就是大小
            long apkSize = file.length();
            appInfo.setApkSize(apkSize);
            //  data/app    system/app
            //获取到安装应用程序的标记
            int flags = installedPackage.applicationInfo.flags;
            if((flags & ApplicationInfo.FLAG_SYSTEM) != 0){//用二进制判断
                appInfo.setUserApp(false);//表示系统app
            }else{
                appInfo.setUserApp(true);//表示用户app
            }
            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                appInfo.setIsRom(false);//表示在SD卡
            }else{
                appInfo.setIsRom(true);//表示内存
            }

            packageAppInfos.add(appInfo);
        }
        return packageAppInfos;
    }
}
