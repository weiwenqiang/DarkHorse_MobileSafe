package com.wwq.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wwq.mobilesafe.R;
import com.wwq.utils.SmsUtils;
import com.wwq.utils.ToastUtils;
import com.wwq.utils.UIUtils;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

/**
 * 高级工具
 * Created by 魏文强 on 2016/4/20.
 */
public class AToolsActivity extends Activity {
    private Button button;
    private ProgressDialog pd;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        ViewUtils.inject(this);
    }

    //电话归属地查询
    public void numberAddressQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    //备份短信
    public void backUpsms(View view) {
        pd = new ProgressDialog(AToolsActivity.this);
        pd.setTitle("备份短信");
        pd.setMessage("稍安勿躁，正在备份。你等着吧..");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//横着样式
        pd.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                boolean result = SmsUtils.backUp(AToolsActivity.this, new SmsUtils.BackUpCallBackSms() {

                    @Override
                    public void befor(int count) {
                        pd.setMax(count);
                        progressBar.setMax(count);
                    }

                    @Override
                    public void onBackUpSms(int process) {
                        pd.setProgress(process);
                        progressBar.setProgress(process);
                    }
                });
                if (result) {//子线程更新UI！！！
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份成功");
                    Looper.loop();
//                    UIUtils.showToast(AToolsActivity.this, "备份成功");//安全弹Toast封装
                } else {
                    Looper.prepare();
                    ToastUtils.showToast(AToolsActivity.this, "备份失败");
                    Looper.loop();
//                    UIUtils.showToast(AToolsActivity.this, "备份失败");
                }
                pd.dismiss();
            }
        }.start();
    }

    //程序锁
    public void appLock(View view) {
        Intent intent = new Intent(this, AppLockActivity.class);
        startActivity(intent);
    }

    //软件推荐
    public void appRecomment(View view) {
// 调用方式二：直接打开全屏积分墙，并且监听积分墙退出的事件onDestory
        OffersManager.getInstance(this).showOffersWall(
                new Interface_ActivityListener() {

                    /**
                     * 但积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
                     */
                    @Override
                    public void onActivityDestroy(Context context) {
                        Toast.makeText(context, "全屏积分墙退出了", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
