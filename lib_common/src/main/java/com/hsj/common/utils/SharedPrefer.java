package com.hsj.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Set;

/**
 * @Author:hsj
 * @Date:2019-07-23
 * @Class:SharedPrefer
 * @Desc:SharedPreferences工具类
 */
public final class SharedPrefer {

    ////////////////////////////////////////////////////////////////
    // SharedPreferences文件名如下：
    //     1、APP_INFO:     App相关信息缓存（版本号、该版本号第一次登陆）
    //     2、DEVICE_INFO:  设备相关信息缓存（）
    //     3、WORK_INFO:    业务相关缓存信息
    //     4、USER_INFO:    用户相关缓存信息(账号、密码、token、设备ID)
    //     5、ACTION_INFO:  App上次退出时未完成的信息
    ////////////////////////////////////////////////////////////////

    public static final String APP_INFO     =   "app_info";
    public static final String DEVICE_INFO  =   "device_info";
    public static final String USER_INFO    =   "user_info";
    public static final String WORK_INFO    =   "work_info";
    public static final String ACTION_INFO  =   "action_info";

    private String fileName = APP_INFO;

    public SharedPrefer(String fileName) {
        if (TextUtils.isEmpty(fileName)){
            throw new NullPointerException("fileName can't be null");
        }
        this.fileName = fileName;
    }

    /**
     * 可以put ：Number:int,long,float
     * String、boolean、Set<String>
     *
     * @param key
     * @param value
     */
    public void put(@NonNull Context context, @NonNull String key, @NonNull Object value) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (boolean) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (int) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else {
            edit.putStringSet(key, (Set<String>) value);
        }
        edit.apply();
    }

    /**
     * 可以remove ：String Key
     *
     * @param key
     */
    public void remove(@NonNull Context context, @NonNull String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.apply();
    }

    /**
     * 获取布尔
     *
     * @param key
     * @return
     */
    public boolean getBoolean(@NonNull Context context, @NonNull String key) {
        return getBoolean(context, key, false);
    }

    /**
     * 获取布尔
     *
     * @param key
     * @return
     */
    public boolean getBoolean(@NonNull Context context, @NonNull String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    public String getString(@NonNull Context context, @NonNull String key) {
        return getString(context, key, "");
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    public String getString(@NonNull Context context, @NonNull String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    /**
     * 获取字符串集合
     *
     * @param key
     * @return
     */
    public Set<String> getStringSet(@NonNull Context context, @NonNull String key) {
        return getStringSet(context, key, null);
    }

    /**
     * 获取字符串集合
     *
     * @param key
     * @return
     */
    public Set<String> getStringSet(@NonNull Context context, @NonNull String key, Set<String> defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getStringSet(key, defaultValue);
    }

    /**
     * 获取整数
     *
     * @param key
     * @return
     */
    public int getInt(@NonNull Context context, @NonNull String key) {
        return getInt(context, key, 0);
    }

    /**
     * 获取整数
     *
     * @param key
     * @return
     */
    public int getInt(@NonNull Context context, @NonNull String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    /**
     * 获取long
     *
     * @param key
     * @return
     */
    public long getLong(@NonNull Context context, @NonNull String key) {
        return getLong(context, key, 0);
    }

    /**
     * 获取long
     *
     * @param key
     * @return
     */
    public long getLong(@NonNull Context context, @NonNull String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    /**
     * 获取float
     *
     * @param key
     * @return
     */
    public float getFloat(@NonNull Context context, @NonNull String key) {
        return getFloat(context, key, 0.00f);
    }

    /**
     * 获取float
     *
     * @param key
     * @return
     */
    public float getFloat(@NonNull Context context, @NonNull String key, float defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultValue);
    }

    /**
     * 清空 SharedPreferences 中数据
     */
    public void clearAll(@NonNull Context context) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.apply();
    }

}