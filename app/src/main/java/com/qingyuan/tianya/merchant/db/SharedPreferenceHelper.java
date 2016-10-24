package com.qingyuan.tianya.merchant.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 数据库
 * Created by gaoyanjun on 2016/1/18.
 */
public class SharedPreferenceHelper {
    private SharedPreferences sp;
    private SharedPreferences.Editor et;
    private Context context;
    private String name;
    @SuppressLint("CommitPrefEdits")
    public SharedPreferenceHelper(Context context, String spName){
        this.context = context;
        this.name = spName;
        if (context!=null){
            sp = context.getSharedPreferences(name,0);
            et = sp.edit();
        }
    }
    /**
     * 存储数据
     */
    public void putValue(String key,String value){
        et.putString(key,value);
        et.commit();
    }
    /**
     * 取出数据
     */
    public String getValue(String key){
        return sp.getString(key,"");
    }

    /**
     * 清楚指定数据
     */
    public void deleteValue(String key){
        et.remove(key);
        et.commit();
    }

    /**
     * 清除sharepreference中的数据
     */
    public void clearShared(){
        et.clear();
        et.commit();
    }
}
