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
import com.qingyuan.tianya.merchant.bean.GoodsBean;
import com.qingyuan.tianya.merchant.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by gaoyanjun on 2016/8/24.
 */
public class GoodsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<GoodsBean> mList;

    public GoodsAdapter(Context context, ArrayList<GoodsBean> mList) {
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
        ViewHolder holder=null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.goodslist,null);
            holder = new ViewHolder();
            holder.head = ((SimpleDraweeView) convertView.findViewById(R.id.goods_pic));
            holder.name = (TextView)convertView.findViewById(R.id.goods_name);
            holder.price = (TextView)convertView.findViewById(R.id.goods_price);
            holder.h_price = (TextView)convertView.findViewById(R.id.goods_h);
            convertView.setTag(holder);
        }else {
            holder =((ViewHolder) convertView.getTag());
        }
        if (StringUtil.isNotEmpty(mList.get(position).getPic())){
            Uri uri = Uri.parse(mList.get(position).getPic());
            holder.head.setImageURI(uri);
        }else {
            holder.head.setImageResource(R.mipmap.default_round_head);
        }
        holder.name.setText(mList.get(position).getGoods_name());
        holder.price.setText("市场价：￥"+mList.get(position).getS_price());
        holder.h_price.setText("会员价：￥"+mList.get(position).getH_price());
        return convertView;
    }
    class ViewHolder{
        public SimpleDraweeView head;
        public TextView name,price,h_price;
    }
}
