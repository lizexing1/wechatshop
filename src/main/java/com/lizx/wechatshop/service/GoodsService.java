package com.lizx.wechatshop.service;

import net.sf.json.JSONObject;

import java.util.Map;

public interface GoodsService {

    void changeMenu();

    JSONObject wechatUnifiedorder(String goodsId, int goodsCount, String userId,String openid);

    String wechatNotifyUrl(Map<String,String> map);
}
