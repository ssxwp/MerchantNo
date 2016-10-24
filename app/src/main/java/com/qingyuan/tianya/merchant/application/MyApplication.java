package com.qingyuan.tianya.merchant.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by gaoyanjun on 2016/8/21.
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        instance = this;
    }
    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * Retrieves application's version number from the manifest
     *
     * @return
     */
    public  String getVersion() {
        String version = "0.0.0";
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
            //version=packageInfo.v
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
    public int getCode(){
        int code =0;
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            code = packageInfo.versionCode;
            //version=packageInfo.v
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }
}
