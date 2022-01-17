package com.hsj.common.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import com.hsj.common.utils.FileUtils;
import com.hsj.common.utils.ThreadManager;
import com.hsj.common.utils.DateUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

/**
 * @Author:hsj
 * @Date:2019-07/22
 * @Class:AppManager
 * @Description:收集AppActivity
 */
public final class AppManager {

    private static final Integer LOG_DIR_SIZE = 3;
    private static final String TIME_FORMAT = "yyyy-MM-dd";
    private static final AppManager INSTANCE = new AppManager();

    private AppManager() {
    }

    /**
     * 实例化 AppManager
     *
     * @return
     */
    public static AppManager getInstance() {
        return INSTANCE;
    }

//===========================================Activity===============================================

    /**
     * Stack 是先入后出/ 后入先出的集合，
     */
    private Stack<SoftReference<Activity>> activityStack = new Stack<>();

    /**
     * 给Activity在onCreate用的
     *
     * @param activity
     */
    public void addActivity(@NonNull Activity activity) {
        activityStack.add(new SoftReference<>(activity));
    }

    /**
     * 给MyApp用来获取最顶端的Activity用的
     *
     * @return
     */
    public Activity getTopActivity() {
        return activityStack.isEmpty() ? null : activityStack.peek().get();
    }

    /**
     * 给Activity在onDestroy用的
     *
     * @param activity
     */
    public void removeActivity(@NonNull Activity activity) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            if (activityStack.get(i).get() == activity) {
                activityStack.remove(i);
                return;
            }
        }
    }

    /**
     * 关闭指定activity
     *
     * @param obj
     */
    public void removeActivity(Object obj) {
        if (obj == null) return;
        Activity activity = (Activity) obj;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            if (activity == activityStack.get(i).get()) {
                activityStack.remove(i);
                activity.finish();
            }
        }
    }

    /**
     * 清空所有Activity，应用推出
     */
    public void removeAll() {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            activityStack.get(i).get().finish();
            activityStack.remove(i);
        }
    }

//=========================================Log======================================================

    /**
     * 初始化日志模块
     *
     * @param context
     */
    public void initLog(Context context) {
        if (context == null) return;
        ThreadManager.executeOnPool(() -> {
            //设置异常日志捕捉
            Thread.setDefaultUncaughtExceptionHandler((thread, e) ->
                    new Thread(() -> logInfo(context, e)).start());
            //检查之前存储的日志
            checkLog(context);
        });
    }

    /**
     * 检测日志文件
     *
     * @param context
     */
    @WorkerThread
    private void checkLog(@NonNull Context context) {
        File dir = getLogDir(context);
        if (dir == null) return;
        File[] dirs = dir.listFiles();
        //超过三个日期的目录,保留最大的三个日期，删除其他
        if (dirs == null || dirs.length <= LOG_DIR_SIZE) return;
        List<Long> logDirTime = new ArrayList<>(dirs.length);
        for (File f : dirs) {
            logDirTime.add(DateUtils.getTimeStamp(TIME_FORMAT, f.getName()));
        }
        Collections.sort(logDirTime);
        logDirTime.subList(0, LOG_DIR_SIZE - 1);
        for (File f : dirs) {
            long dirTime = DateUtils.getTimeStamp(TIME_FORMAT, f.getName());
            if (logDirTime.contains(dirTime)) {
                FileUtils.delete(f);
            }
        }
    }

    /**
     * 错误日志信息
     *
     * @param context
     * @param e
     */
    @WorkerThread
    private void logInfo(@NonNull Context context, @NonNull Throwable e) {
        //打印日志
        e.printStackTrace();

        //保存日志
        saveLog(context, e);

        //崩溃提示
        Looper.prepare();
        showCrashDialog();
        Looper.loop();
    }

    /**
     * 保存错误日志
     *
     * @param context
     * @param ex
     * @return
     */
    private void saveLog(Context context, Throwable ex) {
        File dir = getLogDir(context);
        if (dir == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA);
        String day = sdf.format(new Date());
        dir = new File(dir, day);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                return;
            }
        }
        File file = new File(dir, System.currentTimeMillis() + ".log");
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            fileWriter = new FileWriter(file);
            printWriter = new PrintWriter(fileWriter);

            printWriter.append("========Device Information==========\n");
            printWriter.append(String.format("DEVICE_ID         %s\n", AppConstants.deviceId));
            printWriter.append(String.format("BRAND             %s\n", Build.BRAND));
            printWriter.append(String.format("MODEL             %s\n", Build.MODEL));
            printWriter.append(String.format("CPU_ABI           %s\n", Build.CPU_ABI));
            printWriter.append(String.format("CPU_ABI2          %s\n", Build.CPU_ABI2));
            printWriter.append(String.format("MANUFACTURER      %s\n", Build.MANUFACTURER));
            printWriter.append(String.format("SYSTEM_VERSION    %s\n\n", Build.VERSION.SDK_INT));

            printWriter.append("========APP Information=============\n");
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            printWriter.append(String.format("Account           %s\n", AppConstants.account));
            printWriter.append(String.format("VersionCode       %s\n", packageInfo.versionCode));
            printWriter.append(String.format("VersionName       %s\n\n", packageInfo.versionName));

            printWriter.append("==========Exception================\n");
            ex.printStackTrace(printWriter);

            fileWriter.flush();
            printWriter.flush();
        } catch (IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                ex.printStackTrace();
            }
        }
        //upload log file
    }

    /**
     * 获取日志目录
     *
     * @param context
     * @return
     */
    private File getLogDir(@NonNull Context context) {
        File dir = context.getExternalFilesDir("log");
        if (dir == null) {
            dir = context.getFilesDir();
            dir = new File(dir, "log");
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }
        return dir;
    }

    /**
     * 显示崩溃提示
     */
    private void showCrashDialog() {

    }

}