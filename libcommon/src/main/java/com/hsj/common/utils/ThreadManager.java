package com.hsj.common.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

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

    /**
     * 主线程Handler
     */
    private static Handler mainHandler;
    /**
     * 主线程Handler锁
     */
    private static final Object MAIN_HANDLER_LOCK = new Object();

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

//=====================================Executor=====================================================

    /**
     * 指定线程池队列长度
     */
    private static final int QUEUE_SIZE = 1000;
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 3;
    private static final int KEEP_ALIVE_TIME = 3;
    /**
     * 线程池
     */
    private static ExecutorService mExecutor;
    /**
     * 队列限制长度，防止队列任务过多发生OOM
     */
    private static LinkedBlockingQueue<Runnable> mQueue;

    /**
     * 初始化线程池
     */
    private static void createPool() {
        if (mQueue == null) {
            mQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        }
        if (mExecutor == null) {
            //Android端通常可直接设置int corePoolSize = 3,int maximumPoolSize = 5
            mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                    KEEP_ALIVE_TIME, TimeUnit.SECONDS, mQueue);
        }
    }

    /**
     * 线程池任务
     *
     * @param run Runnable
     */
    public static boolean executeOnPool(Runnable run) {
        if (run == null) return false;
        if (mExecutor == null) createPool();
        mExecutor.execute(run);
        return true;
    }

    /**
     * 线程池任务
     *
     * @param run Runnable
     * @return submit result
     */
    public static Future<?> submitOnPool(Runnable run) {
        if (run == null) return null;
        if (mExecutor == null) createPool();
        return mExecutor.submit(run);
    }

    /**
     * 清空线程池中所有任务
     *
     * @param run Runnable
     * @return result
     */
    public static boolean removePoolTask(Runnable run) {
        if (run == null || mQueue == null) return true;
        return mQueue.remove(run);
    }

    /**
     * 清空线程池中所有任务
     */
    public static void clearPoolTask() {
        if (mQueue != null) {
            mQueue.clear();
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdownPool() {
        if (mQueue != null) {
            mQueue.clear();
            mQueue = null;
        }
        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }
    }

//=====================================SUB Thread===================================================

    /**
     * 副线程执行比较块的任务:
     */
    private static Handler SUB_THREAD_HANDLER;
    private static HandlerThread SUB_THREAD;

    /**
     * 副线程：线程优先级高、执行速度快、
     *
     * @param run Runnable
     * @return execute result
     */
    public static boolean executeOnSubThread(Runnable run) {
        return run != null && getSubThreadHandler().post(run);
    }

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
                SUB_THREAD = new HandlerThread("sub_thread");
                SUB_THREAD.start();
                SUB_THREAD_HANDLER = new Handler(SUB_THREAD.getLooper());
            }
        }
        return SUB_THREAD_HANDLER;
    }

    /**
     * 获取副线程Handler
     *
     * @return SubThread Looper
     */
    public static Looper getSubThreadLooper() {
        return getSubThreadHandler().getLooper();
    }

    /**
     * 清空副线程任务
     */
    public static void clearSubTask(Runnable run) {
        if (run == null || SUB_THREAD_HANDLER == null) return;
        SUB_THREAD_HANDLER.removeCallbacks(run);
    }

    /**
     * shutdown副线程[非必要情况不建议关闭]
     */
    public static void shutdownSubThread() {
        if (SUB_THREAD_HANDLER != null) {
            SUB_THREAD_HANDLER.getLooper().quitSafely();
        }
        if (SUB_THREAD != null) {
            SUB_THREAD.quitSafely();
        }
        SUB_THREAD_HANDLER = null;
        SUB_THREAD = null;
    }

}

