package com.qingyuan.tianya.merchant.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.utils.AMapUtil;
import com.qingyuan.tianya.merchant.utils.Base64;
import com.qingyuan.tianya.merchant.utils.ImageDispose;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.utils.UploadUtil;
import com.qingyuan.tianya.merchant.view.ActionSheet;
import com.qingyuan.tianya.merchant.view.HeaderView;
import com.qingyuan.tianya.merchant.view.TakePhotoPopuWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerchantActivity extends BaseActivity implements ActionSheet.MenuItemClickListener {
    private TakePhotoPopuWindow menuWindow;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 用来标识请求裁剪图片后的activity */
    private static final int CAMERA_CROP_DATA = 3022;
    // 照相机拍照得到的图片
    private String mFileName = System.currentTimeMillis() + ".jpg";
    private File mCurrentPhotoFile;
    private String m_id;
    private RelativeLayout merchant_pic_rel, merchant_name_rel, merchant_address_rel, merchant_phone_rel, merchant_onesort_rel, merchant_on_rel;
    private SimpleDraweeView merchant_pic;
    private TextView merchant_name, merchant_address, merchant_phone, merchant_onesort, merchant_on;
    private String username;
    private String district;
    private String phone;
    private List<String> info = new ArrayList<>();
    private LinearLayout ll;
    private byte[] bytes;
    private TextView start_tv;
    //    private TextView send_tv;
    private TextView rightText;
    private AMapUtil aMap = null;
    private double latitude = 0;
    private double longitude = 0;

//    //选择的商品类型,默认为:购物
//    private String MerchantCategory="购物";
//    private static final int RESULT_CODE=100;

    private Spinner favorable_Spinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        addActivity(this);
        initView();
        initData();
        initClick();
        initLocation();
    }

    @Override
    public void initView() {
        HeaderView headerView = (HeaderView) findViewById(R.id.mer_head);
        rightText = ((TextView) headerView.getRightText());
        merchant_pic_rel = (RelativeLayout) findViewById(R.id.merchant_pic_rel);
        merchant_name_rel = (RelativeLayout) findViewById(R.id.merchant_name_rel);
        merchant_address_rel = (RelativeLayout) findViewById(R.id.merchant_address_rel);
        merchant_phone_rel = (RelativeLayout) findViewById(R.id.merchant_phone_rel);
        merchant_onesort_rel = (RelativeLayout) findViewById(R.id.merchant_onesort_rel);
//        merchant_twosort_rel = (RelativeLayout)findViewById(R.id.merchant_twosort_rel);
        merchant_on_rel = (RelativeLayout) findViewById(R.id.merchant_on_rel);
        merchant_pic = (SimpleDraweeView) findViewById(R.id.merchant_pic);
        merchant_name = (TextView) findViewById(R.id.merchant_name);
        merchant_address = (TextView) findViewById(R.id.merchant_address);
        merchant_phone = (TextView) findViewById(R.id.merchant_phone);
        merchant_onesort = (TextView) findViewById(R.id.merchant_onesort);
//        merchant_twosort = (TextView)findViewById(R.id.merchant_twosort);
        start_tv = (TextView) findViewById(R.id.start_tv);
//        send_tv = (TextView)findViewById(R.id.send_tv);
        merchant_on = (TextView) findViewById(R.id.merchant_on);
        ll = ((LinearLayout) findViewById(R.id.ll));
        mCurrentPhotoFile = new File(Environment.getExternalStorageDirectory(),
                mFileName);

    }

    @Override
    public void initData() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
        if (m_id.equals("44") || m_id.equals("38") || m_id.equals("54")) {
            merchant_onesort_rel.setVisibility(View.GONE);
//            merchant_twosort_rel.setVisibility(View.GONE);
        } else {
            merchant_onesort_rel.setVisibility(View.VISIBLE);
//            merchant_twosort_rel.setVisibility(View.VISIBLE);
        }
        queryMessage();
        info.add("营业中");
        info.add("休息中");

    }

    public void initLocation() {
        aMap = new AMapUtil(MerchantActivity.this);
        aMap.setOption();
        aMap.getData(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        latitude = aMapLocation.getLatitude();//获取纬度
                        longitude = aMapLocation.getLongitude();//获取经度
                        //toast("纬度" + latitude + "经度"+longitude);
                        aMapLocation.getAccuracy();//获取精度信息
                        if (StringUtil.isNotEmpty(String.valueOf(latitude))) {
                            getPoi();
                        }
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        aMap.startLocation();
    }

    @Override
    public void initClick() {
        merchant_pic_rel.setOnClickListener(this);
        merchant_name_rel.setOnClickListener(this);
        merchant_address_rel.setOnClickListener(this);
        merchant_phone_rel.setOnClickListener(this);
        merchant_on_rel.setOnClickListener(this);
        merchant_onesort_rel.setOnClickListener(this);
        rightText.setOnClickListener(this);
    }

    private void getPoi() {
        if (longitude == 0 || latitude == 0) {
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
            return;
        }
        initProgressDialog();
        String urlString = ApiConstant.POI;
        RequestParams params = new RequestParams();
        params.put("shop_id", m_id);
        params.put("longitude", longitude + "");
        params.put("latitude", latitude + "");
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    Log.i("TAG", "商家信息：" + jObj.toString());
                    if (jObj.getString("flag").equals("success")) {
                        toast("定位成功");
                        aMap.stop();
                        close();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.merchant_pic_rel:
                menuWindow = new TakePhotoPopuWindow(this, this);
                // 显示窗口
                menuWindow.showAtLocation(this.findViewById(R.id.re_info),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.iv_take_pic:
                doPickPhotoAction();
                menuWindow.dismiss();
                break;
            case R.id.iv_from_phone:
                pickPhoto();
                menuWindow.dismiss();
                break;
            case R.id.merchant_name_rel:
                Bundle bundle = new Bundle();
                bundle.putString("name", username);
                skipActivity(this, ChangeNameActivity.class, bundle);
                break;
            case R.id.merchant_address_rel:
                Bundle bundle1 = new Bundle();
                bundle1.putString("address", district);
                skipActivity(this, ChangeAddressActivity.class, bundle1);
                break;
            case R.id.merchant_phone_rel:
                Bundle bundle2 = new Bundle();
                bundle2.putString("phone", phone);
                skipActivity(this, PhoneActivity.class, bundle2);
                break;
            case R.id.merchant_on_rel:
                showAcxtionSheet();
                break;
            case R.id.merchant_onesort_rel:
                skipActivity(this, ChangeSortActivity.class, null);
                break;
//            case R.id.merchant_twosort_rel:
//                skipActivity(this,ChangeSortActivity.class,null);
//                break;
            case R.id.titlelog:
                getPoi();
                break;
        }
    }

    /**
     * 营业状态设置
     */
    private void showAcxtionSheet() {
        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle("取消");// before add items
        menuView.addItems(info);
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryMessage();
    }

    private void queryMessage() {
        String urlString = ApiConstant.MERCHANTINFO;
        RequestParams params = new RequestParams();
        params.put("shop_id", m_id);
        HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.trim());
                    Log.i("TAG", "商家信息：" + jObj.toString());
                    if (jObj.getString("flag").equals("success")) {
                        JSONObject object = jObj.getJSONObject("message");
                        username = object.getString("shopname");
                        String status = object.getString("status");
                        String head_pic = object.getString("head_pic");
                        phone = object.getString("phone");
                        String cate_1 = object.getString("name_1");
                        String cate_2 = object.getString("name_2");
                        String qprice = object.getString("q_price");
                        String pPrice = object.getString("p_price");
                        district = object.getString("district");
                        if (StringUtil.isNotEmpty(username)) {
                            merchant_name.setText(username);
                        }
                        if (status.equals("0")) {
                            merchant_on.setText("营业中");
                        } else {
                            merchant_on.setText("休息中");
                        }
                        if (head_pic != null) {
                            Uri uri = Uri.parse(head_pic);
                            merchant_pic.setImageURI(uri);
                        }
                        merchant_address.setText(district);
                        merchant_onesort.setText(cate_1 + "/" + cate_2);
//                        merchant_twosort.setText(cate_2);
//                        MerchantCategory=cate_1;//为商品类型赋值，主界面会用到该字符串
                        merchant_phone.setText(phone);
                        //                       start_tv.setText("起送价：￥"+pPrice);
//                        send_tv.setText("起送价：￥"+qprice);
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

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        // 从相册中去获取
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            toast("没有找到照片");
        }
    }

    /**
     * 描述：从照相机获取
     */
    private void doPickPhotoAction() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhoto();
        } else {
            toast("没有可用的存储卡");
        }
    }

    private void doTakePhoto() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            toast("未找到系统相机程序");
        }
    }

    /**
     * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent mIntent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA:
                Uri uri = mIntent.getData();
                String currentFilePath = getPath(uri);
                if (StringUtil.isNotEmpty(currentFilePath)) {
                    Intent intent1 = new Intent(this, CropImageActivity.class);
                    intent1.putExtra("PATH", currentFilePath);
                    startActivityForResult(intent1, CAMERA_CROP_DATA);
                } else {
                    toast("未在存储卡中找到这个文件");
                }
                break;
            case CAMERA_WITH_DATA:
                String currentFilePath2 = mCurrentPhotoFile.getPath();
                if (StringUtil.isNotEmpty(currentFilePath2)) {
                    Intent intent2 = new Intent(this, CropImageActivity.class);
                    intent2.putExtra("PATH", currentFilePath2);
                    startActivityForResult(intent2, CAMERA_CROP_DATA);
                }

                break;
            case CAMERA_CROP_DATA:
                String path = mIntent.getStringExtra("PATH");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                //bytes = ImageDispose.Bitmap2Bytes(bitmap);
                File file = new File(path);
                toUpdateImage(path);
                merchant_pic.setImageBitmap(bitmap);
                break;
        }
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    public String getPath(Uri uri) {
        if (StringUtil.isEmpty(uri.getAuthority())) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * 存储头像
     */
    private void toUpdateImage(String path) {
        String fileKey = "head_pic";
        UploadUtil uploadUtil = new UploadUtil(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("shop_id", m_id);
        try {
            uploadUtil.uploadFile(new File(path), fileKey, "http://114.215.78.102/index.php/Home/index/edit_shopInfo", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //UploadUtil.uploadFile(file, "http://114.215.78.102/index.php/Home/index/ceshi");
        /*String urlString = ApiConstant.CESHI;
        RequestParams params = new RequestParams();
        try {
            params.put("head_pic", bytes.toString());
            Log.i("ssssss", Base64.encodeFromFile(srcPath));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //params.put("shop_id", m_id);
        HttpUtil.post(urlString, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) { // 获取数据成功会调用这里
                Log.i("TAG", "修改头像成功！" + response);
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
            }

            public void onFailure(Throwable arg0) { // 失败，调用
                close();
                Log.e("hck", " onFailure" + arg0.toString());
            }

            public void onFinish() { // 完成后调用，失败，成功，都要调
                close();
            }


        });*/
    }

    @Override
    public void onItemClick(int itemPosition) {
        merchant_on.setText(info.get(itemPosition));
        String urlString = ApiConstant.CHNAGEINFO;
        RequestParams params = new RequestParams();
        params.put("shop_id", m_id);
        if (info.get(itemPosition).equals("营业中")) {
            params.put("status", 0 + "");
        } else if (info.get(itemPosition).equals("休息中")) {
            params.put("status", 1 + "");
        }
        initProgressDialog();
        HttpUtil.post(urlString, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String jsonString) {
                try {
                    JSONObject jObj = new JSONObject(jsonString);
                    if (jObj.getString("flag").equals("success")) {
                        toast(jObj.getString("message"));
                    } else {
                        toast(jObj.getString("message"));
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
    protected void onDestroy() {
        super.onDestroy();
        if (aMap != null) {
            aMap.stopLocation();
        }
    }


    /* @Override
    protected void onPause() {
        super.onPause();
        //aMap.stop();
        if (aMap.mLocationClient == null) return ;
        if (aMap.mLocationClient.isStarted()){
            aMap.mLocationClient.stopLocation();
        }
    }

    @Override
    public void onDestroy() {
        if (aMap.mLocationClient != null && aMap.mLocationClient.isStarted()) {
            aMap.stop();
            aMap.mLocationClient = null;
        }
        super.onDestroy();
    }*/


//    @Override
//    public void onBackPressed() {
//        Intent intent=new Intent(MerchantActivity.this,HomeActivity.class);
//        intent.putExtra("Category",MerchantCategory);
//        setResult(RESULT_CODE,intent);
//        super.onBackPressed();
//    }
}
