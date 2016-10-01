package com.wwq.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by 魏文强 on 2016/5/12.
 */
public class LocationService extends Service {

    LocationManager lm;
    MyLocationListener listener;
    private SharedPreferences mPref;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        // 获取系统的定位服务
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        // // 获取所有系统的定位服务
        // List<String> allProviders = lm.getAllProviders();
        // System.out.println(allProviders);

        Criteria criteria = new Criteria();// 标准
        criteria.setCostAllowed(true);// 是否运行付费，比如使用3g网络定位
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 精确度
        String bestProvider = lm.getBestProvider(criteria, true);// 获取最佳位置提供者

        listener = new MyLocationListener();
        // 参1，表示位置提供者，参2表示最短更新时间，参3表示最短更新距离，参4
        // lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
        // listener);
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);//当activity销毁时，停止更新位置，节省电量
    }

    class MyLocationListener implements LocationListener {
        // 位置发生变化的回调
        @Override
        public void onLocationChanged(Location location) {
            // 将获取的经纬度保存
            mPref.edit()
                    .putString(
                            "location",
                            "j:" + location.getLatitude() + "; w:"
                                    + location.getLongitude()).commit();

            stopSelf();//停掉服务,节电
        }

        // 位置提供者状态发生变化的回调
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        // 当用户打开GPS
        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        // 当用户关闭GPS
        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
        }
    }

}
