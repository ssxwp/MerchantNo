package com.qingyuan.tianya.merchant.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.adapter.GoodsAdapter;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.bean.GoodsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoodsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private String m_id;
    private ArrayList<GoodsBean> mList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        mListView = (ListView)findViewById(R.id.goods_listview);
    }

    @Override
    public void initData() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
        queGoods();
    }

    private void queGoods() {
        initProgressDialog();
        String urlString = ApiConstant.PRODUCE;
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
                            String goods_id = object.getString("goods_id");
                            String goods_name = object.getString("goodsname");
                            String price = object.getString("price");
                            String h_price = object.getString("h_price");
                            String pic = object.getString("pic");
                            GoodsBean bean = new GoodsBean(goods_id,goods_name,price,h_price,pic);
                            mList.add(bean);
                        }
                        GoodsAdapter adapter = new GoodsAdapter(GoodsActivity.this,mList);
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
    public void initClick() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String goods_id = mList.get(position).getGoods_id();
        Bundle bundle = new Bundle();
        bundle.putString("goods_id",goods_id);
        skipActivity(this,GoodsInfoActivity.class,bundle);
    }
}
