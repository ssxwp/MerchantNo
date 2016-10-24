package com.qingyuan.tianya.merchant.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.api.ApiConstant;
import com.qingyuan.tianya.merchant.api.HttpUtil;
import com.qingyuan.tianya.merchant.utils.Base64;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.utils.UploadUtil;
import com.qingyuan.tianya.merchant.view.HeaderView;
import com.qingyuan.tianya.merchant.view.TakePhotoPopuWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoodsPicActivity extends BaseActivity{
    private GridView gridView1;              //网格显示缩略图
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器

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
    private HeaderView head;

    private ArrayList<String> pathList = new ArrayList<>();
    private String goods_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_pic);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        Bundle bundle = getIntent().getExtras();
        goods_id = bundle.getString("goods_id");
        gridView1 = (GridView) findViewById(R.id.gridView1);
        head = (HeaderView)findViewById(R.id.goods_pic_head);
    }

    @Override
    public void initData() {
        mCurrentPhotoFile = new File(Environment.getExternalStorageDirectory(),
                mFileName);
         /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        //获取资源图片加号
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.gridview_addpic);
        imageItem = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.griditem_addpic,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);

        /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if( imageItem.size() == 7) { //第一张为默认图片
                    Toast.makeText(GoodsPicActivity.this, "图片数6张已满", Toast.LENGTH_SHORT).show();
                }
                else if(position == 0) { //点击图片位置为+ 0对应0张图片
                    //Toast.makeText(GoodsPicActivity.this, "添加图片", Toast.LENGTH_SHORT).show();

                    menuWindow = new TakePhotoPopuWindow(GoodsPicActivity.this, GoodsPicActivity.this);
                    // 显示窗口
                    menuWindow.showAtLocation(GoodsPicActivity.this.findViewById(R.id.li),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    //选择图片
                    /*Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);*/
                    //通过onResume()刷新数据
                }
                else {
                    dialog(position);
                    //Toast.makeText(MainActivity.this, "点击第"+(position + 1)+" 号图片",
                    //      Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void initClick() {
        head.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePic();
            }
        });
    }

    private void updatePic() {
        /*String urlString = ApiConstant.CHANGEPRODUCE;
        RequestParams params = new RequestParams();*/
        if (pathList.size() == 0){
            toast("请选择上传图片");
            return;
        }
        initProgressDialog();
        for (int i = 0; i < pathList.size(); i++) {
            //params.put("pic_"+i,pathList.get(i));
            String fileKey = "pic_"+(1+i);
            UploadUtil uploadUtil = new UploadUtil(GoodsPicActivity.this);
            //UploadUtil.getInstance();
            Map<String, String> params = new HashMap<String, String>();
            params.put("goods_id", goods_id);
            try {
                uploadUtil.uploadFile(new File(pathList.get(i)),fileKey,"http://114.215.78.102/index.php/Home/index/edit_goodsinfo",params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*params.put("goods_id", goods_id);
        initProgressDialog();
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
                close();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_take_pic:
                doPickPhotoAction();
                menuWindow.dismiss();
                break;
            case R.id.iv_from_phone:
                pickPhoto();
                menuWindow.dismiss();
                break;
        }
    }

    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA:
                Uri uri = data.getData();
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
                String path = data.getStringExtra("PATH");
                pathList.add(path);
                //toUpdateImage(path);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                HashMap<String, Object> map = new HashMap<>();
                map.put("itemImage", bitmap);
                imageItem.add(map);
                //merchant_pic.setImageBitmap(bitmap);
                break;
        }
        /*//打开图片
        if(resultCode==RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }  //end if 打开图片*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(!TextUtils.isEmpty(pathImage)){
            /*Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<>();
            map.put("itemImage", addbmp);
            imageItem.add(map);*/
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.griditem_addpic,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();

        //}
    }
    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GoodsPicActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
                Log.i("ssssss",position+"");
                pathList.remove(position-1);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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

}
