package com.qingyuan.tianya.merchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.bean.OrderBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gaoyanjun on 2016/8/25.
 */
public class OrderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<OrderBean> mList;

    public OrderAdapter(Context context, ArrayList<OrderBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.orderlist,null);
            holder = new ViewHolder();
            holder.order_name = (TextView)convertView.findViewById(R.id.order_name);
            holder.order_nub = (TextView)convertView.findViewById(R.id.order_nub);
            holder.order_status = (TextView)convertView.findViewById(R.id.order_status);
            holder.order_price = (TextView)convertView.findViewById(R.id.order_price);
            holder.order_utime = (TextView)convertView.findViewById(R.id.order_utime);
            convertView.setTag(holder);
        }else {
            holder = ((ViewHolder) convertView.getTag());
        }
        holder.order_name.setText(mList.get(position).getGoods_name());
        holder.order_nub.setText("数量："+mList.get(position).getNub());
        if (mList.get(position).getStatus().equals("1")){
            holder.order_status.setText("已支付");
            holder.order_status.setBackgroundResource(R.color.red);
        }else if (mList.get(position).getStatus().equals("2")){
            holder.order_status.setText("已结算");
            holder.order_status.setBackgroundResource(R.color.red);
        }
        holder.order_price.setText("￥"+mList.get(position).getPrice());
        holder.order_utime.setText(getStrTime(mList.get(position).getUtime()));
        return convertView;
    }
    class  ViewHolder{
        public TextView order_name,order_nub,order_status,order_price,order_utime;
    }

    // 将时间戳转为字符串
    public static String getStrTime(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }
}
