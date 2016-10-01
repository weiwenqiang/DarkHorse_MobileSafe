package com.wwq.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by 魏文强 on 2016/5/26.
 */
public class SmsUtils {
    //备份短信接口
    public interface BackUpCallBackSms{
        public void befor(int count);
        public void onBackUpSms(int process);
    }
    //备份短信
    public static boolean backUp(Context context, BackUpCallBackSms callback) {
        /*
        1.判断当前用户的手机上面是否有SD卡
        2.权限 ---rw 需要使用内容提供者
        3.写短信到SD卡
         */
        //判断SD卡的状态
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                //把短信备份到SD卡，第二个参数表示名字
                File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
                FileOutputStream os = new FileOutputStream(file);
                //如果能进来就说明用户有SD卡
                ContentResolver resolver = context.getContentResolver();
                //获取短信的路径
                Uri uri = Uri.parse("content://sms/");

                Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
                int count = cursor.getCount();
//                pd.setMax(count);
                callback.befor(count);
                int process = 0;

                //得到xml序列化器，在android系统里面所有有关xml的解析都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
                //把短信序列化到SD卡然后设置编码格式
                serializer.setOutput(os, "utf-8");
                //standalone表示当前的xml是否是独立文件，true表示是独立文件
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "smss");
                serializer.attribute(null, "size", String.valueOf(count));

                while (cursor.moveToNext()) {
                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");
                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");
                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null, "type");
                    serializer.startTag(null, "body");
                    //读取短信内容
                    serializer.text(Crypto.encrypt("123", cursor.getString(3)));
                    serializer.endTag(null, "body");

                    serializer.endTag(null, "sms");

                    process++;
//                    pd.setProgress(process);
                    callback.onBackUpSms(process);
                    SystemClock.sleep(500);
                }
                cursor.close();
                serializer.endTag(null, "smss");
                serializer.endDocument();
                os.flush();
                os.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
