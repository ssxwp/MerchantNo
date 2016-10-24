package com.qingyuan.tianya.merchant.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.application.MyApplication;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.view.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class HomeActivity extends BaseActivity {


    private TextView login,add_shop,sure_order;
    private ImageView home_add_img;
    private ImageView home_sure_img;
    private RelativeLayout info_rel;
    private RelativeLayout goods_rel;
    private String m_id;
    private TextView out;
    private AlertDialog dialog;
   // private ImageView home_se_img;
    private RelativeLayout se_rel;
    private RelativeLayout qr_rel;
    private long mExitTime;
    private int vcode;
    private String vname;
    private String vdesc;
    private AlertDialog dialog1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addActivity(this);
        getTextUpdate();
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        add_shop = (TextView)findViewById(R.id.add_shop);
        sure_order = (TextView)findViewById(R.id.sure_order);
        login = ((TextView) findViewById(R.id.home_login));
        out = ((TextView) findViewById(R.id.out));
        home_add_img = (ImageView)findViewById(R.id.home_add_img);
        home_sure_img = (ImageView)findViewById(R.id.home_sure_img);
        //home_se_img = (ImageView)findViewById(R.id.home_se_img);
        info_rel = (RelativeLayout)findViewById(R.id.info_rel);
        goods_rel = (RelativeLayout)findViewById(R.id.goods_rel);
        se_rel = (RelativeLayout)findViewById(R.id.se_rel);
        qr_rel = (RelativeLayout)findViewById(R.id.qr_rel);
    }

    @Override
    public void initData() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
    }

    @Override
    public void initClick() {
        login.setOnClickListener(this);
        out.setOnClickListener(this);
        home_add_img.setOnClickListener(this);
        home_sure_img.setOnClickListener(this);
        info_rel.setOnClickListener(this);
        goods_rel.setOnClickListener(this);
        //home_se_img.setOnClickListener(this);
        se_rel.setOnClickListener(this);
        qr_rel.setOnClickListener(this);
        add_shop.setOnClickListener(this);
        sure_order.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.out:
                dialog = new AlertDialog.Builder(this).create();
                dialog.show();
                dialog.getWindow().setContentView(R.layout.dialog_out);
                dialog.getWindow().findViewById(R.id.dialog_out_diss).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().findViewById(R.id.dialog_out_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        loginout();
                    }
                });
                break;
            case R.id.home_login:
//                if (StringUtil.isNotEmpty(m_id)){
//                    toast("已登录");
//                    Bundle bundle = new Bundle();
//                    bundle.putString("from","done");
//                    skipActivity(this, LoginActivity.class, bundle);
                if (StringUtil.isNotEmpty(createSharedPreference(this, "user_custId").getValue("custId"))) {

                    skipActivity(this, PassWordActivity.class, null);
                }else {
                    toast("请先登录账户");
                    Bundle bundle = new Bundle();
                    bundle.putString("from","Pass");
                    skipActivity(this, LoginActivity.class, bundle);
                }
                break;
            case R.id.home_add_img:
            case R.id.add_shop:
                if (StringUtil.isNotEmpty(m_id)){
                    skipActivity(this,AddGoodsActivity.class,null);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from","add");
                    skipActivity(this,LoginActivity.class,bundle);
                }
                break;
            case R.id.home_sure_img:
            case R.id.sure_order:
                if (StringUtil.isNotEmpty(m_id)){
                    skipActivity(this,OrderActivity.class,null);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from","sure");
                    skipActivity(this,LoginActivity.class,bundle);
                }
                break;
            case R.id.info_rel:
                if (StringUtil.isNotEmpty(m_id)){
                    skipActivity(this,MerchantActivity.class,null);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from","info");
                    skipActivity(this,LoginActivity.class,bundle);
                }
                break;
            case R.id.goods_rel:
                if (StringUtil.isNotEmpty(m_id)){
                    skipActivity(this,GoodsActivity.class,null);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("from","goods");
                    skipActivity(this,LoginActivity.class,bundle);
                }
                break;
            case R.id.se_rel:
                skipActivity(this,SerctActivity.class,null);
                break;
            case R.id.qr_rel:
                skipActivity(this,MipcaActivityCapture.class,null);
                break;
        }
    }
    private void loginout() {
        createSharedPreference(HomeActivity.this, "user_custId")
                .clearShared();
        Bundle bundle = new Bundle();
        bundle.putString("from", "login");
        skipActivity(HomeActivity.this,
                LoginActivity.class, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finshAllActivity();
                System.exit(0);
            }
        }
        return true;
    }

    public void getTextUpdate() {
        String urlString = ApiConstant.RECHARGE_URL;
        RequestParams params=new RequestParams();
        params.put("type","2");
        params.put("version",MyApplication.getInstance().getCode()+"");
        HttpUtil.gets("http://114.215.78.102/index.php/Api/index/android_version",params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                Log.i("TAG", "检测版本更新：" + s);
                try {
                    JSONObject jObj = new JSONObject(s);
                    if (jObj.getString("flag").equals("success")) {
                        /*JSONArray jsonArray = jObj.getJSONArray("jsonArray");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobj=jsonArray.getJSONObject(i);
                            vcode=jobj.getInt("vcode");
                            vname=jobj.getString("vname");
                            vdesc=jobj.getString("vdesc");
                            if (vcode>0) {
                                if (vcode> MyApplication.getInstance().getCode()) {
                                    // 返回版本大于当前版本
                                    // 需要升级

                                }
                            }
                        }*/
                    }else {
                        showUpdataDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    close();
                }
            }
            public void onFailure(Throwable arg0) { // 失败，调用
                Log.e("hck", " onFailure" + arg0.toString());
                close();
            };
            public void onFinish() { // 完成后调用，失败，成功，都要掉
                close();
            };
        });
    }
    protected void showUpdataDialog() {
        dialog1 = new AlertDialog.Builder(this).create();
        dialog1.show();
        dialog1.getWindow().setContentView(R.layout.dialog_sure);
        dialog1.getWindow().findViewById(R.id.dialog_out_diss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        dialog1.getWindow().findViewById(R.id.dialog_out_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadApk();
                dialog1.dismiss();
            }
        });
    }
    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(HomeActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = Update.getFileFromServer("http://oiuh123.oss-cn-shanghai.aliyuncs.com/tuyou.apk", pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); // 结束掉进度条对话框
                } catch (Exception e) {
                    // Message msg = new Message();
                    // msg.what = DOWN_ERROR;
                    // handler.sendMessage(msg);
                    // e.printStackTrace();
                }
            }
        }.start();
    }

    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        HomeActivity.this.startActivity(intent);
    }
}
