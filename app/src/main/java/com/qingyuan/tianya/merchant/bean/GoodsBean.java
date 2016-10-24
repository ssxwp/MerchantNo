package com.qingyuan.tianya.merchant.bean;

/**
 * Created by gaoyanjun on 2016/8/24.
 */
public class GoodsBean {
    private String goods_id;
    private String goods_name;
    private String s_price;
    private String h_price;
    private String pic;

    public GoodsBean(String goods_id, String goods_name,String s_price, String h_price, String pic) {
        this.goods_id = goods_id;
        this.goods_name = goods_name;
        this.s_price = s_price;
        this.h_price = h_price;
        this.pic = pic;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getS_price() {
        return s_price;
    }

    public void setS_price(String s_price) {
        this.s_price = s_price;
    }

    public String getH_price() {
        return h_price;
    }

    public void setH_price(String h_price) {
        this.h_price = h_price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
