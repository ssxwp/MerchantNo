package com.qingyuan.tianya.merchant.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.db.SharedPreferenceHelper;


/**
 * fragment基类
 * Created by Administrator on 2016/1/20.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    private Dialog loadingDialog;
    private Dialog loadbar;
    private ConnectivityManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public abstract void initView();

    public abstract void initonClick();

    public abstract void initData();
    public void initProgressDialog() {
        loadingDialog = createLoadingDialog(getActivity(), "正在加载...");
        loadingDialog.show();
       /* if (loadbar == null) {
            loadbar = new Dialog(getActivity(), R.style.load_dialog);
            LayoutInflater mInflater = getActivity().getLayoutInflater();
            View v = mInflater.inflate(R.layout.anim_dialog, null);
            loadbar.setContentView(v);
            loadbar.setCancelable(false);
            loadbar.show();
        } else {
            loadbar.show();
        }*/
    }
    public void close() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
    /**
     * 不需要关闭当前activity
     *
     */
    public void skipActivityforClass(Activity activity,
                                     Class<? extends Activity> target, Bundle bundle) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, target);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        //activity.overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
    }

    /**
     * 需要关闭当前activity
     */
    public void skipActivityforClassClose(Activity activity,
                                          Class<? extends Activity> target, Bundle bundle) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, target);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        activity.finish();
        /*activity.overridePendingTransition(R.anim.activity_in,
                R.anim.activity_out);*/
    }

    /**
     * 获取到顾客Id
     *
     * @param activity
     * @return
     */
    public static String getCustId(Activity activity) {
        String custId = createSharedPreference(activity, "user_custId").getValue("custId");
        return custId;
    }

    /**
     * 想要建立的数据库
     */
    public static SharedPreferenceHelper createSharedPreference(Activity activity, String name) {
        SharedPreferenceHelper sp = new SharedPreferenceHelper(activity, name);
        return sp;
    }


    /**
     * 检测网络是否连接
     * @return
     */
    protected boolean checkNetworkState(Activity activity) {
        boolean flag = false;
        // 得到网络连接信息
        manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork(activity);
        } else {
            // isNetworkAvailable();
        }

        return flag;
    }


    /**
     * 网络未连接时，调用设置方法
     */
    private void setNetwork(Activity activity){
        Toast.makeText(activity, "网路不可用", Toast.LENGTH_SHORT).show();
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
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

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }
}
