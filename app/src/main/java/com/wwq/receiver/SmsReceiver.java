package com.wwq.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.wwq.mobilesafe.R;
import com.wwq.service.LocationService;
import com.wwq.utils.AdminReceiver;

/**
 * Created by 魏文强 on 2016/5/12.
 */
public class SmsReceiver extends BroadcastReceiver {
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] object = (Object[]) intent.getExtras().get("pdus");
        // 为什么是For循环，短信最多是140字节，超出的话，会分为多条短信发送，所以是一个数组，因为我们的短信指令很短，所以for循环只执行一次
        for (Object obj : object) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
            String originatingAddress = message.getOriginatingAddress();// 短信的来源号码
            String messageBody = message.getMessageBody();// 短信内容
            System.out.println(originatingAddress + ";" + messageBody);

            mDPM = (DevicePolicyManager) context
                    .getSystemService(Context.DEVICE_POLICY_SERVICE);
            // 设备管理组件
            mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);

            if ("#*alarm*#".equals(messageBody)) {
                // 播放报警音乐，即使手机调为静音，也能播放音乐，因为使用的是媒体声音的通道，和铃声无关
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();

                abortBroadcast();// 中断短信的传递，从而系统短信app就收不到内容了
            } else if ("#*location*#".equals(messageBody)) {
                // 获取经纬度
                context.startService(new Intent(context, LocationService.class));

                SharedPreferences sp = context.getSharedPreferences("config",
                        Context.MODE_PRIVATE);
                String location = sp.getString("location",
                        "getting location....");
                System.out.println("location:" + location);
                abortBroadcast();
            } else if ("#*wipedata*#".equals(messageBody)) {

                if (mDPM.isAdminActive(mDeviceAdminSample)) {
                    mDPM.lockNow();
                    mDPM.resetPassword("123456", 0);// 空串去掉密码
                } else {
                    Toast.makeText(context, "必须先激活设备！", Toast.LENGTH_SHORT)
                            .show();
                }
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(messageBody)) {
                if (mDPM.isAdminActive(mDeviceAdminSample)) {
                    mDPM.wipeData(0);// 清除数据，恢复出厂设置
                } else {
                    Toast.makeText(context, "必须先激活设备！", Toast.LENGTH_SHORT)
                            .show();
                }
                abortBroadcast();
            }
        }

    }
}
