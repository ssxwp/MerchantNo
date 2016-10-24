package com.qingyuan.tianya.merchant.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.view.HeaderView;

import org.json.JSONException;
import org.json.JSONObject;

public class GoodsNameActivity extends BaseActivity{
    private String name;
    private HeaderView head;
    private EditText ed;
    private String goods_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_name);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        Bundle bundle = getIntent().getExtras();
        goods_id = bundle.getString("goods_id");
        name = bundle.getString("goods_name");
        head = (HeaderView) findViewById(R.id.goods_name_head);
        ed = ((EditText) findViewById(R.id.goods_name_ed));
    }

    @Override
    public void initData() {
        ed.setText(name);
    }

    @Override
    public void initClick() {
        head.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toUpdateName();
            }
        });
    }

    private void toUpdateName() {
        String urlString = ApiConstant.CHANGEPRODUCE;
        RequestParams params = new RequestParams();
        if (StringUtil.isNotEmpty(ed.getText().toString().trim())){
            params.put("goodsname",ed.getText().toString().trim());
        }else {
            toast("商品名字不能为空");
            return;
        }
        params.put("goods_id", goods_id);
        initProgressDialog();
        HttpUtil.post(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                Log.i("TAG", "名字成功！" + response);
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        toast(jObj.getString("message"));
                    } else {
                        toast(jObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    close();
                }
                close();
            }

            public void onFailure(Throwable arg0) { // 失败，调用
                close();
                Log.e("hck", " onFailure" + arg0.toString());
            }

            public void onFinish() { // 完成后调用，失败，成功，都要调
                close();
            }


        });
    }
}
