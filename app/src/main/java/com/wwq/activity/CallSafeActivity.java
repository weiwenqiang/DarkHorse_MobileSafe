package com.wwq.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public class CallSafeActivity extends Activity {

    private ListView list_view;
    private LinearLayout lyt_prb;
    private TextView txt_page;
    private EditText edt_PageNumber;

    private List<BlackNumberInfo> blackNumberInfos;
    private BlackNumberDao dao;
    private CallSafeAdapter adapter;

    private int mCurrentPageNumber = 0;
    private int mPageSize = 20;
    private int totalPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUI();
        initData();
    }

    private void initUI() {
        list_view = (ListView) findViewById(R.id.list_view);
        lyt_prb = (LinearLayout) findViewById(R.id.lyt_prb);
        txt_page = (TextView) findViewById(R.id.txt_page);
        edt_PageNumber = (EditText) findViewById(R.id.edt_PageNumber);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    lyt_prb.setVisibility(View.INVISIBLE);
                    txt_page.setText((mCurrentPageNumber + 1) + "/" + totalPage);//通过总的记录数
                    adapter = new CallSafeAdapter(CallSafeActivity.this, blackNumberInfos);
                    list_view.setAdapter(adapter);
                    break;
            }
        }
    };

    private void initData() {
        lyt_prb.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                dao = new BlackNumberDao(CallSafeActivity.this);
                totalPage = dao.getTotalNumber() / mPageSize;
//                blackNumberInfos = dao.findAll();
                blackNumberInfos = dao.findPar(mCurrentPageNumber, mPageSize);
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
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
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
                        Toast.makeText(CallSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CallSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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

    //上一页
    public void prevPage(View v) {
        //判断当前页码不能小于第一页
        if (mCurrentPageNumber <= 0) {
            Toast.makeText(CallSafeActivity.this, "已经是第一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPageNumber--;
        initData();
    }

    //下一页
    public void nextPage(View v) {
        //判断当前页码不能超过总页码
        if (mCurrentPageNumber >= totalPage - 1) {
            Toast.makeText(CallSafeActivity.this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPageNumber++;
        initData();
    }

    //跳转
    public void skipPage(View v) {
        String str_page_number = edt_PageNumber.getText().toString().trim();
        if (TextUtils.isEmpty(str_page_number)) {
            Toast.makeText(this, "请输入正确的页码", Toast.LENGTH_SHORT).show();
        } else {
            int number = Integer.parseInt(str_page_number);
            if (number >= 0 && number <= totalPage - 1) {
                mCurrentPageNumber = number;
                initData();
            } else {
                Toast.makeText(this, "请输入正确的页码", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
