package com.qingyuan.tianya.merchant.api;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qingyuan.tianya.merchant.utils.MD5Util;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class HttpUtil {
	private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象
	static {
		client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			 try {
				SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
				client.setSSLSocketFactory(sf);
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void get1(String urlString, AsyncHttpResponseHandler res) // 用一个完整url获取一个string对象
	{
		client.get(urlString, res);
	}
	
	public static void getMsg(String urlString, RequestParams params, AsyncHttpResponseHandler res) // url里面带参数
	{
		client.get(urlString, params, res);
	}


	public static void get(String urlString, AsyncHttpResponseHandler res) // 用一个完整url获取一个string对象
	{
		client.get(ApiConstant.URL + urlString, res);
	}

	public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res) // url里面带参数
	{
		client.get(ApiConstant.URL + urlString, params, res);
	}
	public static void gets(String urlString, RequestParams params, AsyncHttpResponseHandler res) // url里面带参数
	{
		client.get(urlString, params, res);
	}

	public static void get(String urlString, JsonHttpResponseHandler res) // 不带参数，获取json对象或者数组
	{
		client.get(ApiConstant.URL + urlString, res);
	}

	public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
	{
		String timestamp= System.currentTimeMillis()+"";
		params.put("timestamp",timestamp);
		params.put("sign", MD5Util.string2MD5(MD5Util.string2MD5(timestamp + "fa@##%&&901S;\\[^&lsdfa")));
		client.get(ApiConstant.URL + urlString, params, res);
	}

	public static void get(String uString, BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
	{
		
		client.get(ApiConstant.URL + uString, bHandler);
	}

	
	public static void post(String urlString, RequestParams params, AsyncHttpResponseHandler res) { // 带参数，获取json对象或者数组
		String timestamp= System.currentTimeMillis()+"";
		params.put("timestamp",timestamp);
		params.put("sign",MD5Util.string2MD5(MD5Util.string2MD5(timestamp+"fa@##%&&901S;\\[^&lsdfa")));
		client.post(ApiConstant.URL + urlString, params, res);
	}
	public static void postt(String urlString, RequestParams params, AsyncHttpResponseHandler res) { // 带参数，获取json对象或者数组

		String timestamp= System.currentTimeMillis()+"";
		params.put("timestamp",timestamp);
		params.put("sign",MD5Util.string2MD5(MD5Util.string2MD5(timestamp+"fa@##%&&901S;\\[^&lsdfa")));
		client.post(urlString, params, res);
	}
	public static void post(String urlString, RequestParams params, JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
	{

		String timestamp= System.currentTimeMillis()+"";
		params.put("timestamp",timestamp);
		params.put("sign",MD5Util.string2MD5(MD5Util.string2MD5(timestamp+"fa@##%&&901S;\\[^&lsdfa")));
		client.post(ApiConstant.URL + urlString, params, res);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}
}