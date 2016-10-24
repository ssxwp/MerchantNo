package com.qingyuan.tianya.merchant.api;
/**
 * @description Android商家端请求服务器的API
 */
public class ApiConstant {


	public static final String URL = "http://114.215.78.102/index.php/";
	//此接口下都做了验证
	public static final String DATA_URL = "home/index/";
	public static final String RECHARGE_URL="Api/Category/";
	public static final String CENTER_URL="MemberCenter/";
	//public static final String ORDER="Order/";
	//定位
	public static final String POI = DATA_URL+"edit_jw";
	//商家登录
	public static final String LAND = DATA_URL+"land";
	//商家详情
	public static final String MERCHANTINFO = DATA_URL+"shopInfo";
	//修改商家详情
	public static final String CHNAGEINFO = DATA_URL+"edit_shopInfo";
	//商家商品列表
	public static final String PRODUCE =  DATA_URL+"get_goods";
	//商品详情
	public static final String PRODUCEINFO = DATA_URL+"get_goodsInfo";
	//修改商品详情
	public static final String CHANGEPRODUCE = DATA_URL+"edit_goodsinfo";
	//修改密码
	public static final String CHANGEPASSWORD = DATA_URL+"eidt_password";
	//查看验证码订单商品
	public static final String YANZHENGMAORDER =  DATA_URL+"validate";
	//验证码验证
	public static final String YANZHENGMA =  DATA_URL+"to_validate";
	//订单列表
	public static final String ORDER =  DATA_URL+"orderList";
	//订单详情
	public static final String ORDERINFO =  DATA_URL+"orderInfo";
	//大类
	public static final String ONESORT =RECHARGE_URL+"get_cate_1";
	//小类
	public static final String TWOSORT =RECHARGE_URL+"get_cate_2";
	//添加商品
	public static final String ADDGOODS = DATA_URL+"add_goodsInfo";
	//测试图片上传接口
	public static final String CESHI = DATA_URL+"ceshi";
	//public static final String CATE = "land";
	//public static final String CATE = "land";
	//public static final String CATE = "land";
	//public static final String CATE = "land";

}
