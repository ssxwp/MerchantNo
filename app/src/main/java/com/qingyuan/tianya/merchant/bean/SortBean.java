package com.qingyuan.tianya.merchant.bean;

/**
 * Created by gaoyanjun on 2016/8/24.
 */
public class SortBean {
   private String cate_id;
    private String cate_name;

    public SortBean(String cate_id, String cate_name) {
        this.cate_id = cate_id;
        this.cate_name = cate_name;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }
}
