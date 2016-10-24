package com.qingyuan.tianya.merchant.bean;

/**
 * Created by gaoyanjun on 2016/8/25.
 */
public class OrderBean {
    private String o_id;
    private String g_id;
    private String goods_name;
    private String nub;
    private String price;
    private String utime;
    private String jtime;
    private String status;

    public OrderBean(String o_id, String g_id,String goods_name, String nub, String price, String utime, String jtime, String status) {
        this.o_id = o_id;
        this.g_id = g_id;
        this.goods_name = goods_name;
        this.nub = nub;
        this.price = price;
        this.utime = utime;
        this.jtime = jtime;
        this.status = status;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getNub() {
        return nub;
    }

    public void setNub(String nub) {
        this.nub = nub;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    public String getJtime() {
        return jtime;
    }

    public void setJtime(String jtime) {
        this.jtime = jtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
