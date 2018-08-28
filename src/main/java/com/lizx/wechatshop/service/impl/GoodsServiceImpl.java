package com.lizx.wechatshop.service.impl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lizx.wechatshop.entity.Goods;
import com.lizx.wechatshop.entity.GoodsOrder;
import com.lizx.wechatshop.entity.User;
import com.lizx.wechatshop.entity.WechatOrder;
import com.lizx.wechatshop.mapper.GoodsMapper;
import com.lizx.wechatshop.mapper.WchatOrderMapper;
import com.lizx.wechatshop.service.GoodsService;
import com.lizx.wechatshop.util.CommonUtil;
import com.lizx.wechatshop.util.HttpClientUtils;
import com.lizx.wechatshop.util.WechatConfig;
import com.lizx.wechatshop.util.XMLUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private WchatOrderMapper wchatOrderMapper ;


    @Override
    public void changeMenu() {

        /**
         * 获取access_token
         */
        String url = WechatConfig.tokenUrl.replace("APPID", WechatConfig.appid).replace("APPSECRET", WechatConfig.secret);
        String result = HttpClientUtils.httpGet(url);
        System.out.println(result);
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
        String access_token = jsonObject.get("access_token").getAsString();
        if(access_token==null){
            //log.error("");
            return;
        }
        /**
         * 改变菜单按钮
         */
        String changeMenuUrl = WechatConfig.changeMenuUrl.replace("ACCESS_TOKEN",access_token);



        /**
         * 修改灵活数据
         */
        List<Map<String, Object>> parentList = goodsMapper.getByParent("0");
        System.out.println();

        JSONArray array = new JSONArray();

        for (int i = 0; i < parentList.size(); i++) {

            Map<String, Object> map = parentList.get(i);
            String pid = (String) map.get("id");

            List<Map<String, Object>> childList = goodsMapper.getByParent(pid);
            if(childList.size()<=0){

                String type = (String) map.get("type");

                JSONObject json = new JSONObject();
                json.put("type",map.get("type"));
                json.put("name",map.get("name"));

                if(type.equals("view")){
                    json.put("url",map.get("url"));
                }

                if(type.equals("click")){
                    json.put("key",map.get("key"));
                }
                array.add(json);
            }else{

                JSONArray childArray = new JSONArray();
                for (int j = 0; j < childList.size(); j++) {
                    //获取每个子节点对象
                    Map<String, Object> childMap = childList.get(j);
                    String type = (String) childMap.get("type");

                    JSONObject json = new JSONObject();
                    json.put("type",childMap.get("type"));
                    json.put("name",childMap.get("name"));

                    //view事件的拼接
                    if(type.equals("view")){
                        json.put("url",childMap.get("url"));
                    }
                    if(type.equals("click")){
                        json.put("key",childMap.get("key"));
                    }

                                    if(type.equals("scancode_push")){
                                        json.put("key",childMap.get("key"));
                                        json.put("sub_button","[]");
                                    }
                    childArray.add(json);
                }
                JSONObject json = new JSONObject();
                json.put("name",map.get("name"));
                json.put("sub_button",childArray);
                array.add(json);
            }
        }

        JSONObject json = new JSONObject();
        json.put("button",array);

        System.out.println(json.toString());

        String changeMenuResult = HttpClientUtils.httpPost(changeMenuUrl, json.toString());
        System.out.println(changeMenuResult);
    }

    /**
     * 微信公众号支付
     * @param goodsId
     * @param goodsCount
     * @param userId
     * @param openid
     * @return
     */
    @Override
    public JSONObject wechatUnifiedorder(String goodsId, int goodsCount, String userId,String openid) {

        //返回controller层用的
        JSONObject json = new JSONObject();

        //1、根据商品id查询出商品信息
        Goods goods = null;//goodsService.getById(goodsId);
        //2、根据商品数量乘以商品单价，得到要下订单的价格
        goods.setPrice(goods.getPrice()*goodsCount);
        //3、获取用户等级计算最后总价
        User user = null;//userService.getById(userId);

        /**
         *3.1、调用微信下单前，先根据用户id和goodsId从数据库中查询下订单时间
         *          *     把数据库中查出来的订单信息和现在的时间做比较，如果超过15分钟，
         *          *     并且订单状态是支付状态，继续调用微信下订单操作
         *
         *     如果没有超过十五分钟，并且订单状态是未支付状态，
         *     把订单表中中的prepareId、签名等信息直接返回到前台即可
         */
        GoodsOrder goodsOrder = wchatOrderMapper.getByUserIdAndGoodsIdAndStaus(userId,goodsId,0);

        if(goodsOrder!=null) {
            //根据订单表中的创建订单的时间和现在时间比较，判断订单时间是否超过了十五分钟
            /**************************判断订单时间是否超时开始******************************************/
            Date create_time = goodsOrder.getCreate_time();
            long currentTimeMillis = System.currentTimeMillis();
            //flag等于true时候是超过了十五分钟，等于false没超过十五分钟
            boolean flag = (currentTimeMillis - create_time.getTime()) > 900000 ? true : false;
            //倒计时的时间戳
            long timeOut = currentTimeMillis - create_time.getTime();

            /***************************判断订单时间是否超时结束*****************************************/
            //flag等于true没有超过15分钟
            if (flag) {
                //从微信订单表中拿出数据来，直接返回给前台
                WechatOrder wechatOrder = wchatOrderMapper.getByOderId(goodsOrder.getId());
                json.put("appId", wechatOrder.getAppid());
                json.put("timeStamp", wechatOrder.getTime_stamp());
                json.put("nonceStr", wechatOrder.getNonce_str());
                json.put("packageVal", wechatOrder.getPackageVal());
                json.put("paySign", wechatOrder.getPay_sign());
                json.put("timeOut", timeOut);//前台需要后台返回一个倒计时，告诉前台从哪倒计时开始
                return json;
            }
        }
        //4、调用微信下单操作，开始执行微信下单功能
        //获取预支付号
        String out_trade_no = UUID.randomUUID().toString();
        String prepayId = "";
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("appid", WechatConfig.appid);
        parameters.put("mch_id", WechatConfig.mch_id);
        parameters.put("nonce_str", WechatConfig.CreateNoncestr());
        parameters.put("body", "1601B支付系统");
        parameters.put("out_trade_no", out_trade_no);
        //110 等于一块一毛钱
        //10000 等于一百
        //如果数据库存的是double类型的，价格是10.5怎么传入微信里，10.5*100=1050
        parameters.put("total_fee", String.valueOf(goods.getPrice())+"00");
        parameters.put("spbill_create_ip", WechatConfig.spbill_create_ip);
        parameters.put("notify_url", WechatConfig.WECHAT_NOTIFY_URL);
        parameters.put("trade_type", "JSAPI");

        //生成签名的地方需要一个openid，所以把这个openid加上
        parameters.put("openid", openid);
        parameters.put("timeStamp", WechatConfig.create_timestamp());

        String sign = WechatConfig.createSign(parameters);
        parameters.put("sign", sign);
        //把map类型的参数转换成xml格式的
        String requestXML = WechatConfig.getRequestXml(parameters);

        //开始请求支付接口,返回微信的订单结果
        String preparyIdXml =CommonUtil.httpRequestJson(WechatConfig.wechatOrderUrl, "POST", requestXML);

        //把返回的订单结果转换为map类型的
        Map<String, String> prepayIdMap =new HashMap<String, String>();
        try {
            prepayIdMap = XMLUtil.doXMLParseString(preparyIdXml);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != prepayIdMap) {
            prepayId =prepayIdMap.get("prepay_id");
        }
        /*******************************重新生成签名开始*************************************/
        //重新需要生成一次签名
        String timeStamp=WechatConfig.create_timestamp();
        String nonceStr=WechatConfig.CreateNoncestr();


        SortedMap<Object,Object> signParams = new TreeMap<Object,Object>();
        signParams.put("appId", WechatConfig.appid);
        signParams.put("nonceStr",nonceStr);
        signParams.put("package", "prepay_id="+prepayId);
        signParams.put("timeStamp", timeStamp);
        signParams.put("signType", "MD5");
        // 生成支付签名，要采用URLENCODER的原始值进行MD5算法！
        String paySign= WechatConfig.createSign(signParams);

        /*******************************重新生成签名结束*************************************/

        //5、往咱们数据库中的订单表中插入一条订单数据，状态为未支付状态和微信订单支付表里插入一条数据

        //6、根据微信的下单返回来的微信id等参数返回到前台

        //7、返回一个时间戳，让前台做倒计时
        return null;
    }

    @Override
    public String wechatNotifyUrl(Map<String, String> map) {


        //获取微信支付的返回结果是否是SUCCESS
        String result_code = map.get("result_code");

        //获取用户唯一标识
        String openid = map.get("openid");
        //获取咱们数据库订单表中的id
        String out_trade_no = map.get("out_trade_no");

        //获取支付时的签名
        String sign = map.get("sign");
        //获取支付的价格
        String total_fee = map.get("total_fee");

        if(!result_code.equals("SUCCESS")){
            return "error";
        }

        /**
         * 根据订单id和openid往数据库的订单表中查询下订单的数据
         * 比较订单的签名和价格是否和微信返回回来的一致
         * 更新订单表的订单状态，更改为已支付，开始发货
         */

        return "SUCCESS";

    }






    /**
     * 微信在APP中支付
     * @param goodsId
     * @param goodsCount
     * @param userId
     * @param openid
     * @return
     */
    public JSONObject wechatAppUnifiedorder(String goodsId, int goodsCount, String userId,String openid) {

        //返回controller层用的
        JSONObject json = new JSONObject();

        //1、根据商品id查询出商品信息
        Goods goods = null;//goodsService.getById(goodsId);
        //2、根据商品数量乘以商品单价，得到要下订单的价格
        goods.setPrice(goods.getPrice()*goodsCount);
        //3、获取用户等级计算最后总价
        User user = null;//userService.getById(userId);

        /**
         *3.1、调用微信下单前，先根据用户id和goodsId从数据库中查询下订单时间
         *          *     把数据库中查出来的订单信息和现在的时间做比较，如果超过15分钟，
         *          *     并且订单状态是支付状态，继续调用微信下订单操作
         *
         *     如果没有超过十五分钟，并且订单状态是未支付状态，
         *     把订单表中中的prepareId、签名等信息直接返回到前台即可
         */
        GoodsOrder goodsOrder = wchatOrderMapper.getByUserIdAndGoodsIdAndStaus(userId,goodsId,0);

        if(goodsOrder!=null) {
            //根据订单表中的创建订单的时间和现在时间比较，判断订单时间是否超过了十五分钟
            /**************************判断订单时间是否超时开始******************************************/
            Date create_time = goodsOrder.getCreate_time();
            long currentTimeMillis = System.currentTimeMillis();
            //flag等于true时候是超过了十五分钟，等于false没超过十五分钟
            boolean flag = (currentTimeMillis - create_time.getTime()) > 900000 ? true : false;
            //倒计时的时间戳
            long timeOut = currentTimeMillis - create_time.getTime();

            /***************************判断订单时间是否超时结束*****************************************/
            //flag等于true没有超过15分钟
            if (flag) {
                //从微信订单表中拿出数据来，直接返回给前台
                WechatOrder wechatOrder = wchatOrderMapper.getByOderId(goodsOrder.getId());
                json.put("appId", wechatOrder.getAppid());
                json.put("timeStamp", wechatOrder.getTime_stamp());
                json.put("nonceStr", wechatOrder.getNonce_str());
                json.put("packageVal", wechatOrder.getPackageVal());
                json.put("paySign", wechatOrder.getPay_sign());
                json.put("timeOut", timeOut);//前台需要后台返回一个倒计时，告诉前台从哪倒计时开始
                return json;
            }
        }
        //4、调用微信下单操作，开始执行微信下单功能
        //获取预支付号
        String out_trade_no = UUID.randomUUID().toString();
        String prepayId = "";
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("appid", WechatConfig.appid);
        parameters.put("mch_id", WechatConfig.mch_id);
        parameters.put("nonce_str", WechatConfig.CreateNoncestr());
        parameters.put("body", "1601B支付系统");
        parameters.put("out_trade_no", out_trade_no);
        //110 等于一块一毛钱
        //10000 等于一百
        //如果数据库存的是double类型的，价格是10.5怎么传入微信里，10.5*100=1050
        parameters.put("total_fee", String.valueOf(goods.getPrice())+"00");
        parameters.put("spbill_create_ip", WechatConfig.spbill_create_ip);
        parameters.put("notify_url", WechatConfig.WECHAT_NOTIFY_URL);
        parameters.put("trade_type", "APP");

        //生成签名的地方需要一个openid，所以把这个openid加上
//        parameters.put("openid", openid);
        parameters.put("timeStamp", WechatConfig.create_timestamp());

        String sign = WechatConfig.createSign(parameters);
        parameters.put("sign", sign);
        //把map类型的参数转换成xml格式的
        String requestXML = WechatConfig.getRequestXml(parameters);

        //开始请求支付接口,返回微信的订单结果
        String preparyIdXml =CommonUtil.httpRequestJson(WechatConfig.wechatOrderUrl, "POST", requestXML);

        //把返回的订单结果转换为map类型的
        Map<String, String> prepayIdMap =new HashMap<String, String>();
        try {
            prepayIdMap = XMLUtil.doXMLParseString(preparyIdXml);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != prepayIdMap) {
            prepayId =prepayIdMap.get("prepay_id");
        }
        /*******************************重新生成签名开始*************************************/
        //重新需要生成一次签名
        String timeStamp=WechatConfig.create_timestamp();
        String nonceStr=WechatConfig.CreateNoncestr();


        SortedMap<Object,Object> signParams = new TreeMap<Object,Object>();
        signParams.put("appId", WechatConfig.appid);
        signParams.put("nonceStr",nonceStr);
        signParams.put("package", "prepay_id="+prepayId);
        signParams.put("timeStamp", timeStamp);
        signParams.put("signType", "MD5");
        // 生成支付签名，要采用URLENCODER的原始值进行MD5算法！
        String paySign= WechatConfig.createSign(signParams);

        /*******************************重新生成签名结束*************************************/

        //5、往咱们数据库中的订单表中插入一条订单数据，状态为未支付状态和微信订单支付表里插入一条数据

        //6、根据微信的下单返回来的微信id等参数返回到前台

        //7、返回一个时间戳，让前台做倒计时
        return null;
    }


}
