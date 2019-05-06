package com.qihoo360.myapplication.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * +----------------------------------------------------------------------
 * | 功能描述:
 * +----------------------------------------------------------------------
 * | 时　　间: 2019/4/29.
 * +----------------------------------------------------------------------
 * | 代码创建: 张云鹏
 * +----------------------------------------------------------------------
 * | 版本信息: V1.0.0
 * +----------------------------------------------------------------------
 **/
public class ToastUtil {

    public static void showToast(final Activity activity, final String content) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, content, Toast.LENGTH_LONG).show();
            }
        });
    }
}
