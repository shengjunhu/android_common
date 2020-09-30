package com.hsj.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * @Author:hsj
 * @Date:2020-06-28
 * @Class:FileUtils
 * @Desc:文件操作工具类
 */
public final class FileUtils {

    //////////////////////////////////////////////////////////////
    // FileUtils
    //     1、保存文件
    //     2、复制文件
    //     3、移动文件
    //     4、重命名文件
    //     5、删除文件
    //////////////////////////////////////////////////////////////

    private FileUtils() {
        throw new IllegalAccessError("FileUtils can't be instance");
    }

    /**
     * 通过Uri获取真实路径
     *
     * @param context 上下文
     * @param uri 文件Uri
     * @return 路径
     */
    public static String getPathFromURI(Context context, Uri uri) {
        if (context == null || uri == null) return null;
        final String scheme = uri.getScheme();
        if (scheme == null) return uri.getPath();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) return uri.getPath();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA},
                    null, null, null);
            if (cursor == null) return null;
            String path = null;
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (index > -1) {
                    path = cursor.getString(index);
                }
            }
            cursor.close();
            return path;
        }
        return null;
    }

    /**
     * 关闭IO流
     *
     * @param closeable 流
     */
    public static void ioClose(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除目录
     *
     * @param desFile 删除目标文件目录
     * @return 结果
     */
    public static boolean delete(File desFile) {
        if (desFile == null || !desFile.exists()) return true;
        if (desFile.isFile()) return desFile.delete();
        File[] files = desFile.listFiles();
        if (files == null || files.length == 0) return true;
        for (File file : files) {
            if (file.isDirectory()) {
                delete(file);
            } else {
                if (!file.delete()) {
                    Logger.w("File delete failed -> " + file.getAbsolutePath());
                }
            }
        }
        return true;
    }

    /**
     * 读取文件(比FileInputStream快)
     *
     * @param desFile 目标文件
     * @return 内容
     */
    public static byte[] readFile(File desFile) {
        if (desFile == null || desFile.isDirectory()) return null;
        byte[] data = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(desFile, "r");
            data = new byte[(int) raf.length()];
            raf.readFully(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ioClose(raf);
        }
        return data;
    }

    /**
     * 保存byte[]到文件
     *
     * @param data    数据内容
     * @param desFile 存在文件
     * @return 结果
     */
    public static boolean saveFile(byte[] data, File desFile) {
        if (desFile == null || desFile.isDirectory()) return false;
        boolean result = true;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(desFile, "rw");
            raf.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            ioClose(raf);
        }
        return result;
    }

}
