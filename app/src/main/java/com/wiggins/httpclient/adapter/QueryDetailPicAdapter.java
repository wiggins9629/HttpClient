package com.wiggins.httpclient.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiggins.httpclient.R;
import com.wiggins.httpclient.bean.QueryDetailPicUrl;
import com.wiggins.httpclient.utils.BitmapUtil;
import com.wiggins.httpclient.utils.StringUtil;

import java.util.List;

/**
 * @Description 历史上的今天 - 事件详情图片列表适配器
 * @Author 一花一世界
 * @Time 2017/2/21 9:49
 */

public class QueryDetailPicAdapter extends BaseAdapter {

    private List<QueryDetailPicUrl> data;
    private LayoutInflater inflater;

    public QueryDetailPicAdapter(List<QueryDetailPicUrl> data, Context context) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<QueryDetailPicUrl> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_today_history_detail, null);
            holder = new ViewHolder();
            holder.mIvUrl = (ImageView) convertView.findViewById(R.id.iv_url);
            holder.mTvTitle = (TextView) convertView.findViewById(R.id.tv_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QueryDetailPicUrl item = data.get(position);
        asyncloadImage(holder.mIvUrl, item.getUrl());
        if (StringUtil.isEmpty(item.getPic_title())) {
            holder.mTvTitle.setVisibility(View.GONE);
        } else {
            holder.mTvTitle.setVisibility(View.VISIBLE);
            holder.mTvTitle.setText(item.getPic_title());
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView mIvUrl;
        private TextView mTvTitle;
    }

    private void asyncloadImage(final ImageView imageView, final String uri) {
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (imageView != null && uri != null) {
                        imageView.setImageBitmap(bitmap);
                    }

                }
            }
        };
        // 子线程，开启子线程去下载或者去缓存目录找图片，并且返回图片在缓存目录的地址
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //这个URI是图片下载到本地后的缓存目录中的URI
                    if (uri != null) {
                        Bitmap bitmap = BitmapUtil.getHttpBitmap(uri);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = bitmap;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }
}
