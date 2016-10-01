package com.wwq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏文强 on 2016/6/29.
 */
public class AppLockDao {
    private Context context;
    private AppLockOpenHelper helper;

    public AppLockDao(Context context) {
        this.context = context;
        helper = new AppLockOpenHelper(context);
    }

    public void add(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        db.insert("info", null, values);
        db.close();
        //自定义注册一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("com.wwq.change"), null);
    }

    public void delete(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("info", "packagename = ?", new String[]{packageName});
        db.close();
        //自定义注册一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("com.wwq.change"), null);
    }

    public boolean find(String packageName) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", null, "packagename = ?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部的锁定的包名
     * @return
     */
    public List<String> findAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, null, null, null, null, null);
        List<String> packnames = new ArrayList<String>();
        while(cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}
