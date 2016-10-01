package com.wwq.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 魏文强 on 2016/4/22.
 */
public class ToastUtils {
    public static void showToast(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
}
