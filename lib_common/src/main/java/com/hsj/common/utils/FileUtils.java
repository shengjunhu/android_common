package com.hsj.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

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
     * @param context
     * @param uri
     * @return
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
     * 删除目录
     *
     * @param file
     * @return
     */
    public static boolean delete(File file) {
        if (file == null || !file.exists()) return true;
        if (file.isFile()) return file.delete();
        File[] files = file.listFiles();
        if (files == null || files.length == 0) return true;
        for (File f : files) {
            if (f.isFile()) {
                if (!f.delete()) {
                    Logger.w("file: " + f.getAbsolutePath() + " delete failed!");
                }
            } else {
                delete(f);
            }
        }
        return true;
    }

}
