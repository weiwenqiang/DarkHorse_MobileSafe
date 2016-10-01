package com.wwq.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 魏文强 on 2016/6/11.
 */
public class AntivirusDao {
    private static final String PATH = "data/data/com.wwq.mobilesafe/files/antivirus.db";
    //检查当前的md5值是否在病毒数据库里面
    public static String checkFileVirus(String md5){
        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READONLY);
        //查询当前传过来的md5是否在病毒数据库里面
        Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
        //判断当前的游标是否可以移动
        if(cursor.moveToNext()){
            desc = cursor.getString(0);
        }
        cursor.close();
        return desc;
    }
    //添加病毒数据库
    public static void addVirus(String md5, String desc){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READWRITE);
        if(checkFileVirus(md5)!=null){
            ContentValues values = new ContentValues();
            values.put("md5", md5);
            values.put("type", 6);
            values.put("name", "Android.Troj.AirAD.a");
            values.put("desc", desc);
            db.insert("datable", null, values);
            db.close();
        }
    }
}
