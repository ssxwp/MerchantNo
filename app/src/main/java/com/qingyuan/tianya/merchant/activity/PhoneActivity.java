package com.qingyuan.tianya.merchant.activity;

import android.app.AlertDialog;
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

public class PhoneActivity extends BaseActivity{
    private String phone;
    private HeaderView head;
    private EditText ed;
    private String m_id;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        Bundle bundle = getIntent().getExtras();
        phone = bundle.getString("phone");
        head = (HeaderView) findViewById(R.id.phone_head);
        ed = ((EditText) findViewById(R.id.phone_ed));
    }

    @Override
    public void initData() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
        //if (StringUtil.isNotEmpty(phone)) {
            ed.setText(phone);
        //}
    }

    @Override
    public void initClick() {
        head.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toUpdatePhone();
            }
        });
        head.getLeftImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dialog = new AlertDialog.Builder(PhoneActivity.this).create();
                dialog.show();
                dialog.getWindow().setContentView(R.layout.dialog_noof);
                dialog.getWindow().findViewById(R.id.dialog_out_diss).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().findViewById(R.id.dialog_out_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        });
    }

    private void toUpdatePhone() {
        String ph = ed.getText().toString().trim();
        String urlString = ApiConstant.CHNAGEINFO;
        RequestParams params = new RequestParams();
        if (ph == null || ph.length() == 0) {
            toast("请输入您的手机号！");
            return;
        }
        if (ph.length()<7){
            toast("请输入有效的联系电话");
            return;
        }
        if (StringUtil.isNotEmpty(ph)){
            params.put("phone",ph);
        }
        params.put("shop_id", m_id);
        initProgressDialog();
        HttpUtil.post(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                Log.i("TAG", "修改头像成功！" + response);
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        toast(jObj.getString("message"));
                        finish();
                    } else {
                        toast("电话号码未修改");
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
