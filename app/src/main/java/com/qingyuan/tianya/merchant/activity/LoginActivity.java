package com.qingyuan.tianya.merchant.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.bean.LoginBean;
import com.qingyuan.tianya.merchant.db.SharedPreferenceHelper;
import com.qingyuan.tianya.merchant.push.ExampleUtil;
import com.qingyuan.tianya.merchant.utils.KeepLogMsg;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.view.HeaderView;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
    private EditText et_reg_phone,et_reg_password;
    private SharedPreferenceHelper userMsg;
    private LoginBean loginBean;
    private String from;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        HeaderView header = (HeaderView) findViewById(R.id.login_head);
        header.getHeadBackGround().setBackgroundColor(Color.rgb(38, 42, 59));
        et_reg_phone = ((EditText) findViewById(R.id.et_reg_phone));
        et_reg_password = ((EditText) findViewById(R.id.et_reg_password));
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        from = bundle.getString("from");
        if (from!=null&&from.equals("done")){
            et_reg_phone.setFocusable(false);
            et_reg_password.setFocusable(false);
            et_reg_phone.setFocusableInTouchMode(false);
            et_reg_password.setFocusableInTouchMode(false);
            findViewById(R.id.done).setVisibility(View.VISIBLE);
        }
        userMsg = new SharedPreferenceHelper(LoginActivity.this, "user_custId");
    }

    @Override
    public void initClick() {
        findViewById(R.id.login_log).setOnClickListener(this);
     //   findViewById(R.id.change_password).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_log:
                String et_phone = et_reg_phone.getText().toString().trim();
                String et_password = et_reg_password.getText().toString().trim();
                getLoginMessage(et_phone, et_password);
                break;
//            case R.id.change_password:
//                if (StringUtil.isNotEmpty(createSharedPreference(this, "user_custId").getValue("custId"))) {
//                    skipActivity(this, PassWordActivity.class, null);
//                }else {
//                    toast("请先登录");
//                }
//                break;
        }
    }
    private void getLoginMessage(final String phone, String password) {
        if (phone == null || phone.trim().length() == 0) {
            toast("请输入您的账号！");
            return;
        }
        if (password == null || password.trim().length() == 0) {
            toast("请输入您的密码！");
            return;
        }
        initProgressDialog();
        String urlString = ApiConstant.LAND;
        RequestParams params = new RequestParams();
        params.put("account", phone);
        params.put("password", password);
        HttpUtil.get(urlString, params, new
                AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {
                        // 获取数据成功会调用这里
                        try {
                            JSONObject jObj = new JSONObject(response.trim());
                            if (jObj.getString("flag").equals("success")) {
                                String custId = jObj.getString("message");
                                userMsg.putValue("custId", custId);
                                loginBean = new LoginBean();
                                loginBean.setM_id(custId);
                                KeepLogMsg.getKeepLogMsg(loginBean, userMsg);
                                toast("登录成功");
                                switch (from) {
                                    case "login":
                                        skipActivityForClose(LoginActivity.this, HomeActivity.class, null);
                                        break;
                                    case "add":
                                        skipActivityForClose(LoginActivity.this, AddGoodsActivity.class, null);
                                        break;
                                    case "sure":
                                        skipActivityForClose(LoginActivity.this, OrderActivity.class, null);
                                        break;
                                    case "info":
                                        skipActivityForClose(LoginActivity.this, MerchantActivity.class, null);
                                        break;
                                    case "goods":
                                        skipActivityForClose(LoginActivity.this,GoodsActivity.class,null);
                                        break;
                                    case "change":
                                        skipActivityForClose(LoginActivity.this,HomeActivity.class,null);
                                        break;
                                    case  "Pass":
                                        skipActivityForClose(LoginActivity.this,PassWordActivity.class,null);

                                }
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
