package com.wwq.service;

import com.wwq.db.AddressDao;
import com.wwq.mobilesafe.R;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {
	private TelephonyManager tm;
	private MyListener listener;
	private OutCallReceiver receiver;
	private WindowManager mWM;
	private View view;
	private SharedPreferences mPref;
	WindowManager.LayoutParams params;
	private int winWidth;
	private int winHeight;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		// 监听来电
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// 监听打电话的状态

		// 动态注册广播
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);// 停止来电监听
		// 反向注销广播
		unregisterReceiver(receiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("电话铃响...");

				String address = AddressDao.getAddress(incomingNumber);// 更具来电号码查询归属地
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG).show();
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (mWM != null && view != null) {
					mWM.removeView(view);// 从windoow中移除view
					view = null;
				}
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	/**
	 * 监听去电的广播接受者
	 * 
	 * @author 魏文强 需要权限：android.permission.PROCESS_OUTGOING_CALLS
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();

			String address = AddressDao.getAddress(number);
			showToast(address);
			Toast.makeText(context, address, Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 自定义归属地浮窗
	 * 需要权限SYSTEM_ALERT_WINDOW
	 */
	private void showToast(String text) {
		// 可以在第三方app中弹出自己的浮窗
		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		//获取屏幕宽高
		winWidth = mWM.getDefaultDisplay().getWidth();
		winHeight = mWM.getDefaultDisplay().getHeight();
		
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//不能被触摸
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;Toast动画，用不着
		params.type = WindowManager.LayoutParams.TYPE_PHONE;//权限低TYPE_TOAST
		params.gravity = Gravity.LEFT + Gravity.TOP;//将重心位置设置为左上方，也就是(0，0)，而不是默认的中心位置
		
		params.setTitle("Toast");
		
		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		//设置浮窗的位置，基于偏移量
		params.x = lastX;
		params.y = lastY;

		// view = new TextView(this);
		view = View.inflate(this, R.layout.toast_address, null);

		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int style = mPref.getInt("address_style", 0);
		view.setBackgroundResource(bgs[style]);//根据存储的样式更新背景
		
		TextView tvText = (TextView) view.findViewById(R.id.tv_number);
		tvText.setText(text);
		mWM.addView(view, params);// 将view添加在屏幕(window)上
		
		view.setOnTouchListener(new OnTouchListener() {
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
					//更新浮窗位置
					params.x += dx;
					params.y += dy;
					//防止坐标偏离屏幕
					if(params.x <0 ){
						params.x=0;
					}
					if(params.y <0 ){
						params.y=0;
					}
					if(params.x >winWidth-view.getWidth()){
						params.x=winWidth-view.getWidth();
					}
					if(params.y >winHeight-view.getHeight()){
						params.y=winHeight-view.getHeight();
					}
					System.out.println("x:"+params.x+" y:"+params.y);
					mWM.updateViewLayout(view, params);
					// 初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 记录坐标点
					Editor edit = mPref.edit();
					edit.putInt("lastX", params.x);
					edit.putInt("lastY", params.y);
					edit.commit();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
}
