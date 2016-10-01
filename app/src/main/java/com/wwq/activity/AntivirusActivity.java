package com.wwq.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wwq.db.AntivirusDao;
import com.wwq.mobilesafe.R;
import com.wwq.utils.MD5Util;

import java.util.List;

/**
 * Created by 魏文强 on 2016/6/10.
 */
public class AntivirusActivity extends Activity {
    private Message message;

    private TextView tv_init_virus;
    private ProgressBar pb;
    private ImageView img_scanning;
    private LinearLayout lyt_content;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        initUI();
        initData();
    }

    private void initUI() {
        tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);
        pb = (ProgressBar) findViewById(R.id.antivirus_progressBar);
        img_scanning = (ImageView) findViewById(R.id.img_scanning);
        lyt_content = (LinearLayout) findViewById(R.id.lyt_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        /*
        第一个参数表示开始的角度
        第二个参数表示结束的角度
        第三个参数表示参照自己
        初始化旋转动画
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(5000);//设置动画时间
        rotateAnimation.setRepeatCount(Animation.INFINITE);//设置动画无限循环
        img_scanning.startAnimation(rotateAnimation);
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                message = Message.obtain();
                message.what = BEGING;
                //获取到手机上所有安装的软件
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
                //返回手机上面安装了多少个应用程序
                int size = packageInfoList.size();
                pb.setMax(size);//设置进度条最大值
                int progress=0;

                for (PackageInfo packageInfo : packageInfoList) {
                    ScanInfo scanInfo = new ScanInfo();
                    //获取到当前手机上面的app名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    scanInfo.appName = appName;
                    scanInfo.packageName = packageInfo.applicationInfo.packageName;
                    //首先需要获取到每个应用程序的位置（目录）
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //获取到文件的MD5
                    String md5 = MD5Util.getFileMd5(sourceDir);
                    //判断当前的文件是否是病毒数据库里面
                    String desc = AntivirusDao.checkFileVirus(md5);
//                    System.out.println(md5);
//                    System.out.println(scanInfo.appName);
                    //如果当前的描述信息等于null说明没有病毒
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }
                    progress++;
                    SystemClock.sleep(100);
                    pb.setProgress(progress);
                    message = Message.obtain();
                    message.what = SCANING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }
                message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    private static final int BEGING = 101;
    private static final int SCANING = 102;
    private static final int FINISH = 103;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BEGING://扫描开始
                    tv_init_virus.setText("初始化8核杀毒引擎");
                    break;
                case SCANING://病毒扫描中
                    TextView child = new TextView(AntivirusActivity.this);
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    //如果为true表示有病毒
                    if(scanInfo.desc){
                        child.setTextColor(Color.RED);
                        child.setText(scanInfo.appName + " -> 有病毒");
                    }else{
                        child.setTextColor(Color.BLACK);
                        child.setText(scanInfo.appName + " -> 扫描安全");
                    }
                    lyt_content.addView(child);
                    //自动滚动
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(scrollView.FOCUS_DOWN);//一直往下滚动
                        }
                    });
                    break;
                case FINISH://扫描结束
                    //当扫描结束后停止动画
                    img_scanning.clearAnimation();
                    break;
            }
        }
    };

    class ScanInfo {
        boolean desc;
        String appName;
        String packageName;
    }
}
