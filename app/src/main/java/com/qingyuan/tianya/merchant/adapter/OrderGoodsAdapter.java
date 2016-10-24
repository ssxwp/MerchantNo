package com.qingyuan.tianya.merchant.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.bean.OrderGoodsBean;
import com.qingyuan.tianya.merchant.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by gaoyanjun on 2016/8/7.
 */
public class OrderGoodsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<OrderGoodsBean> mList;

    public OrderGoodsAdapter(Context context, ArrayList<OrderGoodsBean> mList) {
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
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.ordergoods,parent,false);
            holder = new ViewHolder();
            holder.name_text= ((TextView) convertView.findViewById(R.id.order_name_text));
            holder.num_text= ((TextView) convertView.findViewById(R.id.order_nub_text));
            holder.price_text= ((TextView) convertView.findViewById(R.id.order_price_text2));
            holder.vip_price= ((TextView) convertView.findViewById(R.id.order_price_text));
            holder.head = (SimpleDraweeView)convertView.findViewById(R.id.order_head);
            holder.tv_all = (TextView)convertView.findViewById(R.id.order_transall_text1);
            convertView.setTag(holder);
        }else {
            holder = ((ViewHolder) convertView.getTag());
        }
        if (StringUtil.isNotEmpty(mList.get(position).getPic())){
            Uri uri = Uri.parse(mList.get(position).getPic());
            holder.head.setImageURI(uri);
        }else {
            holder.head.setImageResource(R.mipmap.default_round_head);
        }
        holder.name_text.setText("商品名："+mList.get(position).getName());
        holder.num_text.setText("共"+mList.get(position).getNub()+"件");
        holder.price_text.setText("原   价:￥"+mList.get(position).getPrice());
        holder.vip_price.setText("会员价:￥"+mList.get(position).getHprice());
        holder.tv_all.setText("￥"+Double.parseDouble(mList.get(position).getPrice())*Double.parseDouble(mList.get(position).getNub()));
        //+"(￥"+Double.parseDouble(mList.get(position).getHprice())*Double.parseDouble(mList.get(position).getNub())+")");
        return convertView;
    }

    private class ViewHolder{
        public TextView name_text,price_text,num_text,vip_price,tv_all;
        public SimpleDraweeView head;
    }
}
