package com.hsj.common.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author:hsj
 * @Date:2019-07-23
 * @Class:ThreadManager
 * @Desc:线程管理器
 */
public final class ThreadManager {

    private ThreadManager() {
        throw new IllegalAccessError("ThreadManager can't be instance");
    }

//======================================Main Handler================================================

    /**
     * 主线程Handler锁
     */
    private static final Object MAIN_HANDLER_LOCK = new Object();

    /**
     * 主线程Handler
     */
    private static Handler mainHandler;

    /**
     * 取得UI线程Handler
     *
     * @return MainHandler
     */
    public static Handler getMainHandler() {
        if (mainHandler == null) {
            synchronized (MAIN_HANDLER_LOCK) {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mainHandler;
    }

//========================================Executor==================================================

    /**
     * 指定线程池队列长度
     */
    private static final int QUEUE_SIZE      = 100;

    /**
     * 指定线程池核心线程数量
     */
    private static final int CORE_POOL_SIZE  = 1;

    /**
     * 指定线程池最大线程数量
     */
    private static final int MAX_POOL_SIZE   = 3;

    /**
     * 指定空闲线程存活时间/秒
     */
    private static final int KEEP_ALIVE_TIME = 1;

    /**
     * 线程池
     */
    private static final ExecutorService EXECUTOR;

    /**
     * 队列限制长度，防止队列任务过多发生OOM
     */
    private static final LinkedBlockingQueue<Runnable> QUEUE;

    static {
        QUEUE = new LinkedBlockingQueue<>(QUEUE_SIZE);
        EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, QUEUE);
    }

    /**
     * 线程池任务
     *
     * @param run Runnable
     */
    public static void executeOnPool(Runnable run) {
        if (run != null) EXECUTOR.execute(run);
    }

    /**
     * 线程池任务
     *
     * @param run Runnable
     * @return submit result
     */
    @Nullable
    public static Future<?> submitOnPool(Runnable run) {
        return run != null ? EXECUTOR.submit(run) : null;
    }

    /**
     * 清空线程池中所有任务
     *
     * @param run Runnable
     * @return result
     */
    public static boolean removePoolTask(Runnable run) {
        return run == null || QUEUE.remove(run);
    }

    /**
     * 清空线程池中所有任务
     */
    public static void clearPoolTask() {
        QUEUE.clear();
    }

//=====================================SUB Thread===================================================

    /**
     * 副线程Handler
     */
    private static HandlerThread SUB_THREAD;

    /**
     * 副线程执行比较块的任务
     */
    private static Handler SUB_THREAD_HANDLER;

    /**
     * 获取副线程
     *
     * @return SubThread
     */
    public static Thread getSubThread() {
        if (SUB_THREAD == null) {
            getSubThreadHandler();
        }
        return SUB_THREAD;
    }

    /**
     * 获取副线程Handler
     *
     * @return SubThread Handler
     */
    public static Handler getSubThreadHandler() {
        if (SUB_THREAD_HANDLER == null) {
            synchronized (ThreadManager.class) {
                SUB_THREAD = new HandlerThread("thread_sub");
                SUB_THREAD.start();
                SUB_THREAD_HANDLER = new Handler(SUB_THREAD.getLooper());
            }
        }
        return SUB_THREAD_HANDLER;
    }

    /**
     * 获取副线程Looper
     *
     * @return SubThread Looper
     */
    public static Looper getSubThreadLooper() {
        return getSubThreadHandler().getLooper();
    }

    /**
     * 副线程:线程优先级高、执行速度快、
     *
     * @param run Runnable
     * @return execute result
     */
    public static boolean executeOnSubThread(Runnable run) {
        return run != null && getSubThreadHandler().post(run);
    }

    /**
     * 清空副线程任务
     */
    public static void clearSubTask(Runnable run) {
        if (run != null && SUB_THREAD_HANDLER != null) {
            SUB_THREAD_HANDLER.removeCallbacks(run);
        }
    }

}

