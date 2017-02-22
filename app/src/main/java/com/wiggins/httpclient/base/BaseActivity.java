package com.wiggins.httpclient.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * @Description 所有Activity的基类
 * @Author 一花一世界
 * @Time 2017/2/20 9:42
 */

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.finishActivity(this);
    }
}
