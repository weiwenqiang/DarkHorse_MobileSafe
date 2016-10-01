package com.wwq.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wwq.engine.AppInfos;
import com.wwq.entity.AppInfo;
import com.wwq.mobilesafe.R;
import com.wwq.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 魏文强 on 2016/5/22.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_rom)
    private TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    @ViewInject(R.id.tv_app_count)
    private TextView tv_app_count;

    private List<AppInfo> appInfoList;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;

    private PopupWindow popupWindow;
    private AppInfo localPackageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUI();
        initData();
    }

    @Override
    protected void onDestroy() {
        popupWindowDismiss();
        super.onDestroy();
    }

    private void initUI() {
        ViewUtils.inject(this);
        //获取到rom内存的运行的剩余空间
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取到SD卡的剩余空间
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //格式化大小
        tv_rom.setText("内存可用：" + Formatter.formatFileSize(this, rom_freeSpace));
        tv_sd.setText("SD卡可用" + Formatter.formatFileSize(this, sd_freeSpace));
        //卸载广播
        UninstallReceiver receiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);

        //设置ListView的滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * @param firstVisibleItem 第一个可见的条目位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount 总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popupWindowDismiss();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size() + 1) {
                        //系统应用程序
                        tv_app_count.setText("系统程序（" + systemAppInfos.size() + ")");
                    } else {
                        //用户应用程序
                        tv_app_count.setText("用户程序（" + userAppInfos.size() + ")");
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到当前点击的item对象
                Object obj = listView.getItemAtPosition(position);
                if (obj != null && obj instanceof AppInfo) {
                    localPackageInfo = (AppInfo) obj;

                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);

                    LinearLayout lytUninstall = (LinearLayout) contentView.findViewById(R.id.lyt_item_popup_uninstall);
                    LinearLayout lytStart = (LinearLayout) contentView.findViewById(R.id.lyt_item_popup_start);
                    LinearLayout lytShare = (LinearLayout) contentView.findViewById(R.id.lyt_item_popup_share);
                    LinearLayout lytDetails = (LinearLayout) contentView.findViewById(R.id.lyt_item_popup_details);
                    lytUninstall.setOnClickListener(AppManagerActivity.this);
                    lytStart.setOnClickListener(AppManagerActivity.this);
                    lytShare.setOnClickListener(AppManagerActivity.this);
                    lytDetails.setOnClickListener(AppManagerActivity.this);

                    popupWindowDismiss();
                    //-2表示包裹内容
                    popupWindow = new PopupWindow(contentView, -2, -2);
                    //需要注意，使用 PopupWindow 必须设置背景，不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);
                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);

                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(500);
                    contentView.startAnimation(sa);
                }
            }
        });
    }

    private void initData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                //获取到所有安装到手机上面的应用程序
                appInfoList = AppInfos.getAppInfos(AppManagerActivity.this);
                //appInfoList 拆成 用户程序集合 + 系统程序集合
                userAppInfos = new ArrayList<AppInfo>();//用户程序集合
                systemAppInfos = new ArrayList<AppInfo>();//系统程序集合
                for (AppInfo appInfo : appInfoList) {
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };
        thread.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManagerAdapter adapter = new AppManagerAdapter();
            listView.setAdapter(adapter);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_item_popup_uninstall://卸载
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + localPackageInfo.getApkPackName()));
                startActivity(intent);
                popupWindowDismiss();
                break;
            case R.id.lyt_item_popup_start: //运行
                Intent startIntent = getPackageManager().getLaunchIntentForPackage(localPackageInfo.getApkPackName());
                if(startIntent!=null){
                    startActivity(startIntent);
                }else{
                    ToastUtils.showToast(AppManagerActivity.this,"该应用没有启动界面");
                }
                popupWindowDismiss();
                break;
            case R.id.lyt_item_popup_share://分享
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                shareIntent.putExtra("android.intent.extra.TEXT", "Hi！推荐您使用软件：" + localPackageInfo.getApkName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + localPackageInfo.getApkPackName());
                startActivity(Intent.createChooser(shareIntent, "分享"));
                popupWindowDismiss();
                break;
            case R.id.lyt_item_popup_details://详情
                Intent detailsIntent = new Intent();
                detailsIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detailsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                detailsIntent.setData(Uri.parse("package:" + localPackageInfo.getApkPackName()));
                startActivity(detailsIntent);
                break;
        }
    }

    private class AppManagerAdapter extends BaseAdapter {
        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_location;
            TextView tv_apk_size;
        }

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() + 1) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = position - 1 - userAppInfos.size() - 1;
                appInfo = systemAppInfos.get(location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果当前的 position 等于 0，表示应用程序
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序（" + userAppInfos.size() + ")");
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序（" + systemAppInfos.size() + ")");
                return textView;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() + 1) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = position - 1 - userAppInfos.size() - 1;
                appInfo = systemAppInfos.get(location);
            }

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                holder.tv_apk_size = (TextView) view.findViewById(R.id.tv_apk_size);

                view.setTag(holder);
            }

            holder.iv_icon.setBackground(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());
            holder.tv_apk_size.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            if (appInfo.isRom()) {
                holder.tv_location.setText("手机内存");
            } else {
                holder.tv_location.setText("外部存储");
            }
            return view;
        }
    }

    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
            System.out.println("接收到卸载的广播");
        }
    }
}
