package com.wwq.activity;

import com.wwq.mobilesafe.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 修改归属地显示位置
 * 
 * @author 魏文强
 *
 */
public class DragViewActivity extends Activity {
	private TextView tvTop;
	private TextView tvBottom;
	private ImageView ivDrag;
	private SharedPreferences mPref;
	long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		tvTop = (TextView) findViewById(R.id.tv_drag_top);
		tvBottom = (TextView) findViewById(R.id.tv_drag_bottom);
		ivDrag = (ImageView) findViewById(R.id.iv_drag);

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		// onMeasure测量,onLayout定位置,onDraw绘制
		// ivDrag.layout(lastX, lastY, lastX + ivDrag.getWidth(),
		// lastY + ivDrag.getHeight());//不能用这个方法，因为还没有测量完成，就不能安放位置
		// 获取屏幕宽高
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		if (lastY > winHeight / 2) {
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag
				.getLayoutParams();// 获取布局对象，封装了很多信息//获取布局对象
		layoutParams.leftMargin = lastX;// 设置左边距
		layoutParams.topMargin = lastY;// 设置顶边距
		ivDrag.setLayoutParams(layoutParams);// 重设位置
		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 把图片居中
					ivDrag.layout(winWidth / 2 - ivDrag.getWidth() / 2,
							ivDrag.getTop(), winWidth / 2 + ivDrag.getWidth()
									/ 2, ivDrag.getBottom());
				}
			}
		});
		ivDrag.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 获取起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					// 计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;
					// 更新左上右下距离
					int l = ivDrag.getLeft() + dx;
					int r = ivDrag.getRight() + dx;
					int t = ivDrag.getTop() + dy;
					int b = ivDrag.getBottom() + dy;
					// 判断是否超出屏幕边界，注意状态栏的高度
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - 20) {
						break;
					}
					// 更新界面
					ivDrag.layout(l, t, r, b);
					// 初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					if (t > winHeight / 2) {
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					} else {
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}
					break;
				case MotionEvent.ACTION_UP:
					// 记录坐标点
					Editor edit = mPref.edit();
					edit.putInt("lastX", ivDrag.getLeft());
					edit.putInt("lastY", ivDrag.getTop());
					edit.commit();
					break;
				default:
					break;
				}
				return false;// 事件要向下传递，让onClick可以响应
			}
		});
	}
}
