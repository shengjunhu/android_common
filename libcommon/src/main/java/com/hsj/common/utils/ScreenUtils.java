package com.hsj.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * @Author:hsj
 * @Date:2019-07-23
 * @Class:ScreenUtils
 * @Desc:屏幕工具类
 */
public final class ScreenUtils {

    //////////////////////////////////////////////////////////////
    // ScreenUtils
    //     1、dp2px
    //     2、sp2px
    //     3、px2dp
    //     4、px2sp
    //     5、StatusBar
    //////////////////////////////////////////////////////////////

    private ScreenUtils() {
        throw new IllegalAccessError("ScreenUtils can't be instance");
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static float dp2px(@NonNull Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spValue
     * @return
     */
    public static float sp2px(@NonNull Context context, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static float px2dp(@NonNull Context context, float pxValue) {
        return (pxValue / context.getResources().getDisplayMetrics().density);
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static float px2sp(@NonNull Context context, float pxValue) {
        return (pxValue / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 获取设备分辨率( 内容显示分辨率)
     *
     * @param context
     */
    public static int[] getResolution(@NonNull Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /**
     * 获取设备分辨率（屏幕分辨率）
     *
     * @param context
     */
    public static int[] getScreenResolution(@NonNull Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new int[]{dm.widthPixels, dm.heightPixels + getNavigationBarHeight(context)};
    }

    /**
     * 获取设备顶部状态栏高度
     *
     * @param context
     */
    public static int getStatusBarHeight(@NonNull Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获状态栏高度
     */
    public static int getStatusBarHeight2(@NonNull Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            Object obj2 = field.get(obj);
            if (obj2 == null) return statusBarHeight;
            int temp = Integer.parseInt(obj2.toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(temp);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取设备底部导航栏高度
     *
     * @param context
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 隐藏状态栏
     * <p>也就是设置全屏，一定要在setContentView之前调用，否则报错</p>
     * <p>此方法Activity可以继承AppCompatActivity</p>
     * <p>启动的时候状态栏会显示一下再隐藏，比如QQ的欢迎界面</p>
     * <p>在配置文件中Activity加属性android:theme="@android:style/Theme.NoTitleBar.Fullscreen"</p>
     * <p>如加了以上配置Activity不能继承AppCompatActivity，会报错</p>
     *
     * @param activity
     */
    public static void hideStatusBar(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 透明状态栏 setContentView之前调用有效
     * >21
     *
     * @param activity
     */
    public static void setSystemBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

}