package com.wwq.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.wwq.db.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by 魏文强 on 2016/5/21.
 */
public class CallSafeService extends Service {
    private BlackNumberDao dao;
    private TelephonyManager tm;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        dao = new BlackNumberDao(this);
        //获取到系统的电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        InnerReceiver innerReceiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://电话闲置
                    break;
                case TelephonyManager.CALL_STATE_RINGING://电话铃响
                    String mode = dao.findNumber(incomingNumber);
                    if (mode.equals("1") || mode.equals("2")) {

                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new MyContentObserver(new Handler(), incomingNumber));

//                        tm.endcall();//危险操作，被隐藏，@hide 源码隐藏标记
                        endCall();//aidl挂断电话
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://电话接听
                    break;
            }
        }
    }

    //内容观察者
    private class MyContentObserver extends ContentObserver {
        private String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler        The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //当数据改变的时候调用的方法
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
            super.onChange(selfChange);
        }
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] object = (Object[]) intent.getExtras().get("pdus");
            // 为什么是For循环，短信最多是140字节，超出的话，会分为多条短信发送，所以是一个数组，因为我们的短信指令很短，所以for循环只执行一次
            for (Object obj : object) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
                String originatingAddress = message.getOriginatingAddress();// 短信的来源号码
                String messageBody = message.getMessageBody();// 短信内容
                System.out.println(originatingAddress + ";" + messageBody);
                //通过短信的电话号码查询拦截的模式
                String mode = dao.findNumber(originatingAddress);
                if (mode.equals("1")) {
                    abortBroadcast();
                } else if (mode.equals("3")) {
                    abortBroadcast();
                }
                //通过短信内容拦截
                if (messageBody.contains("fapiao")) {
                    abortBroadcast();
                }
            }
        }
    }

    //挂断电话
    private void endCall() {
        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("gettService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            //使用aidl，强行调方法
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除来电号码
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
    }
}
