package com.qingyuan.tianya.merchant.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.db.SharedPreferenceHelper;
import com.qingyuan.tianya.merchant.utils.StringUtil;

import java.util.LinkedList;

/**
 * 基类Activity
 * Created by gaoyanjun on 2016/1/18.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener{
    private static LinkedList<Activity> activityQueue = null;
    private ProgressDialog dialog;
    private Dialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 加载数据
     */
    public abstract void initData();

    /**
     * 点击事件监听
     */
    public abstract void initClick();

    public void onClick(View view){

    }

    /**
     * 吐丝
     */
    protected void toast(CharSequence msg){
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 将Activity加入到活动队列
     */
    public static void addActivity(Activity act){
        if (activityQueue!=null){
            activityQueue.add(act);
        }else {
            activityQueue = new LinkedList<>();
            activityQueue.add(act);
        }
    }

    /**
     * 关闭所有已经打开的Activity
     */
    public static void finshAllActivity(){
        if (null!=activityQueue){
            if (activityQueue.size()!=0){
                for (Activity act:activityQueue){
                    if (act!=null){
                        act.finish();
                    }
                }
            }
        }
    }

    /**
     * 加载显示dialog
     */
    public void initProgressDialog(){
       /* dialog = new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.show();*/
        loadingDialog = createLoadingDialog(this, "正在加载...");
        loadingDialog.show();
    }
    /**
     * 获取到顾客Id
     * @param activity
     * @return
     */
    public static String getCustId(Activity activity) {
        String custId = createSharedPreference(activity, "user_custId").getValue("m_id");
        return custId;
    }
    /**
     * 关闭dialog
     */
    public void close(){
        if (loadingDialog!=null){
            if (loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
        }
    }

    /**
     * 不需要关闭当前Activity跳转
     * @param activity 当前Activity
     * @param target 跳转目标
     * @param bundle 传递的数据
     */
    public void skipActivity(Activity activity,Class<? extends Activity> target,Bundle bundle){
        if (activity == null){
            return;
        }
        Intent intent = new Intent(activity,target);
        if (bundle!=null){
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        //activity的退出和进入动画
        //activity.overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
    }

    /**
     * 需要关闭当前Activity跳转其他Activity
     */
    public void skipActivityForClose(Activity activity,Class<? extends Activity> target,Bundle bundle){
        if (activity == null){
            return;
        }
        Intent intent = new Intent(activity,target);
        if (bundle!=null){
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        finishActivity(activity);

    }

    /**
     * 关闭当前Activity
     * @param activity 当前Activity
     */
    public void finishActivity(Activity activity) {
        activity.finish();
        //activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    /**
     * 获取用户ID
     * @param activity 当前Activity
     */
    public static String getUserId(Activity activity){
        return createSharedPreference(activity,"user_Id").getValue("userId");
    }

    /**
     * 建立数据库
     */
    public static SharedPreferenceHelper createSharedPreference(Activity activity, String name) {
        SharedPreferenceHelper sp = null;
        if (activity!=null&& StringUtil.isNotEmpty(name)){
            sp = new SharedPreferenceHelper(activity,name);
        }
        return sp;
    }

    /**
     * 检测网络是否连接
     */
    protected boolean checkNetworkState(){
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        //判断网络是否连接
        if (manager.getActiveNetworkInfo()!=null){
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (flag){
            setNetWork();
        }
        return flag;
    }

    /**
     * 网络未连接时提醒设置
     */
    private void setNetWork() {
        Toast.makeText(BaseActivity.this, "网络未连接", Toast.LENGTH_SHORT).show();
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_show);
        ((TextView) window.findViewById(R.id.dialog_text_content)).setText("网络不可用");
        window.findViewById(R.id.dialog_text_nati).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView postiv = (TextView) window.findViewById(R.id.dialog_text_posti);
        postiv.setText("设置");
        postiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
            }
        });
    }
    /**
     * 得到自定义的progressDialog
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.dialog_loading);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.load_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }
}
