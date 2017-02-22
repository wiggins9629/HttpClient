package com.wiggins.httpclient.listener;

/**
 * @Description 处理数据返回接口
 * @Author 一花一世界
 * @Time 2017/2/20 9:48
 */

public interface DataCallBack<T> {
    void onSuccess(T responseData);

    void onError(T responseData);
}