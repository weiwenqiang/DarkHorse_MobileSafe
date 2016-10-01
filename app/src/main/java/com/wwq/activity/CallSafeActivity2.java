package com.wwq.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wwq.adapter.MyBaseAdapter;
import com.wwq.db.BlackNumberDao;
import com.wwq.entity.BlackNumberInfo;
import com.wwq.mobilesafe.R;

import java.util.List;

/**
 * Created by 魏文强 on 2016/5/15.
 */
public class CallSafeActivity2 extends Activity {

    private ListView list_view;
    private LinearLayout lyt_prb;

    private List<BlackNumberInfo> blackNumberInfos;
    private BlackNumberDao dao;
    private CallSafeAdapter adapter;

    private int totalNumber;
    private int startIndex = 0;//开始的位置
    private int maxCount = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe2);
        initUI();
        initData();
    }

    private void initUI() {
        list_view = (ListView) findViewById(R.id.list_view);
        lyt_prb = (LinearLayout) findViewById(R.id.lyt_prb);
        //设置listview滚动监听器
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            //状态改变时候会调用的方法
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://闲置或静止状态
                        //获取到最后一条显示的数据
                        int lastVisiblePosition = list_view.getLastVisiblePosition();
                        System.out.println("lastVisiblePosition" + lastVisiblePosition);
                        if (lastVisiblePosition == blackNumberInfos.size() - 1) {
                            startIndex += maxCount;
                            if (startIndex >= totalNumber) {
                                Toast.makeText(CallSafeActivity2.this, "没有数据了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            initData();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://手指触摸时候的状态

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://惯性

                        break;
                }
            }

            //listview滚动的时候会调用的方法，时时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    lyt_prb.setVisibility(View.INVISIBLE);
                    if(adapter == null){
                        adapter = new CallSafeAdapter(CallSafeActivity2.this, blackNumberInfos);
                        list_view.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    private void initData() {
        lyt_prb.setVisibility(View.VISIBLE);
        dao = new BlackNumberDao(CallSafeActivity2.this);
        totalNumber = dao.getTotalNumber();
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (blackNumberInfos == null) {
                    blackNumberInfos = dao.findPar2(startIndex, maxCount);
                } else {
                    //把后面的数据，追加到集合里面，防止黑名单被覆盖
                    blackNumberInfos.addAll(dao.findPar2(startIndex, maxCount));
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {

        public CallSafeAdapter(Context mContext, List<BlackNumberInfo> list) {
            super(mContext, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity2.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) convertView.findViewById(R.id.txt_number);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.txt_mode);
                holder.img_call_delete = (ImageView) convertView.findViewById(R.id.img_call_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BlackNumberInfo entity = blackNumberInfos.get(position);
            holder.tv_number.setText(entity.getNumber());
            String mode = entity.getMode();
            if (mode.equals("1")) {
                holder.tv_mode.setText("来电拦截+短信拦截");
            } else if (mode.equals("2")) {
                holder.tv_mode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tv_mode.setText("短信拦截");
            }
            holder.img_call_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = entity.getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        blackNumberInfos.remove(number);
                        Toast.makeText(CallSafeActivity2.this, "删除成功", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CallSafeActivity2.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView tv_number;
            TextView tv_mode;
            ImageView img_call_delete;
        }
    }

    //添加黑名单
    public void addBlackNumber(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialog_view = View.inflate(this, R.layout.dialog_add_black_number, null);
        final EditText et_number = (EditText) dialog_view.findViewById(R.id.et_number);
        Button btn_ok = (Button) dialog_view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialog_view.findViewById(R.id.btn_cancel);
        final CheckBox cb_phone = (CheckBox) dialog_view.findViewById(R.id.cb_phone);
        final CheckBox cb_sms = (CheckBox) dialog_view.findViewById(R.id.cb_sms);
        final AlertDialog dialog = builder.create();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_number = et_number.getText().toString().trim();
                if (TextUtils.isEmpty(str_number)) {
                    Toast.makeText(CallSafeActivity2.this, "请输入黑名单号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = "";
                if (cb_phone.isChecked() && cb_sms.isChecked()) {
                    mode = "1";
                } else if (cb_phone.isChecked()) {
                    mode = "2";
                } else if (cb_sms.isChecked()) {
                    mode = "3";
                } else {
                    Toast.makeText(CallSafeActivity2.this, "请勾选拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.setNumber(str_number);
                blackNumberInfo.setMode(mode);
                blackNumberInfos.add(0,blackNumberInfo);
                //把电话号码和拦截模式添加
                dao.add(str_number, mode);
                if (adapter == null) {
                    adapter = new CallSafeAdapter(CallSafeActivity2.this, blackNumberInfos);
                    list_view.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        dialog.setView(dialog_view);
        dialog.show();
    }
}
