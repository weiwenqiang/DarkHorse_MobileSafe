package com.wwq.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 魏文强 on 2016/4/20.
 */
public class MD5Util {
    public static String encode(String password){
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");//获取MD5算法对象
            byte[] digest = instance.digest(password.getBytes());//对字符串加密，返回字节数组
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;//获取字节的低八位有效值
                String hexString = Integer.toHexString(i);//将整数转换为16进制
                if(hexString.length()<2){
                    hexString = "0"+hexString;//如果是1位的话，补0
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //没有该算法时，抛出异常，不会走到这里
            e.printStackTrace();
        }
        return "";
    }

    public static String getFileMd5(String sourceDir){
        File file = new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            //获取到数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            while((len = fis.read(buffer))!=-1){
                messageDigest.update(buffer, 0, len);
            }
            byte[] result = messageDigest.digest();

            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                int i = b & 0xff;//获取字节的低八位有效值
                String hexString = Integer.toHexString(i);//将整数转换为16进制
                if(hexString.length()<2){
                    hexString = "0"+hexString;//如果是1位的话，补0
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
