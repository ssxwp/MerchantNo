package com.qingyuan.tianya.merchant.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.bean.LoginBean;
import com.qingyuan.tianya.merchant.utils.KeepLogMsg;

import org.json.JSONException;
import org.json.JSONObject;

public class PassWordActivity extends BaseActivity{

    //private EditText phone;
    private EditText old_password;
    private EditText password;
    private EditText password1;
    private TextView sure;
    private String m_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_word);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        //phone = (EditText)findViewById(R.id.et_reg_phone);
        old_password = (EditText)findViewById(R.id.et_reg_oldpassword);
        password = (EditText)findViewById(R.id.et_reg_password);
        password1 = (EditText)findViewById(R.id.et_reg_password1);
        sure = (TextView)findViewById(R.id.tv_login);
    }

    @Override
    public void initData() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
    }

    @Override
    public void initClick() {
        sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_login:
                //String account =phone.getText().toString().trim();
                String old = old_password.getText().toString().trim();
                String newword = password.getText().toString().trim();
                String pass = password1.getText().toString().trim();
                changePassword(old,newword,pass);
                break;
        }
    }

    private void changePassword(String old, String newword, String pass) {
        /*if (account == null || account.trim().length() == 0) {
            toast("请输入您的账号！");
            return;
        }*/
        if (old == null || old.trim().length() == 0) {
            toast("请输入您的旧密码！");
            return;
        }
        if (newword == null || newword.trim().length() == 0) {
            toast("请输入您的新密码！");
            return;
        }
        if (pass == null || pass.trim().length() == 0) {
            toast("请确认您的新密码！");
            return;
        }
        if (!newword.equals(pass)){
            toast("两次输入密码不一致");
            return;
        }
        initProgressDialog();
        String urlString = ApiConstant.CHANGEPASSWORD;
        RequestParams params = new RequestParams();
        params.put("shop_id", m_id);
        params.put("y_p", old);
        params.put("x_p",newword);
        HttpUtil.get(urlString, params, new
                AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {
                        // 获取数据成功会调用这里
                        try {
                            JSONObject jObj = new JSONObject(response.trim());
                            if (jObj.getString("flag").equals("success")) {
                                toast(jObj.getString("message"));
                                createSharedPreference(PassWordActivity.this, "user_custId")
                                        .clearShared();
                                Bundle bundle = new Bundle();
                                bundle.putString("from","change");
                                skipActivityForClose(PassWordActivity.this,LoginActivity.class,bundle);
                            } else {
                                toast(jObj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        close();
                    }

                    public void
                    onFailure(Throwable arg0) { // 失败，调用
                        close();
                    }

                    public void onFinish() { // 完成后调用，失败，成功，都要掉 close(); }; });
                    }
                });
    }

}
