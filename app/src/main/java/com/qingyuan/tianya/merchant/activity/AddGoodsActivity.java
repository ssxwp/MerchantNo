package com.qingyuan.tianya.merchant.activity;

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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.utils.StringUtil;
import com.qingyuan.tianya.merchant.utils.UploadUtil;
import com.qingyuan.tianya.merchant.view.HeaderView;
import com.qingyuan.tianya.merchant.view.TakePhotoPopuWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddGoodsActivity extends BaseActivity {
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
    private EditText ed_name, ed_price, ed_hprice, ed_boxs;
    private String m_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        m_id = createSharedPreference(this, "user_custId").getValue("custId");
        gridView1 = (GridView) findViewById(R.id.gridView2);
        head = (HeaderView) findViewById(R.id.goods_add_head);
        ed_name = (EditText) findViewById(R.id.add_ed);
        ed_price = (EditText) findViewById(R.id.goods_add_ed);
        ed_hprice = (EditText) findViewById(R.id.add_hprice_ed);
        ed_boxs = (EditText)findViewById(R.id.goods_add_box);

//        ed_remark = (EditText) findViewById(R.id.add_remark_ed);
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
        simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.griditem_addpic, new String[]{"itemImage"}, new int[]{R.id.imageView1});
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
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
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
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                if (imageItem.size() == 2) { //第一张为默认图片
//                    Toast.makeText(AddGoodsActivity.this, "更多图片需要到商品详情处修改", Toast.LENGTH_SHORT).show();
//                } else

                if (position == 0) { //点击图片位置为+ 0对应0张图片
                    menuWindow = new TakePhotoPopuWindow(AddGoodsActivity.this, AddGoodsActivity.this);
                    // 显示窗口
                    menuWindow.showAtLocation(AddGoodsActivity.this.findViewById(R.id.li), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else {
                    dialog(position);
                }
            }
        });
    }

    @Override
    public void initClick() {
        head.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoods();
            }
        });
    }

    private void addGoods() {
        String name = ed_name.getText().toString().trim();
        String price = ed_price.getText().toString().trim();
        String hprice = ed_hprice.getText().toString().trim();
        String box = ed_boxs.getText().toString().trim();
//        String remark = ed_remark.getText().toString().trim();
        if (name == null || name.length() == 0) {
            toast("名字不能为空");
            return;
        }
        if (price == null || price.length() == 0) {
            toast("原价不能为空");
            return;
        }
//        if (hprice == null || hprice.length() == 0) {
//            toast("会员价不能为空");
//            return;
        //}
        if (pathList == null || pathList.size() == 0) {
            toast("图片不能为空");
            return;
        }
        String fileKey = "pic_1";
        //UploadUtil uploadUtil = UploadUtil.getInstance();
        UploadUtil uploadUtil = new UploadUtil(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("goodsname", name);
        params.put("price", price);
        params.put("h_price", hprice);
        params.put("c_box",box);
//        params.put("remark", remark);
        params.put("shop_id", m_id);
        try {
            uploadUtil.uploadFile(new File(pathList.get(0)), fileKey, "http://114.215.78.102/index.php/Home/index/add_goodsInfo", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_take_pic:
                doPickPhotoAction();
                menuWindow.dismiss();
                break;
            case R.id.iv_from_phone:
                menuWindow.dismiss();
                pickPhoto();
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
//                Uri uri = data.getData();
//                String currentFilePath = getPath(uri);
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String selectedPath = cursor.getString(columnIndex);
                cursor.close();

                if (StringUtil.isNotEmpty(selectedPath)) {
                    Intent intent1 = new Intent(this, CropImageActivity.class);
                    intent1.putExtra("PATH", selectedPath);
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
                pathList.clear();
                imageItem.clear();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.griditem_addpic,
                new String[]{"itemImage"}, new int[]{R.id.imageView1});
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AddGoodsActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
                pathList.remove(position - 1);
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
//        try {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//            intent.setType("image/*");
//            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//        } catch (ActivityNotFoundException e) {
//            toast("没有找到照片");
//        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
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