package com.wwq.fragment;

import android.app.Activity;
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
public class UnLockFragment extends Fragment {
    private View view;
    private ListView list_view;
    private TextView tv_unlock;

    private List<TaskInfo> taskInfos;
    private UnLockAdapter adapter;
    private AppLockDao dao;
    private List<TaskInfo> unLockLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unlock, null);

        getControlReference();
        initComponent();
        setControlEvents();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //附加
        //图片资源文件夹缩放比例为 3:4:6:8
    }

    @Override
    public void onStart() {
        super.onStart();
        taskInfos = TaskInfoParser.getTaskInfos(getActivity());
        //获取到程序锁的dao
        unLockLists = new ArrayList<TaskInfo>();
        dao = new AppLockDao(getActivity());
        for (TaskInfo taskInfo : taskInfos) {
            //判断当前的应用是否在程序锁的数据里面
            if (dao.find(taskInfo.getPackageName())) {

            } else {//如果查询不到，说明没有在程序锁的数据库里面
                unLockLists.add(taskInfo);
            }
        }

        adapter = new UnLockAdapter();
        list_view.setAdapter(adapter);
    }

    private void getControlReference() {
        list_view = (ListView) view.findViewById(R.id.list_view);
        tv_unlock = (TextView) view.findViewById(R.id.tv_unlock);
    }

    private void initComponent() {

    }

    private void setControlEvents() {

    }

    private class UnLockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            tv_unlock.setText("未加锁（" + unLockLists.size() + "）个");
            return unLockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockLists.get(position);
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
                view = View.inflate(getActivity(), R.layout.item_unlock, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            final TaskInfo taskInfo = unLockLists.get(position);
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_name.setText(taskInfo.getAppName());
            //把程序添加到程序锁数据库里面
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //初始化一个位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    //设置动画时间
                    translateAnimation.setDuration(2000);
                    //开始动画
                    view.startAnimation(translateAnimation);
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            SystemClock.sleep(2000);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //添加到数据库
                                    dao.add(taskInfo.getPackageName());
                                    //从当前集合移除对象
                                    unLockLists.remove(position);
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
