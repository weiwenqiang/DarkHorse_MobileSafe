package com.wwq.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wwq.db.AntivirusDao;
import com.wwq.entity.Virus;
import com.wwq.mobilesafe.R;
import com.wwq.utils.StreamUtil;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 魏文强 on 2016/4/17.
 */
public class SplashActivity extends Activity {
    //全局引用
    //控件
    private RelativeLayout rlRoot;
    private TextView tv_version;
    private TextView tv_progress;//下载进度展示
    //组件
    private SharedPreferences mPref;
    private AntivirusDao antivirusDao;
    //变量
    private String mVersionName;
    private int mVersionCode;
    private String mDesc;
    private String mDownloadUrl;
    //生命周期
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //有米初始化
        AdManager.getInstance(this).init("申请ID", "申请Key", false);
        getControlReference();
        initComponent();
        getControlEvents();
    }

    //获得控件引用
    private void getControlReference(){
        rlRoot = (RelativeLayout) findViewById(R.id.lyt_root);
        tv_version = (TextView) findViewById(R.id.txt_version);
        tv_progress = (TextView) findViewById(R.id.txt_progress);
    }
    //初始化组件
    private void initComponent(){
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        tv_version.setText("版本名:" + getVersionName() + "v");
        copyDB("address.db");//拷贝联系人数据库
        copyAntivirusDB("antivirus.db");//拷贝病毒数据库

        createShortcut();//创建快捷方式
        updateVirus();//更新病毒数据库

        boolean autoUpdate = mPref.getBoolean("auto_update", true);
        if(autoUpdate){
            checkVersion();
        }else{
            handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);//延时两秒后发送消息
        }
        //渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rlRoot.startAnimation(anim);
    }
    //设置控制事件
    private void getControlEvents(){

    }
    //Handler
    private final int CODE_UPDATE_DIALOG = 0;
    private final int CODE_URL_ERROR = 1;
    private final int CODE_NET_ERROR = 2;
    private final int CODE_JSON_ERROR = 3;
    private final int CODE_ENTER_HOME = 4;//进入主界面
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDailog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                default:
                    break;
            }
        }
    };

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void checkVersion() {
        final long startTime = System.currentTimeMillis();
        // 启动子线程异步加载数据
        new Thread() {

            @Override
            public void run() {
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://10.0.2.2:8080/test/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    // 请求方式
                    conn.setRequestMethod("GET");
                    // 连接超时
                    conn.setConnectTimeout(5000);
                    // 响应超时,连接上服务器了，但服务器迟迟不给响应
                    conn.setReadTimeout(5000);
                    // 连接服务器
                    conn.connect();
                    // 获取响应码
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtil.readFromStream(inputStream);
                        // 解析JSON
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDesc = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");
                    }
                    if (mVersionCode > getVersionCode()) {
                        // 服务器VersionCode大于本地的VersionCode
                        // 说明有更新，弹出升级对话框
                        msg.what = CODE_UPDATE_DIALOG;
                    }else{
                        //没有版本更新
                        msg.what = CODE_ENTER_HOME;
                    }

                } catch (MalformedURLException e) {
                    // url错误的异常
                    msg.what = CODE_URL_ERROR;
                    enterHome();
                    e.printStackTrace();
                } catch (IOException e) {
                    // 网络错误的异常
                    msg.what = CODE_NET_ERROR;
                    enterHome();
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = CODE_JSON_ERROR;
                    enterHome();
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;
                    if(timeUsed<2000){
                        try {
                            //强制休眠一段时间，保证闪屏页展示2秒钟
                            Thread.sleep(2000-timeUsed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                    if (conn != null) {
                        // 关闭网络连接
                        conn.disconnect();
                    }
                }
            }

        }.start();
    }

    private void showUpdateDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage(mDesc);
//		builder.setCancelable(false);//不让用户取消对话框,尽量不用，用户体验太差
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("立即更新");
                download();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        //设置取消的监听，用户点击返回键时会触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }
    private void enterHome(){
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void download(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            tv_progress.setVisibility(View.VISIBLE);//显示进度


            String target = Environment.getExternalStorageDirectory()+"/update.apk";
            HttpUtils utils = new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>(){

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    //文件下载进度
                    System.out.println("下载进度："+current+"/"+total);
                    tv_progress.setText("下载进度:"+current * 100 / total+"%");
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    Toast.makeText(SplashActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    //跳转到系统下载页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
//					startActivity(intent);
                    startActivityForResult(intent, 0);//如果用户取消安装会返回结果,回调结果
                }
            });
        }else{
            Toast.makeText(SplashActivity.this, "没有找到SD卡", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void copyDB(String dbName){
        File destFile = new File(getFilesDir(),"address.db");//要拷贝的目标地址

        if(destFile.exists()){
            System.out.println("数据库"+dbName+"已存在！");
            return;
        }

        InputStream in=null;
        FileOutputStream out=null;
        try {
            in = getAssets().open(dbName);

            out = new FileOutputStream(destFile);
            int len = 0;
            byte[] buffer = new byte[1024];
            while((len=in.read(buffer))!=-1){
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyAntivirusDB(String dbName){
        File destFile = new File(getFilesDir(),"antivirus.db");//要拷贝的目标地址

        if(destFile.exists()){
            System.out.println("数据库"+dbName+"已存在！");
            return;
        }

        InputStream in=null;
        FileOutputStream out=null;
        try {
            in = getAssets().open(dbName);

            out = new FileOutputStream(destFile);
            int len = 0;
            byte[] buffer = new byte[1024];
            while((len=in.read(buffer))!=-1){
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //创建快捷方式
    private void createShortcut() {
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //如果设置为true表示可以创建重复的快捷方式
        intent.putExtra("duplicate", false);//Launcher.EXTRA_SHORTCUT_DUPLICATE
        //快捷方式功能为打电话
//        Intent wayCallIntent = new Intent();
//        wayCallIntent.setAction(Intent.ACTION_CALL);
//        wayCallIntent.setData(Uri.parse("tel://13012345678"));
        //快捷方式名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "创世之盾");
        //快捷方式图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //桌面快捷方式需要使用隐式意图
        Intent splashIntent = new Intent();
        splashIntent.setAction("wwq.home");
        splashIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, splashIntent);

        sendBroadcast(intent);
    }
    //更新病毒数据库
    private void updateVirus(){
        antivirusDao = new AntivirusDao();
        //联网从服务器获取到最新病毒数据库
        HttpUtils httpUtils = new HttpUtils();
        String url = "http://192.168.26.2:8080//test/virus.json";
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println(responseInfo.result);
//                {"md5":"3771ac0744b39b80ea2e48a90d848311","desc":"蝗虫病毒赶快卸载"}
                try {
                    //手动解析
//                    JSONObject jsonObject = new JSONObject(responseInfo.result);
//                    String md5 = jsonObject.getString("md5");
//                    String desc = jsonObject.getString("desc");
                    //三方Gson谷歌
                    Gson gson = new Gson();
                    //所有格式都只要这一句
                    Virus virus = gson.fromJson(responseInfo.result, Virus.class);
                    antivirusDao.addVirus(virus.md5, virus.desc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
}
