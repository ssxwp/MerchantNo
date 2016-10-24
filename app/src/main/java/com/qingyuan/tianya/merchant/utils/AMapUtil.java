package com.qingyuan.tianya.merchant.utils;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;


public class AMapUtil extends Application {

	public AMapLocationClient mLocationClient = null;

	public TextView mLocationResult,logMsg;
	public TextView trigger,exit;
	public Vibrator mVibrator;
	/** 定义搜索服务类 */

	public AMapUtil(Context ctx) {
		mLocationClient=new AMapLocationClient(ctx);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mLocationClient = new AMapLocationClient(this.getApplicationContext());     //声明LocationClient类
		super.onCreate();
	}

	public void setOption(){
		//声明mLocationOption对象
		AMapLocationClientOption mLocationOption;
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}

	/**
	 * 停止
	 */
	public void stop()
	{
		mLocationClient.stopLocation();
	}

	/**
	 * 开始
	 */
	public void startLocation() {
		//启动定位
		mLocationClient.startLocation();
	}

	public void stopLocation(){
		mLocationClient.stopLocation();
		mLocationClient.onDestroy();
		mLocationClient=null;
	}

	public String Version() {
		return mLocationClient.getVersion();
	}

	/**
	 * 取消监听事件
	 * @param listener
	 */
	public void unRegisterLocationListener(AMapLocationListener listener){
		mLocationClient.unRegisterLocationListener(listener);
	}

	/**
	 * 获取数据
	 * @param bdLocationLi
	 */
	public void getData(AMapLocationListener bdLocationLi)
	{
		mLocationClient.setLocationListener(bdLocationLi);
	}

	/**
	 * 显示请求字符串
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			if (mLocationResult != null)
				mLocationResult.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
