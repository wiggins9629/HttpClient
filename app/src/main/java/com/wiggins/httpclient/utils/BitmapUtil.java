package com.wiggins.httpclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.wiggins.httpclient.R;
import com.wiggins.httpclient.base.BaseApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description Bitmap工具类
 * @Author 一花一世界
 * @Time 2017/2/21 10:05
 */

public class BitmapUtil {

    public static Context getContext() {
        return BaseApplication.getContext();
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * @param url 　本地或网络的url
     * @return url的bitmap
     */
    public static Bitmap getLocalOrNetBitmap(String url) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_default);
        if (url != null) {
            InputStream in = null;
            BufferedOutputStream out = null;
            try {
                //读取图片输入流
                in = new BufferedInputStream(new URL(url).openStream(), Constant.IO_BUFFER_SIZE);
                //准备输出流
                final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                out = new BufferedOutputStream(dataStream, Constant.IO_BUFFER_SIZE);
                byte[] b = new byte[Constant.IO_BUFFER_SIZE];
                int read;
                //将输入流变为输出流
                while ((read = in.read(b)) != -1) {
                    out.write(b, 0, read);
                }
                out.flush();
                //将输出流转换为bitmap
                byte[] data = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                data = null;
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return bitmap;
            }
        }
        return bitmap;
    }
}
