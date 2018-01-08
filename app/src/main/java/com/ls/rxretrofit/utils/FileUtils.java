package com.ls.rxretrofit.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Created by liusong on 2018/1/3.
 */

public class FileUtils {

    /**
     * 保存图片到本地
     *
     * @param context
     * @param data
     * @return
     */
    public static String saveImageStream2Local(Context context, ResponseBody data) {
        try {
            String fileUrl = context.getExternalCacheDir() + File.separator + "TEMP_IMAGE.jpg";
            FileOutputStream fos = new FileOutputStream(fileUrl);
            InputStream is = data.byteStream();

            byte[] buffer = new byte[1024 * 10];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            is.close();
            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
