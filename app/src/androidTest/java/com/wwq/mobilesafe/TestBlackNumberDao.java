package com.wwq.mobilesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.wwq.db.BlackNumberDao;
import com.wwq.entity.BlackNumberInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by 魏文强 on 2016/5/17.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    public Context mContext;

    @Override
    protected void setUp() throws Exception {
        mContext = getContext();
        super.setUp();
    }

    public void testAdd() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random();

        for (int i = 0; i < 200; i++) {
            long number = 13612345678l + i;
            dao.add(number + "", String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testDelete(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean delete = dao.delete("13612345678");
        assertEquals(true, delete);
    }

    public void testFind(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        String mode = dao.findNumber("13612345679");
        assertEquals("1", mode);
    }

    public void testFindAll(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        List<BlackNumberInfo> blackNumberInfoList = dao.findAll();
        for(BlackNumberInfo blackNumberInfo : blackNumberInfoList){
            System.out.println(blackNumberInfo.getNumber() + "//"+blackNumberInfo.getMode());
        }
    }
}
