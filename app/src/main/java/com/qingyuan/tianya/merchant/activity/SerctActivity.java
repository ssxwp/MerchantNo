package com.qingyuan.tianya.merchant.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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
import com.qingyuan.tianya.merchant.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SerctActivity extends BaseActivity{

    private EditText se_ed;
    private TextView se_tv;
    private TextView se_sure;
    private TextView tv_name;
    private TextView tv_nub;
    private String order_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serct);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        se_ed = (EditText)findViewById(R.id.se_ed);
        se_tv = (TextView)findViewById(R.id.se_tv);
        se_sure = (TextView)findViewById(R.id.se_sure);
        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_nub = (TextView)findViewById(R.id.tv_nub);
        //mListView = (ListView)findViewById(R.id.se_list);
    }

    @Override
    public void initData() {

    }

    private void getSe(String se) {
        if (se==null||se.length()==0){
            toast("请输入验证码");
            return;
        }
        initProgressDialog();
        String urlString = ApiConstant.YANZHENGMAORDER;
        RequestParams params = new RequestParams();
        params.put("validate", se);
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        JSONObject customer = jObj.getJSONObject("message");
                        String goods_name = customer.getString("goods_name");
                        order_id = customer.getString("order_id");
                        String nub = customer.getString("nub");
                        if (StringUtil.isNotEmpty(goods_name)){
                            tv_name.setText(goods_name);
                            tv_nub.setText("数量：" + nub);
                            se_sure.setVisibility(View.VISIBLE);

                        }else {
                            toast("商品不存在");
                        }
                    } else {
                        Toast.makeText(SerctActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
        se_tv.setOnClickListener(this);
        se_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.se_tv:
                String se = se_ed.getText().toString().trim();
                getSe(se);
                break;
            case R.id.se_sure:
                getSure();
                break;
        }
    }

    private void getSure() {
        initProgressDialog();
        String urlString = ApiConstant.YANZHENGMA;
        RequestParams params = new RequestParams();
        params.put("order_id", order_id);
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        Toast.makeText(SerctActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SerctActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
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
}
