package com.lizx.wechatshop.util;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class WechatConfig {

    //微信公众号的应用id
    public static String appid = "wxe4e4c89c099df28b";
    //微信公众号的开发者秘钥AppSecret
    public static String secret = "017b50f403851f52874ee9f827fb2e80";
    //微信公众号支付的商户号
    public static String mch_id = "1288900901";
    //微信商户里面设置的秘钥  key为商户平台设置的密钥key
    public static String API_KEY = "sjdkljksfdkjlsd";
    //服务器的ip地址
    public static String spbill_create_ip = "120.78.167.80";
    //支付完成后，微信通知咱们平台支付结果的地址
    public static String WECHAT_NOTIFY_URL = "http://www.lizexing.cn/wechatshop/goods/wechatNotifyUrl";

    //获取token的url
    public static String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    //获取用户权限的token，根据这个token可以获取用户信息
    public static String oauth2TokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    //改变菜单的url
    public static String changeMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    //发送模板消息的url
    public static  String templateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    //网页授权地址
    public static  String wechatAuthorize = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
    //没有经过授权的，调用普通用户信息的url
    public static  String simpleUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    //用于检验token是否过时的url
    public static  String checkTokenTimeOutUrl = "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID";
    //token过期后刷新token的url
    public static String refreshTokenUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    //微信调用同意下单的接口地址
    public static String wechatOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    /**
     *判断access_token有没有过时，过时就调用刷新access_token，没过时原封不动返回
     * @param access_token
     * @param openid
     * @param refresh_token
     * @param request
     * @return
     */
    public static String freshToken(String access_token, String openid,
                                    String refresh_token, HttpServletRequest request){

        //先检验下access_token是否超时了
        String checkTokenTimeOutUrl = WechatConfig.checkTokenTimeOutUrl.replace("ACCESS_TOKEN",access_token)
                .replace("OPENID",openid);

        String checkTokenResult = HttpClientUtils.httpGet(checkTokenTimeOutUrl);

        JSONObject json = JSONObject.fromObject(checkTokenResult);
        String errmsg = (String) json.get("errmsg");

        if(!errmsg.equals("ok")){//token超时，执行下重新刷新token的操作
            String refreshTokenUrl = WechatConfig.refreshTokenUrl.replace("APPID", WechatConfig.appid)
                    .replace("REFRESH_TOKEN", refresh_token);
            String refreshTokenStr = HttpClientUtils.httpGet(refreshTokenUrl);
            JSONObject refreshTokenJson = JSONObject.fromObject(refreshTokenStr);
            access_token = (String) refreshTokenJson.get("access_token");
            refresh_token = (String) refreshTokenJson.get("refresh_token");
            request.getSession().setAttribute("access_token",access_token);
            request.getSession().setAttribute("refresh_token",refresh_token);
        }
        return access_token;
    }


    /**
     * 获取预支付ID时  获取随机码
     * @param
     * @return
     */
    public static String CreateNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    /*************************************签名算法开始****************************************/
    public static String createSign(SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        String sign = MD5Util.MD5Encode(sb.toString(),"UTF-8").toUpperCase();
        return sign;
    }



    /*************************************签名算法结束****************************************/


    /************************************Map转XML开始****************************************************/
    public static String getRequestXml(SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)||"sign".equalsIgnoreCase(k)) {
                sb.append("<"+k+">"+"<![CDATA["+v+"]]></"+k+">");
            }else {
                sb.append("<"+k+">"+v+"</"+k+">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /************************************Map转XML结束****************************************************/


    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }



}
