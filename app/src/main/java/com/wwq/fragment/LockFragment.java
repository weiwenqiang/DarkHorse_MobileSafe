package com.wwq.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wwq.db.AppLockDao;
import com.wwq.engine.TaskInfoParser;
import com.wwq.entity.AppInfo;
import com.wwq.entity.TaskInfo;
import com.wwq.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏文强 on 2016/6/11.
 */
public class LockFragment extends Fragment {
    private View view;
    private AppLockDao dao;

    private TextView tv_lock;
    private ListView list_view;

    private LockAdapter adapter;
    private List<TaskInfo> lockLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lock, null);
        tv_lock = (TextView) view.findViewById(R.id.tv_lock);
        list_view = (ListView) view.findViewById(R.id.list_view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //拿到所有应用程序
        List<TaskInfo> appInfoList = TaskInfoParser.getTaskInfos(getActivity());
        lockLists = new ArrayList<TaskInfo>();
        dao = new AppLockDao(getActivity());
        for (TaskInfo appInfo : appInfoList) {
            if (dao.find(appInfo.getPackageName())) {
                lockLists.add(appInfo);
            } else {

            }
        }
        adapter = new LockAdapter();
        list_view.setAdapter(adapter);
    }

    private class LockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            tv_lock.setText("已加锁（" + lockLists.size() + "）个");
            return lockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return lockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_lock, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            final TaskInfo taskInfo = lockLists.get(position);
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_name.setText(taskInfo.getAppName());
            //把程序添加到程序锁数据库里面
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //初始化一个位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    //设置动画时间
                    translateAnimation.setDuration(2000);
                    //开始动画
                    view.startAnimation(translateAnimation);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            SystemClock.sleep(2000);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //添加到数据库
                                    dao.delete(taskInfo.getPackageName());
                                    //从当前集合移除对象
                                    lockLists.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_unlock;
    }
}
