package com.wwq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.wwq.entity.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏文强 on 2016/5/15.
 */
public class BlackNumberDao {

    private BlackNumberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberOpenHelper(context);
    }

    //黑名单号码，拦截模式
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("mode", mode);
        long rowid = db.insert("blacknumber", null, contentValues);
        if (rowid == -1) {
            return false;
        } else {
            return true;
        }
    }

    //电话号码
    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowNumber = db.delete("blacknumber", "number=?", new String[]{number});
        if (rowNumber == 0) {
            return false;
        } else {
            return true;
        }
    }

    //修改拦截模式
    public boolean changNumberMode(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("mode", mode);
        int rowNumber = db.update("blacknumber", contentValues, "number=?", new String[]{number});
        if (rowNumber == 0) {
            return false;
        } else {
            return true;
        }
    }

    //通过电话号码进行查找
    public String findNumber(String number) {
        String mode = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    //查询所有的黑名单
    public List<BlackNumberInfo> findAll() {
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(3000);
        return blackNumberInfos;
    }

    /**
     * 分页加载数据
     * @param pageNumber 表示当前是那一页
     * @param pageSize 表示每一页有多少条数据
     * @return
     * limit:限制，当前要显示多少数据
     * offset:跳过，从第几条开始
     */
    public List<BlackNumberInfo> findPar(int pageNumber, int pageSize){
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number, mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(pageSize), String.valueOf(pageNumber * pageSize)});
        while(cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }
    //获取总的记录数
    public int getTotalNumber(){
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 分批加载数据
     * @param startIndex 开始位置
     * @param maxCount 每页展示的最大数目
     * @return
     */
    public List<BlackNumberInfo> findPar2(int startIndex, int maxCount){
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number, mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        while(cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }
}
