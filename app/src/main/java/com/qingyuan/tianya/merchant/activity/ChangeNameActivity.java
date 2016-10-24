package com.qingyuan.tianya.merchant.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.utils.Base64;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.view.HeaderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ChangeNameActivity extends BaseActivity {

    private String name;
    private HeaderView head;
    private EditText ed;
    private String m_id;
    private AlertDialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        head = (HeaderView) findViewById(R.id.merchant_head);
        ed = ((EditText) findViewById(R.id.merchant_ed));

    }

    @Override
    public void initData() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
        ed.setText(name);
    }

    @Override
    public void initClick() {
        head.getRightText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toUpdateName();
            }
        });
        head.getLeftImageView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

               dialog = new AlertDialog.Builder(ChangeNameActivity.this).create();
                dialog.show();
                dialog.getWindow().setContentView(R.layout.dialog_noof);
                dialog.getWindow().findViewById(R.id.dialog_out_diss).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().findViewById(R.id.dialog_out_ok).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        finish();
                    }
                });


            }
        });
    }
    /**
     *
     * 存储名字
     */
    private void toUpdateName() {
        String urlString = ApiConstant.CHNAGEINFO;
        RequestParams params = new RequestParams();
        if (StringUtil.isNotEmpty(ed.getText().toString().trim())){
            params.put("shopname",ed.getText().toString().trim());
        }else {
            toast("店铺名字不能为空");
            return;
        }
        params.put("shop_id", m_id);
        initProgressDialog();
        HttpUtil.post(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                Log.i("TAG", "修改成功！" + response);
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        toast(jObj.getString("message"));
                        finish();
                    } else {

                        toast("不能输入相同店铺名");
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
