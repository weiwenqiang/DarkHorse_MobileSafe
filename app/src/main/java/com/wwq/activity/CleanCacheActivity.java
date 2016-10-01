package com.wwq.activity;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wwq.mobilesafe.R;
import com.wwq.utils.UIUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏文强 on 2016/7/8.
 */
public class CleanCacheActivity extends Activity {
    private PackageManager packageManager;
    private List<CacheInfo> cacheLists;
    private ListView list_view;
    private FrameLayout lyt_lading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cachee);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        lyt_lading.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                //安装到手机上面所有的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo : installedPackages) {
                    getCacheSize(packageInfo);
                }
                handler.sendEmptyMessage(0);
            }
        };
        thread.start();
    }

    private void initUI() {
        lyt_lading = (FrameLayout) findViewById(R.id.lyt_lading);
        packageManager = getPackageManager();

        cacheLists = new ArrayList<CacheInfo>();
        list_view = (ListView) findViewById(R.id.list_view);
        //第一个参数接收一个包名，第二个参数接收aidl对象
//        public abstract void getPackageSizeInfo(String packageName, int userHandle,
//        IPackageStatsObserver observer);
//        packageManager.getPackageSizeInfo();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            lyt_lading.setVisibility(View.GONE);
            CacheAdapter adapter = new CacheAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private void getCacheSize(PackageInfo packageInfo) {
        try {
//            Class<?> clazz = getClassLoader().loadClass("packageManager");//报错
            //通过反射获取到当前的方法
//            Method method = clazz.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //第一个参数表示当前的这个方法由谁调用，
            method.invoke(packageManager, packageInfo.applicationInfo.packageName, new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
        private PackageInfo packageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取当前应用的缓存大小
            long cacheSize = pStats.cacheSize;
            //如果当前的缓存大小是大于0的，说明有缓存
            if (cacheSize > 0) {
                System.out.println("当前应用名字：" + packageInfo.applicationInfo.loadLabel(packageManager) + "  缓存大小：" + cacheSize);
                CacheInfo cacheInfo = new CacheInfo();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheInfo.icon = icon;
                cacheInfo.cacheSize = cacheSize;
                cacheInfo.appName = appName;
                cacheLists.add(cacheInfo);
            }
        }
    }

    static class CacheInfo {
        Drawable icon;
        long cacheSize;
        String appName;
    }

    private class CacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheLists.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.appName = (TextView) view.findViewById(R.id.tv_name);
                holder.cacheSize = (TextView) view.findViewById(R.id.tv_cache_size);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            CacheInfo cacheInfo = cacheLists.get(position);
            holder.icon.setImageDrawable(cacheInfo.icon);
            holder.appName.setText(cacheInfo.appName);
            holder.cacheSize.setText(Formatter.formatFileSize(CleanCacheActivity.this, cacheInfo.cacheSize));
            return view;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView appName;
        TextView cacheSize;
    }

    public void cleanAll(View view) throws InvocationTargetException, IllegalAccessException {
        //获取到当前应用程序里面所有的方法
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            //判断当前的方法名字
            if (method.getName().equals("freeStorageAndNotify")) {
                method.invoke(packageManager, Integer.MAX_VALUE, new MyIPackageDataObserver());
            }
        }
        UIUtils.showToast(CleanCacheActivity.this, "全部清除");
    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }
}
