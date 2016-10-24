package com.qingyuan.tianya.merchant.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.activity.OrderInfoActivity;
import com.qingyuan.tianya.merchant.adapter.GoodsAdapter;
import com.qingyuan.tianya.merchant.adapter.OrderAdapter;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.bean.GoodsBean;
import com.qingyuan.tianya.merchant.bean.OrderBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    private View view;
    private ListView mListView;
    private ArrayList<OrderBean> mList;
    private String m_id;
    private boolean flag = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);
        initView();
        initData();
        initonClick();
        return view;
    }

    @Override
    public void initView() {
        mListView = (ListView)view.findViewById(R.id.orderlistview);
    }

    @Override
    public void initonClick() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        m_id = createSharedPreference(getActivity(), "user_custId").getValue("custId");
        getOrder(0);
    }

    private void getOrder(int i) {
        if (i==0){
            initProgressDialog();
        }
        mList = new ArrayList<>();
        String urlString = ApiConstant.ORDER;
        RequestParams params = new RequestParams();
        params.put("shop_id",m_id);
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    Log.i("TAG", "商品信息：" + jObj.toString());
                    if (jObj.getString("flag").equals("success")) {
                        JSONArray array = jObj.getJSONArray("message");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String o_id = object.getString("o_id");
                            String g_id = object.getString("g_id");
                            String goods_name = object.getString("goods_name");
                            String price = object.getString("price");
                            String nub = object.getString("nub");
                            String utime = object.getString("utime");
                            String jtime = object.getString("jtime");
                            String status = object.getString("status");
                            OrderBean bean = new OrderBean(o_id,g_id, goods_name,  nub,price, utime,jtime,status);
                            mList.add(bean);
                        }
                        OrderAdapter adapter = new OrderAdapter(getActivity(), mList);
                        mListView.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    close();
                }
                close();
            }

            public void onFailure(Throwable arg0) { // 失败，调用
                Log.e("hck", " onFailure" + arg0.toString());
                close();
            }

            public void onFinish() { // 完成后调用，失败，成功，都要掉
                close();
            }

        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("o_id",mList.get(position).getO_id());
        bundle.putString("g_id",mList.get(position).getStatus());
        skipActivityforClass(getActivity(), OrderInfoActivity.class,bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
