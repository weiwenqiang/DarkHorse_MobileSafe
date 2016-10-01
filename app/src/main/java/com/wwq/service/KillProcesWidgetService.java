package com.wwq.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.wwq.mobilesafe.R;
import com.wwq.receiver.MyAppWidgetProvider;
import com.wwq.utils.ServiceStatusUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 魏文强 on 2016/6/10.
 */
public class KillProcesWidgetService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //桌面小控件的管理者
        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);


        //每隔5秒钟更新一次桌面
        //初始化定时器
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("KillProcesWidgetService 5秒");
                //更新桌面
                //上下文，第二个参数表示当前有那一个广播处理当前的桌面小控件
                ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
                //初始化一个远程的View
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                //需要注意，这个里面findingviewid这个方法
                int processCount = ServiceStatusUtils.getProcessCount(getApplicationContext());
                views.setTextViewText(R.id.process_count, "正在运行软件：" + processCount);
                long availMem = ServiceStatusUtils.getAvailMem(getApplicationContext());
                views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(getApplicationContext(), availMem));
                //发送一个隐式意图
                Intent intent = new Intent();
                intent.setAction("com.wwq.mobilesafe");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                widgetManager.updateAppWidget(componentName, views);
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }
}
