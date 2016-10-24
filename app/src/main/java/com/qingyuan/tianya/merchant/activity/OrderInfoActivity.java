package com.qingyuan.tianya.merchant.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.adapter.OrderGoodsAdapter;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.bean.OrderGoodsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderInfoActivity extends BaseActivity{

    private TextView tv_name,tv_phone,tv_address,tv_sure;
    private ListView mListView;
    private String o_id;
    private ArrayList<OrderGoodsBean> mList;
    private String g_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        tv_name = ((TextView) findViewById(R.id.ac_order_name_text));
        tv_phone = ((TextView) findViewById(R.id.ac_order_phone_text));
        tv_address = ((TextView) findViewById(R.id.ac_order_address));
        tv_sure = ((TextView) findViewById(R.id.sure_text));
        mListView = (ListView)findViewById(R.id.order_info_listview);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        o_id = bundle.getString("o_id");
        g_id = bundle.getString("g_id");
        getMessage(0);
    }

    private void getMessage(int i) {
        if (i == 0) {
            initProgressDialog();
        }
        mList = new ArrayList<>();
        String urlString = ApiConstant.ORDERINFO;
        RequestParams params = new RequestParams();
        params.put("order_id", o_id);
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        JSONObject customer = jObj.getJSONObject("message");
                        String status = customer.getString("status");
                        if (status.equals("8")){
                            tv_sure.setText("已接单");
                            tv_sure.setClickable(false);
                            tv_sure.setBackgroundColor(Color.GRAY);
                        }
                        JSONObject where = customer.getJSONObject("where");
                        String name = where.getString("name");
                        String phone = where.getString("phone");
                        String road = where.getString("road");
                        tv_name.setText("收货人："+name);
                        tv_phone.setText("电   话："+phone);
                        tv_address.setText("地   址："+road);
                        JSONArray array = customer.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject cus = array.getJSONObject(i);
                            String num = cus.getString("nub");
                            String goods_name = cus.getString("name");
                            String price = cus.getString("price");
                            String hprice = cus.getString("h_price");
                            String pic = cus.getString("pic");
                            //String pic = "http://m2.quanjing.com/2m/fod_liv002/fo-11171537.jpg";
                            OrderGoodsBean orderBean = new OrderGoodsBean(num, goods_name, price, hprice, pic);
                            mList.add(orderBean);
                        }
                        OrderGoodsAdapter adapter = null;
                        if (adapter == null) {
                            adapter = new OrderGoodsAdapter(OrderInfoActivity.this, mList);
                            mListView.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(mListView);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        //mListView.setOnItemClickListener(this);
                    } else {
                        Toast.makeText(OrderInfoActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    close();
                }
                close();
            }

            public void onFailure(Throwable arg0) { // 失败，调用
                close();
            }

            public void onFinish() { // 完成后调用，失败，成功，都要掉
                close();
            }
        });
    }

    @Override
    public void initClick() {
        tv_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sure_text:
                getSure();
                break;
        }
    }

    private void getSure() {
        initProgressDialog();
        RequestParams params = new RequestParams();
        params.put("order_id", o_id);
        HttpUtil.gets("http://114.215.78.102/index.php?s=/Api/Order/confirmOrder", params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        toast(jObj.getString("message"));
                        getMessage(1);
                    } else {
                        Toast.makeText(OrderInfoActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    close();
                }
                close();
            }

            public void onFailure(Throwable arg0) { // 失败，调用
                close();
            }

            public void onFinish() { // 完成后调用，失败，成功，都要掉
                close();
            }
        });
    }

    /**
     * 解决scrollview嵌套listview问题
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
