package com.qingyuan.tianya.merchant.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.adapter.AbstractSpinerAdapter;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.bean.SortBean;
import com.qingyuan.tianya.merchant.bean.SortTwoBean;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.view.HeaderView;
import com.qingyuan.tianya.merchant.view.SpinnerPopuWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChangeSortActivity extends BaseActivity implements AbstractSpinerAdapter.IOnItemSelectListener {

    private HeaderView head;
    private TextView sp_one;
    private TextView sp_two;
    private List<SortBean> mList = new ArrayList<>();
    private List<SortTwoBean> mList1 = new ArrayList<>();
    private List<String> mListOne = new ArrayList<>();
    private List<String> mListTwo = new ArrayList<>();
    private String cate_id1;
    private String one_id;
    private String m_id;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sort);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        head = ((HeaderView) findViewById(R.id.sort_head));
        sp_one = (TextView)findViewById(R.id.sort_one);
        sp_two = (TextView)findViewById(R.id.sort_two);
    }

    @Override
    public void initData() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
        queOne();
        mSpinnerPopuWindow = new SpinnerPopuWindow(this);
        mSpinnerPopuWindow1 = new SpinnerPopuWindow(ChangeSortActivity.this);
        mSpinnerPopuWindow.refreshData(mListOne,0);
        mSpinnerPopuWindow.setItemListener(this);
    }

    /**
     * 一级分类
     */
    private void queOne() {
        String urlString = ApiConstant.ONESORT;
        RequestParams params = new RequestParams();
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    Log.i("TAG", "分类信息：" + jObj.toString());
                    if (jObj.getString("flag").equals("success")) {
                        JSONArray array = jObj.getJSONArray("responseList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String cate_id = object.getString("cate_id");
                            String cate_name = object.getString("cate_name");
                            SortBean bean = new SortBean(cate_id, cate_name);
                            mListOne.add(cate_name);
                            mList.add(bean);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    close();
                }
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
        sp_one.setOnClickListener(this);
        sp_two.setOnClickListener(this);
        head.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSort();
            }
        });
        head.getLeftImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new AlertDialog.Builder(ChangeSortActivity.this).create();
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

    private void changeSort() {
        String urlString = ApiConstant.CHNAGEINFO;
        RequestParams params = new RequestParams();
        if (StringUtil.isEmpty(cate_id1.trim())){
            toast("正在加载...");
            return;
        }
        if (StringUtil.isEmpty(one_id.trim())) {
            toast("正在加载...");
            return;
        }
        params.put("cate_1", one_id);
        params.put("cate_2",cate_id1);
        params.put("shop_id", m_id);
        initProgressDialog();
        HttpUtil.post(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                Log.i("TAG", "修改分类成功！" + response);
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    if (jObj.getString("flag").equals("success")) {
                        toast(jObj.getString("message"));
                        finish();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sort_one:
                showPopuWindow(sp_one);
                break;
            case R.id.sort_two:
                if (mListTwo.size()==0){
                    toast("请先选择一级分类");
                }else {
                    showPopuWindow1(sp_two);
                }
                break;
        }
    }
    private SpinnerPopuWindow mSpinnerPopuWindow;
    private void showPopuWindow(View view) {
        mSpinnerPopuWindow.setWidth(view.getWidth());
        mSpinnerPopuWindow.showAsDropDown(view);
    }
    private SpinnerPopuWindow mSpinnerPopuWindow1;
    private void showPopuWindow1(View view) {
        mSpinnerPopuWindow1.setWidth(view.getWidth());
        mSpinnerPopuWindow1.showAsDropDown(view);
    }

    @Override
    public void onItemClick(int pos) {
        sp_one.setText(mList.get(pos).getCate_name());
        //sp_two.setText("全部");
        one_id = mList.get(pos).getCate_id();
        quTwo(one_id);
    }

    private void quTwo(String one_id) {
        mList1.clear();
        mListTwo.clear();
        String urlString = ApiConstant.TWOSORT;
        RequestParams params = new RequestParams();
        params.put("cate_id",one_id);
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    Log.i("TAG", "分类信息：" + jObj.toString());
                    if (jObj.getString("flag").equals("success")) {
                        JSONArray array = jObj.getJSONArray("responseList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String cate_id = object.getString("cate_id");
                            String cate_name = object.getString("cate_name");
                            SortTwoBean bean = new SortTwoBean(cate_id,cate_name);
                            mListTwo.add(cate_name);
                            mList1.add(bean);
                        }
                        mSpinnerPopuWindow1.refreshData(mListTwo,0);
                        mSpinnerPopuWindow1.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {

                            @Override
                            public void onItemClick(int pos) {
                                sp_two.setText(mList1.get(pos).getCate_name());
                                cate_id1 = mList1.get(pos).getCate_id();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    close();
                }
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
}
