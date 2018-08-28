package com.lizx.wechatshop.entity;

import java.sql.Date;

public class WechatOrder {


    private String id;
    private String appid;
    private String time_stamp;
    private String nonce_str;
    private String packageVal;
    private String pay_sign;
    private String goods_id;
    private String order_id;
    private Date create_time;
    private Date update_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getPackageVal() {
        return packageVal;
    }

    public void setPackageVal(String packageVal) {
        this.packageVal = packageVal;
    }

    public String getPay_sign() {
        return pay_sign;
    }

    public void setPay_sign(String pay_sign) {
        this.pay_sign = pay_sign;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
