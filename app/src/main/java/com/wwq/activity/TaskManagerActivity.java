package com.wwq.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wwq.engine.TaskInfoParser;
import com.wwq.entity.AppInfo;
import com.wwq.entity.TaskInfo;
import com.wwq.mobilesafe.R;
import com.wwq.utils.ServiceStatusUtils;
import com.wwq.utils.SharedPreferencesUtils;
import com.wwq.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏文强 on 2016/6/4.
 */
public class TaskManagerActivity extends Activity {
    private SharedPreferences sp;

    @ViewInject(R.id.tv_task_process_count)
    private TextView tv_task_process_count;
    @ViewInject(R.id.tv_task_memory)
    private TextView tv_task_memory;
    @ViewInject(R.id.list_view)
    private ListView list_view;

    private TaskManagerAdapter adapter;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;

    private int processCount;
    private long  availMem;
    private long totalMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        sp = getSharedPreferences("config", 0);//0表示私有的
        initUI();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * ActivityManager 活动管理器（任务管理器）
     * PackageManager 包管理器
     */
    private void initUI() {
        ViewUtils.inject(this);
        processCount = ServiceStatusUtils.getProcessCount(this);
        tv_task_process_count.setText("运行进程" + processCount + "个");
        availMem = ServiceStatusUtils.getAvailMem(this);
        totalMem = ServiceStatusUtils.getTotalMem();
        tv_task_memory.setText("剩余/总内存：" +
                Formatter.formatFileSize(TaskManagerActivity.this, availMem) + "/" +
                Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到当前点击的对象
                Object object = list_view.getItemAtPosition(position);
                if (object != null && object instanceof TaskInfo) {
                    TaskInfo taskInfo = (TaskInfo) object;

                    TaskManagerAdapter.ViewHolder holder = (TaskManagerAdapter.ViewHolder) view.getTag();
                    if(taskInfo.getPackageName().equals(getPackageName())){
                        return;
                    }
                    if (taskInfo.isChecked()) {
                        taskInfo.setChecked(false);
                        holder.ch_app_status.setChecked(false);
                    } else {
                        taskInfo.setChecked(true);
                        holder.ch_app_status.setChecked(true);
                    }
                }
            }
        });
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);

                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();

                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }

//                handler.sendEmptyMessage(0);//换方式
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new TaskManagerAdapter();
                        list_view.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//            boolean result = sp.getBoolean("is_show_system", false);
            boolean result = SharedPreferencesUtils.getBoolean(TaskManagerActivity.this, "is_show_system", false);
            if(result){
                return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
            }else{
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                return null;
            }
            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = position - 1 - userTaskInfos.size() - 1;
                taskInfo = systemTaskInfos.get(location);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果当前的 position 等于 0，表示应用程序
            if (position == 0) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序（" + userTaskInfos.size() + ")");
                return textView;
            } else if (position == userTaskInfos.size() + 1) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序（" + systemTaskInfos.size() + ")");
                return textView;
            }
            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = position - 1 - userTaskInfos.size() - 1;
                taskInfo = systemTaskInfos.get(location);
            }
            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_app_memory_size = (TextView) view.findViewById(R.id.tv_app_memory_size);
                holder.ch_app_status = (CheckBox) view.findViewById(R.id.chb_app_status);
                view.setTag(holder);
            }
            holder.iv_app_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_app_name.setText(taskInfo.getAppName());
            holder.tv_app_memory_size.setText("内存占用：" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));

            if (taskInfo.isChecked()) {
                holder.ch_app_status.setChecked(true);
            } else {
                holder.ch_app_status.setChecked(false);
            }
            //判断当前展示的item是否是自己的程序。如果是，把程序选择隐藏
            if(taskInfo.getPackageName().equals(getPackageName())){
                holder.ch_app_status.setVisibility(View.INVISIBLE);//INVISIBLE比GONE性能好
            }else{
                holder.ch_app_status.setVisibility(View.VISIBLE);
            }

            return view;
        }

        class ViewHolder {
            ImageView iv_app_icon;
            TextView tv_app_name;
            TextView tv_app_memory_size;
            CheckBox ch_app_status;
        }
    }

    //全选
    public void selectAll(View view) {
        for (TaskInfo taskInfo : userTaskInfos) {
            if(taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(true);
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setChecked(true);
        }
        //一旦数据发生改变，一定要刷新
        adapter.notifyDataSetChanged();
    }

    //反选
    public void selectOppsite(View view) {
        for (TaskInfo taskInfo : userTaskInfos) {
            if(taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        //一旦数据发生改变，一定要刷新
        adapter.notifyDataSetChanged();
    }

    //清理进程
    public void killProcess(View view) {
        //想杀死进程，首先必须得到进程管理器
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<TaskInfo> killList = new ArrayList<TaskInfo>();

        int totalCount = 0;//清理了几个进程
        long killMem = 0;//清理进程的大小
        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.isChecked()) {
                killList.add(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            if (taskInfo.isChecked()) {
                killList.add(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }
        for(TaskInfo taskInfo : killList){
            if(taskInfo.isUserApp()){
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                userTaskInfos.remove(taskInfo);
            }else{
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                systemTaskInfos.remove(taskInfo);
            }
        }
        adapter.notifyDataSetChanged();
        ToastUtils.showToast(this, "共清理了" + totalCount + "个进程，释放"
                + Formatter.formatFileSize(this, availMem) + "内存");
        processCount -= totalCount;
        tv_task_process_count.setText("运行进程" + processCount + "个");

        tv_task_memory.setText("剩余/总内存：" +
                Formatter.formatFileSize(TaskManagerActivity.this, availMem + killMem) + "/" +
                Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
    }
    //进入任务管理设置界面
    public void openSetting(View view){
        Intent intent = new Intent(this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }
}
