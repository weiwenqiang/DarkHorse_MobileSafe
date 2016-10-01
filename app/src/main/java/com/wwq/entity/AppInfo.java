package com.wwq.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by 魏文强 on 2016/5/22.
 */
public class AppInfo {
    private Drawable icon;//程序的图标
    private String apkName;//程序的名字
    private long apkSize;//程序的大小
    private boolean userApp;//是否是用户app还是系统app
    private boolean isRom;//放置的位置
    private String apkPackName;//包的名字

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getApkPackName() {
        return apkPackName;
    }

    public void setApkPackName(String apkPackName) {
        this.apkPackName = apkPackName;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", apkName='" + apkName + '\'' +
                ", apkSize=" + apkSize +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", apkPackName='" + apkPackName + '\'' +
                '}';
    }
}
