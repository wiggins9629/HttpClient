package com.wiggins.httpclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wiggins.httpclient.adapter.TodayHistoryQueryAdapter;
import com.wiggins.httpclient.base.BaseActivity;
import com.wiggins.httpclient.bean.ResultDesc;
import com.wiggins.httpclient.bean.TodayHistoryQuery;
import com.wiggins.httpclient.http.HttpRequest;
import com.wiggins.httpclient.utils.Constant;
import com.wiggins.httpclient.utils.StringUtil;
import com.wiggins.httpclient.utils.ToastUtil;
import com.wiggins.httpclient.utils.UIUtils;
import com.wiggins.httpclient.widget.TitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private MainActivity mActivity = null;
    private TitleView titleView;
    private EditText mEdtData;
    private Button mBtnQuery;
    private TextView mTvEmpty;
    private ListView mLvData;

    private List<TodayHistoryQuery> todayHistoryQuery;
    private TodayHistoryQueryAdapter todayHistoryQueryAdapter;
    private Gson gson = null;
    private String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        initView();
        initData();
        setLinstener();
    }

    private void initData() {
        if (gson == null) {
            gson = new Gson();
        }
        if (todayHistoryQuery == null) {
            todayHistoryQuery = new ArrayList<>();
        }
        if (todayHistoryQueryAdapter == null) {
            todayHistoryQueryAdapter = new TodayHistoryQueryAdapter(todayHistoryQuery, mActivity);
            mLvData.setAdapter(todayHistoryQueryAdapter);
        } else {
            todayHistoryQueryAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleView);
        titleView.setAppTitle(UIUtils.getString(R.string.event_list));
        titleView.setLeftImageVisibility(View.GONE);
        mEdtData = (EditText) findViewById(R.id.edt_data);
        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mLvData = (ListView) findViewById(R.id.lv_data);
        mLvData.setEmptyView(mTvEmpty);
    }

    private void setLinstener() {
        mBtnQuery.setOnClickListener(this);
        mLvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(mActivity, TodayHistoryDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("e_id", String.valueOf(todayHistoryQuery.get(position).getE_id()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private class TodayHistoryQueryTask extends AsyncTask<Map<String, String>, Void, ResultDesc> {

        @Override
        protected void onPostExecute(ResultDesc resultDesc) {
            super.onPostExecute(resultDesc);
            todayHistoryQuery.clear();
            if (resultDesc.getError_code() == 0) {
                try {
                    JSONArray jsonArray = new JSONArray(resultDesc.getResult());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        TodayHistoryQuery bean = gson.fromJson(jsonObject.toString(), TodayHistoryQuery.class);
                        todayHistoryQuery.add(bean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                todayHistoryQueryAdapter.setData(todayHistoryQuery);
                Log.e(Constant.LOG_TAG, "历史上的今天 - 事件列表:" + todayHistoryQuery.toString());
            } else {
                todayHistoryQueryAdapter.setData(todayHistoryQuery);
                ToastUtil.showText(resultDesc.getReason());
            }
        }

        @Override
        protected ResultDesc doInBackground(Map<String, String>... maps) {
            Map<String, String> map = new HashMap<>();
            map.put("key", Constant.APP_KEY);
            map.put("date", data);
            return HttpRequest.postRequest(Constant.queryEvent, map);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_query:
                data = mEdtData.getText().toString().trim();
                if (StringUtil.isEmpty(data)) {
                    ToastUtil.showText(UIUtils.getString(R.string.query_date_not_empty));
                    return;
                }
                //历史上的今天 事件列表
                new TodayHistoryQueryTask().execute();
                break;
        }
    }
}
