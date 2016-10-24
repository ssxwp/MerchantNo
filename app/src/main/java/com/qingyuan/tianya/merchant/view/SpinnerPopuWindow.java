package com.qingyuan.tianya.merchant.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;


import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.adapter.AbstractSpinerAdapter;
import com.qingyuan.tianya.merchant.adapter.NormalSpinerAdapter;

import java.util.List;

/**
 * Created by gaoyanjun on 2016/4/20.
 * spinner下拉view
 */
public class SpinnerPopuWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private NormalSpinerAdapter mAdapter;
    private AbstractSpinerAdapter.IOnItemSelectListener mItemSelectListener;


    public SpinnerPopuWindow(Context context)
    {
        super(context);

        mContext = context;
        init();
    }


    public void setItemListener(AbstractSpinerAdapter.IOnItemSelectListener listener){
        mItemSelectListener = listener;
    }


    private void init()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);


        mListView = (ListView) view.findViewById(R.id.spinner_listview);


        mAdapter = new NormalSpinerAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }


    public void refreshData(List<String> list, int selIndex)
    {
        if (list != null && selIndex  != -1)
        {
            mAdapter.refreshData(list, selIndex);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        if (mItemSelectListener != null){
            mItemSelectListener.onItemClick(pos);
        }
    }


}
