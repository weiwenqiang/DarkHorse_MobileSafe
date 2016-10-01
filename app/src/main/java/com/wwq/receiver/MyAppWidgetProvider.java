package com.wwq.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.wwq.service.KillProcesWidgetService;
import com.wwq.service.KillProcessService;

/**
 * 创建桌面小部件的步骤：
 * 1 需要在清单文件里面配置元数据
 * 2 需要配置当前元数据里面要用到的ml，res/xml
 * 3 需要配置一个广播接收者
 * 4 实现一个桌面小部件的xml（根据需求，桌面小控件长什么样子，就实现什么样子）
 *
 * @author 魏文强
 */
public class MyAppWidgetProvider extends AppWidgetProvider {
    /**
     * 当前广播的生命周期只有10秒钟
     * 不能做耗时操作
     */
    //当桌面上面所有的桌面小控件都删除时最终会调用
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        System.out.println("onDisabled");
        Intent intent = new Intent(context, KillProcesWidgetService.class);
        context.stopService(intent);
    }
    //第一次创建的时候会调用
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        System.out.println("onEnabled");

        Intent intent = new Intent(context, KillProcesWidgetService.class);
        context.startService(intent);

    }
    //每次删除桌面小控件的时候都会调用的方法
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        System.out.println("onDeleted");
    }
    //每次有新的桌面小控件生成的时候会调用
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("onUpdate");
    }
}
