package com.qingyuan.tianya.merchant.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class GoodsInfoActivity extends BaseActivity {

    private RelativeLayout goodsinfo_pic_rel;
    private RelativeLayout goodsinfo_name_rel;
    private RelativeLayout goodsinfo_price_rel;
    private RelativeLayout goodsinfo_hprice_rel;
    private SimpleDraweeView goods_info_pic;
    private TextView goodsinfo_name;
    private TextView goodsinfo_price;
    private TextView goodsinfo_hprice;
    //private WebView webView;
    private String goodsname;
    //  private TextView change_text;
    private String goods_id;
    //  private ImageView check;
    private boolean checked;
    private String price;
    private String h_price;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        goodsinfo_pic_rel = (RelativeLayout)findViewById(R.id.goodsinfo_pic_rel);
        goodsinfo_name_rel = (RelativeLayout)findViewById(R.id.goodsinfo_name_rel);
        goodsinfo_price_rel = (RelativeLayout)findViewById(R.id.goodsinfo_price_rel);
        goodsinfo_hprice_rel = (RelativeLayout)findViewById(R.id.goodsinfo_hprice_rel);
        goods_info_pic = (SimpleDraweeView)findViewById(R.id.goods_info_pic);
        goodsinfo_name = (TextView)findViewById(R.id.goodsinfo_name);
        goodsinfo_price = (TextView)findViewById(R.id.goodsinfo_price);
        goodsinfo_hprice = (TextView)findViewById(R.id.goodsinfo_hprice);
        //change_text = (TextView)findViewById(R.id.change_text);
       // check = (ImageView)findViewById(R.id.check);
       // webView = (WebView)findViewById(R.id.web);
    }

    @Override
    public void initData() {
        Bundle bundle =getIntent().getExtras();
        goods_id = bundle.getString("goods_id");
        getGoodsMesage();
    }

    private void getGoodsMesage() {
        String urlString = ApiConstant.PRODUCEINFO;
        RequestParams params = new RequestParams();
        params.put("goods_id", goods_id);
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    Log.i("TAG", "商品信息：" + jObj.toString());
                    if (jObj.getString("flag").equals("success")) {
                        JSONObject object = jObj.getJSONObject("message");
                        goodsname = object.getString("goodsname");
                        price = object.getString("price");
                        h_price = object.getString("h_price");
                        String pic1 = object.getString("pic_1");
                        String remark = object.getString("remark");
                        if (StringUtil.isNotEmpty(pic1)){
                            Uri uri = Uri.parse(pic1);
                            goods_info_pic.setImageURI(uri);
                        }else {
                            goods_info_pic.setImageResource(R.mipmap.default_round_head);
                        }
                        goodsinfo_name.setText(goodsname);
                        goodsinfo_price.setText("￥"+price);
                        goodsinfo_hprice.setText("￥"+h_price);
                        //webView.loadDataWithBaseURL(null, remark, "text/html", "utf-8", null);
                    }else {
                        toast(jObj.getString("message"));
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
        goodsinfo_pic_rel.setOnClickListener(this);
        goodsinfo_name_rel.setOnClickListener(this);
        goodsinfo_price_rel.setOnClickListener(this);
        goodsinfo_hprice_rel.setOnClickListener(this);
        //change_text.setOnClickListener(this);
        //check.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goodsinfo_pic_rel:
                Bundle bundle4 = new Bundle();
                bundle4.putString("goods_id",goods_id);
                skipActivity(this,GoodsPicActivity.class,bundle4);
                break;
            case R.id.goodsinfo_name_rel:
                Bundle bundle = new Bundle();
                bundle.putString("goods_id",goods_id);
                bundle.putString("goods_name",goodsname);
                skipActivity(this,GoodsNameActivity.class,bundle);
                break;
            case R.id.goodsinfo_price_rel:
                Bundle bundle1 = new Bundle();
                bundle1.putString("goods_id",goods_id);
                bundle1.putString("goods_price",price);
                skipActivity(this,GoodsPriceActivity.class,bundle1);
                break;
            case R.id.goodsinfo_hprice_rel:
                Bundle bundle2 = new Bundle();
                bundle2.putString("goods_id",goods_id);
                bundle2.putString("goods_price",h_price);
                skipActivity(this,GoodsHpriceActivity.class,bundle2);
                break;
//            case R.id.change_text:
//                Bundle bundle3 = new Bundle();
//                bundle3.putString("goods_id",goods_id);
//                skipActivity(this,GoodsRemarkActivity.class,bundle3);
//                break;
//            case R.id.check:
//                if (checked){
//                    checked = false;
//                    check.setImageResource(R.mipmap.weixuanzhong);
//                }else {
//                    checked = true;
//                    check.setImageResource(R.mipmap.xuanzhong);
//                }
//                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoodsMesage();
    }
}
