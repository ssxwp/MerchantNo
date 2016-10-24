package com.qingyuan.tianya.merchant.bean;

/**
 * Created by gaoyanjun on 2016/8/7.
 */
public class OrderGoodsBean {
    private String nub;//数量
    private String name;//名字
    private String price;//价格
    private String hprice;
    private String pic;

    public OrderGoodsBean(String nub, String name, String price, String hprice, String pic) {
        this.nub = nub;
        this.name = name;
        this.price = price;
        this.hprice = hprice;
        this.pic = pic;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getHprice() {
        return hprice;
    }

    public void setHprice(String hprice) {
        this.hprice = hprice;
    }

    public String getNub() {
        return nub;
    }

    public void setNub(String nub) {
        this.nub = nub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
