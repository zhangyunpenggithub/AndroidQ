package com.qihoo360.myapplication.util;

import java.text.NumberFormat;

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
public class FileUtil {


    public static NumberFormat mSizeFormat;

    private static final float NUMBER_SIZE_K = 1024f;

    private static final float NUMBER_SIZE_M = 1048576f;

    private static final float NUMBER_SIZE_G = 1073741824f;

    static {
        mSizeFormat = NumberFormat.getInstance();
        mSizeFormat.setMaximumFractionDigits(2);
    }

    /**
     * 手机卫士首页v8获取了微信的垃圾数量，这个数值我们的转化和清理组的不一样，所以我们把他们的方法复制到下面了
     * @param size
     * @return
     */
    public static final String getFormatSizeSource(long size) {
        String[] result = new String[2];
        NumberFormat sizeFormat = NumberFormat.getInstance();
        sizeFormat.setMaximumFractionDigits(0);
        sizeFormat.setMinimumFractionDigits(0);
        if (size >= 1000 * NUMBER_SIZE_M) {
            result[1] = "GB";
            if (size > 999 * NUMBER_SIZE_G) {
                // 当值大于999G时，显示999G(不存在的情况)
                result[0] = "999";
            } else if (size >= 100 * NUMBER_SIZE_G) {
                // 876G
                sizeFormat.setMaximumFractionDigits(0);
                sizeFormat.setMinimumFractionDigits(0);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_G);
            } else if (size >= 10 * NUMBER_SIZE_G) {
                // 46.9G
                sizeFormat.setMaximumFractionDigits(1);
                sizeFormat.setMinimumFractionDigits(1);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_G);
            } else {
                // 8.78G
                sizeFormat.setMaximumFractionDigits(2);
                sizeFormat.setMinimumFractionDigits(2);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_G);
            }
        } else if (size >= 1000 * NUMBER_SIZE_K) {
            result[1] = "MB";
            if (size >= 999 * NUMBER_SIZE_M) {
                // 999.xMB垃圾会向上取整显示1,000MB，特殊处理
                result[0] = "999";
            } else if (size >= 100 * NUMBER_SIZE_M) {
                // 123M
                sizeFormat.setMaximumFractionDigits(0);
                sizeFormat.setMinimumFractionDigits(0);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_M);
            } else if (size >= 10 * NUMBER_SIZE_M) {
                // 34.8M
                sizeFormat.setMaximumFractionDigits(1);
                sizeFormat.setMinimumFractionDigits(1);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_M);
            } else {
                // 7.98M
                sizeFormat.setMaximumFractionDigits(2);
                sizeFormat.setMinimumFractionDigits(2);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_M);
            }
        } else if (size >= 1000) {
            result[1] = "KB";
            if (size >= 999 * NUMBER_SIZE_K) {
                // 999.xKB垃圾会向上取整显示1,000KB，特殊处理
                result[0] = "999";
            } else if (size >= 100 * NUMBER_SIZE_K) {
                // 798K
                sizeFormat.setMaximumFractionDigits(0);
                sizeFormat.setMinimumFractionDigits(0);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_K);
            } else if (size >= 10 * NUMBER_SIZE_K) {
                // 49.3K
                sizeFormat.setMaximumFractionDigits(1);
                sizeFormat.setMinimumFractionDigits(1);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_K);
            } else {
                // 9.99k
                sizeFormat.setMaximumFractionDigits(2);
                sizeFormat.setMinimumFractionDigits(2);
                result[0] = sizeFormat.format(size / NUMBER_SIZE_K);
            }
        } else {
            if (size < 0) {
                size = 0;
            }
            result[1] = "B";
            result[0] = String.valueOf(size);
        }
        return result[0] + result[1];
    }
}
