package com.aiven.hfl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.aiven.hfl.R;
import com.aiven.hfl.bean.HttpLogBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : AivenLi
 * @date : 2022/10/29 11:12
 * @desc :
 */
public class HttpLogAdapter extends RecyclerView.Adapter<HttpLogAdapter.ViewHolder> {

    private List<HttpLogBean> mData;
    private Context mContext;

    public HttpLogAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void updateData(List<HttpLogBean> mData) {
        int size = this.mData.size();
        this.mData.clear();
        notifyItemRangeRemoved(0, size);
        appendData(mData);
    }

    public void appendData(List<HttpLogBean> mData) {
        int size = this.mData.size();
        this.mData.addAll(mData);
        checkItemSize();
        notifyItemRangeInserted(size, this.mData.size());
    }

    public void appendData(HttpLogBean httpLogBean) {
        mData.add(httpLogBean);
        checkItemSize();
        notifyItemRangeInserted(mData.size() - 1, mData.size());
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    private void checkItemSize() {
        if (this.mData.size() > 50) {
            int removeCount = this.mData.size() - 50;
            this.mData.subList(0, removeCount).clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_http_log, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HttpLogBean item = mData.get(position);
        holder.tvUrl.setText(item.getUrl());
        holder.tvMethod.setText(item.getMethod());
        holder.tvCode.setText(getString(R.string.number, item.getCode()));
        holder.tvHeaders.setText(item.getHeaders());
        holder.tvBody.setText(item.getBody());
        holder.tvTimes.setText(getString(R.string.ms, item.getMilliseconds()));
        holder.tvData.setText(item.getData());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String getString(@StringRes int id) {
        return mContext.getString(id);
    }

    private String getString(@StringRes int id, int i) {
        return mContext.getString(id, i);
    }

    private String getString(@StringRes int id, long i) {
        return mContext.getString(id, i);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUrl;
        public TextView tvMethod;
        public TextView tvCode;
        public TextView tvHeaders;
        public TextView tvBody;
        public TextView tvTimes;
        public TextView tvData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUrl     = findViewById(R.id.tv_url_value);
            tvMethod  = findViewById(R.id.tv_method_value);
            tvCode    = findViewById(R.id.tv_code_value);
            tvHeaders = findViewById(R.id.tv_headers_value);
            tvBody    = findViewById(R.id.tv_body_value);
            tvTimes   = findViewById(R.id.tv_times_value);
            tvData    = findViewById(R.id.tv_data_value);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return itemView.findViewById(id);
        }
    }
}
