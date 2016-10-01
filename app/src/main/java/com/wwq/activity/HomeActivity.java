package com.wwq.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wwq.mobilesafe.R;
import com.wwq.utils.MD5Util;

/**
 * Created by 魏文强 on 2016/4/17.
 */
public class HomeActivity extends Activity {
    //全局引用
    private SharedPreferences mPref;
    //控件
    private GridView gv_Home;
    //组件
    private String[] mItem = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};

    //变量
    //生命周期
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPref = getSharedPreferences("config", MODE_PRIVATE);


        getControlReference();
        initComponent();
        setControlEvents();
    }

    //获得控件引用
    private void getControlReference() {
        gv_Home = (GridView) findViewById(R.id.gv_home);
    }

    //初始化组件
    private void initComponent() {
        gv_Home.setAdapter(new HomeAdapter());
    }

    //设置控制事件
    private void setControlEvents() {
        gv_Home.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        // 手机防盗
                        showPasswordDialog();
                        break;
                    case 1:
                        // 通信卫士
                        startActivity(new Intent(HomeActivity.this,
                                CallSafeActivity2.class));
                        break;
                    case 2:
                        // 软件管理
                        startActivity(new Intent(HomeActivity.this,
                                AppManagerActivity.class));
                        break;
                    case 3:
                        // 进程管理
                        startActivity(new Intent(HomeActivity.this,
                                TaskManagerActivity.class));
                        break;
                    case 4:
                        // 流量统计
                        startActivity(new Intent(HomeActivity.this,
                                TrafficManagerActivity.class));
                        break;
                    case 5:
                        // 手机杀毒
                        startActivity(new Intent(HomeActivity.this,
                                AntivirusActivity.class));
                        break;
                    case 6:
                        // 缓存清理
                        startActivity(new Intent(HomeActivity.this,
                                CleanCacheActivity.class));
                        break;
                    case 7:
                        // 高级工具
                        startActivity(new Intent(HomeActivity.this,
                                AToolsActivity.class));
                        break;
                    case 8:
                        // 设置中心
                        startActivity(new Intent(HomeActivity.this,
                                SettingActivity.class));
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显示密码弹窗
     */
    protected void showPasswordDialog() {
        // 判断是否设置密码
        String savedPassword = mPref.getString("password", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            showPasswordInputDialog();
        } else {
            // 如果没有设置过，弹出设置密码的弹窗
            showPasswordSetDailog();
        }
    }

    /**
     * 输入密码的弹窗
     */
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_input_password, null);
        dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0，保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password_conf);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = mPref.getString("password", null);
                    if (MD5Util.encode(password).equals(savedPassword)) {
                        // Toast.makeText(HomeActivity.this, "登录成功！",
                        // Toast.LENGTH_SHORT).show();
                        dialog.dismiss();// 隐藏dialog
                        // 跳转手机防盗页
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空",
                            Toast.LENGTH_SHORT).show();
                }

                String savedPassword = mPref.getString("password", null);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });

        dialog.show();
    }

    /**
     * 设置密码的弹窗
     */
    private void showPasswordSetDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_set_password, null);
        dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0，保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password_conf);
        final EditText etPasswordConfirm = (EditText) view
                .findViewById(R.id.et_password_confirm);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
                // password!=null && password.length()>0
                if (!TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(passwordConfirm)) {
                    if (password.equals(passwordConfirm)) {
                        // Toast.makeText(HomeActivity.this, "登录成功",
                        // Toast.LENGTH_SHORT).show();
                        // 将密码保存起来
                        mPref.edit()
                                .putString("password",
                                        MD5Util.encode(password)).commit();

                        dialog.dismiss();// 隐藏dialog
                        // 跳转手机防盗页
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });

        dialog.show();
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItem.length;
        }

        @Override
        public Object getItem(int position) {
            return mItem[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,
                    R.layout.item_home_list, null);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_item);

            tvItem.setText(mItem[position]);
            ivIcon.setImageResource(mPics[position]);
            return view;
        }
    }
}
