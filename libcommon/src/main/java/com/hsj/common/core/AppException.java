package com.hsj.common.core;

/**
 * @Author:hsj
 * @Date:2019-07-23
 * @Class:AppException
 * @Description:异常基类
 */
public final class AppException extends Throwable {

    public static final int LOW_DISK_CODE = -9;
    public static final int LOW_MEMORY_CODE = -8;

    private AppException() {
    }

    public AppException(int code) {
        super(parseException(code));
    }

    private static String parseException(int code) {
        switch (code) {
            case LOW_DISK_CODE:
                return "Low Disk Space";
            case LOW_MEMORY_CODE:
                return "Low Memory";
            default:
                return "UnKnow Exception";
        }
    }

}