package com.lizx.wechatshop.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lizx.wechatshop.entity.Goods;
import com.lizx.wechatshop.service.GoodsService;
import com.lizx.wechatshop.util.AlipayConfig;
import com.lizx.wechatshop.util.HttpClientUtils;
import com.lizx.wechatshop.util.WechatConfig;
import com.lizx.wechatshop.util.XMLUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("goods")
@Api(value = "goods",tags={"商品操作"})
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("changeMenu")
    public void changeMenu(){

        goodsService.changeMenu();

    }

    @RequestMapping("getLocation")
    public String getLocation(){
        return "getLocation";
    }

       /**
     * 不需要授权的调用
     */
    @RequestMapping("wechatAuthorize")
    public void wechatAuthorize(HttpServletRequest request,HttpServletResponse response,String status){

        String openid = (String) request.getSession().getAttribute("openid");
        String access_token = (String) request.getSession().getAttribute("access_token");
        if(!StringUtils.isBlank(openid)){

            try {
                if(status.equals("1")){//个人中心
                    response.sendRedirect("http://www.lizexing.cn/wechatshop/goods/personalCenter");
                }else if(status.equals("2")){//商城首页
                    //return "redirect:index";
                }else if(status.equals("3")){//学习社区

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        String url = URLEncoder.encode("http://www.lizexing.cn/wechatshop/goods/getCode");
        String wechatAuthorizeUrl = WechatConfig.wechatAuthorize.replace("APPID",WechatConfig.appid).
                replace("REDIRECT_URI",url).replace("SCOPE","snsapi_base")
                .replace("STATE",status);

        try {
            response.sendRedirect(wechatAuthorizeUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 需要授权的调用
     */
    @RequestMapping("wechatAuthorizeUserInfo")
    public void wechatAuthorizeUserInfo(HttpServletResponse response){
        //做为参数的URL
        String url = URLEncoder.encode("http://www.lizexing.cn/wechatshop/goods/getCode");
        //访问的URL
        String wechatAuthorizeUrl = WechatConfig.wechatAuthorize.replace("APPID",WechatConfig.appid).
                replace("REDIRECT_URI",url).replace("SCOPE","snsapi_userinfo");

        System.out.println("-----"+wechatAuthorizeUrl);

        try {
            response.sendRedirect(wechatAuthorizeUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据微信返回回来的code获取用户openid和token，然后再根据state判断决定跳转哪个页面
     * @param code
     * @param state
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping("getCode")
    public String getCode(String code, String state, HttpServletRequest request, HttpServletResponse response, Model model){

        System.out.println("code==="+code);
        System.out.println("state==="+state);

        //拼接获取token的url
       String oauth2TokenUrl = WechatConfig.oauth2TokenUrl.replace("APPID",WechatConfig.appid).
                replace("SECRET",WechatConfig.secret).replace("CODE",code);

        System.out.println("oauth2TokenUrl=="+oauth2TokenUrl);

        String result = HttpClientUtils.httpGet(oauth2TokenUrl);
        System.out.println("result==="+result);

        JSONObject json = JSONObject.fromObject(result);
        String access_token = (String) json.get("access_token");
        String refresh_token = (String) json.get("refresh_token");
        String openid = (String) json.get("openid");
        if(StringUtils.isBlank(access_token)){
            /**
             * 记录日志
             * log.error(根据code获取access_token时出现错误！)
             */
            return null;
        }

        //把openid和access_token放入到session中，如果是分布式项目，放入redis当中
        request.getSession().setAttribute("access_token",access_token);
        request.getSession().setAttribute("openid",openid);
        request.getSession().setAttribute("refresh_token",refresh_token);

        try {
            if(state.equals("1")){//个人中心
                response.sendRedirect("http://www.lizexing.cn/wechatshop/goods/personalCenter");
            }else if(state.equals("2")){//商城首页
                return "redirect:index";
            }else if(state.equals("3")){//学习社区

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 個人中心頁面
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping("personalCenter")
    public String personalCenter(HttpServletRequest request, HttpServletResponse response, Model model){

        String openid = (String) request.getSession().getAttribute("openid");
        String access_token = (String) request.getSession().getAttribute("access_token");
        String refresh_token = (String) request.getSession().getAttribute("refresh_token");
        System.out.println("openid=="+openid);
        System.out.println("access_token=="+access_token);
        System.out.println("refresh_token=="+refresh_token);
        //判断access_token有没有过时，过时就调用刷新access_token，没过时原封不动返回
        access_token = WechatConfig.freshToken(access_token,openid,refresh_token,request);

        //拼接获取用户信息的url
        String simpleUserInfoUrl = WechatConfig.simpleUserInfoUrl.replace("ACCESS_TOKEN",access_token)
                .replace("OPENID",openid);

        System.out.println("simpleUserInfoUrl==="+simpleUserInfoUrl);
        //获取用户信息
        String result = HttpClientUtils.httpGet(simpleUserInfoUrl);
        System.out.println("result====="+result);
        JSONObject json = JSONObject.fromObject(result);

        String nickname = (String) json.get("nickname");
        Integer sex = (Integer) json.get("sex");
        String headimgurl = (String) json.get("headimgurl");

        /**
         *根据openid往数据库中查询数据库中是否有用户信息，如果没有，提醒用户完事信息,可以让用户跳转到完善信息页面
         * 如果数据库中有用户信息，直接跳转到个人中心
         * 把数据库中有的用户的手机号，邮寄地址等信息都可以放入到个人中心页面中
         */
        model.addAttribute("nickname",nickname);
        model.addAttribute("sex",sex);
        model.addAttribute("headimgurl",headimgurl);
        return "personalCenter";
    }

    /**
     * 進入到商城首頁
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value="index", method = RequestMethod.GET)
    @ResponseBody
    public String index(HttpServletRequest request, HttpServletResponse response, Model model,
                       Goods goods){

        List<String> list = new ArrayList<String>();
        list.add("dddddddd");
        list.add("dssss");
        list.add("ffffffffff");

        JSONObject json = new JSONObject();
        json.put("list",list);
        json.put("goods",goods);
        System.out.println(goods.getPrice());
        return json.toString();
    }


    /**
     * 微信支付调用方法
     * @param request
     * @param response
     * @param model
     * @param goodsId 商品id
     * @param goodsCount  商品数量
     * @param token  传入token防止重复提交
     * @return
     */
    @RequestMapping("wechatUnifiedorder")
    public String wechatUnifiedorder(HttpServletRequest request, HttpServletResponse response,
           Model model,String goodsId,int goodsCount,String token){

        //获取用户id
        String userId = (String) request.getSession().getAttribute("userId");
        //获取session中的token今夕比较，防止用户重复提交
        String sessionToken = (String) request.getSession().getAttribute("token");
        //微信公众号用户的唯一标识，可以判断是哪个用户在操作
        String openid = (String) request.getSession().getAttribute("openid");

        //防止用户重复提交的操作
        request.getSession().setAttribute("token","");
        if(token.equals(sessionToken)){
            //防止用户重复提交
            goodsService.wechatUnifiedorder(goodsId,goodsCount,userId,openid);
        }


        return null;
    }

    /**
     * 用户支付完成后，微信异步通知咱们平台支付结果
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="wechatNotifyUrl",method={RequestMethod.GET,RequestMethod.POST})
    public void wechatNotifyUrl(HttpServletRequest request,HttpServletResponse response){

        try {
            //获得请求体中的值
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            //把传过来的输入流转换为输出流
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            //把输出流转换为字符串
            String result = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> map = null;
            //把微信传回来的字符串xml转换为map类型的，就可以键值对获取数据了
            map = XMLUtil.doXMLParseString(result);

            String message = goodsService.wechatNotifyUrl(map);


            String xml = "<xml><return_code><![CDATA["+message+"]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            //给微信返回结果成功或失败，微信就不会再调用咱们平台了
            response.getWriter().write(xml);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("alipay")
    public void alipay(HttpServletResponse response){

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.ALIPAY_URL,
                    AlipayConfig.APPID,AlipayConfig.APP_PRIVATE_KEY,AlipayConfig.FORMAT,AlipayConfig.CHARSET,
                    AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGN_TYPE);

            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
            alipayRequest.setReturnUrl(AlipayConfig.RETURN_URL);
            alipayRequest.setNotifyUrl(AlipayConfig.NOTIFY_URL);//在公共参数中设置回跳和通知地址

            String out_trade_no = UUID.randomUUID().toString();

            JSONObject json = new JSONObject();
            json.put("out_trade_no",out_trade_no);
            json.put("product_code","FAST_INSTANT_TRADE_PAY");
            json.put("total_amount",1.10);
            json.put("subject","1601B支付系统");

            alipayRequest.setBizContent(json.toString());//填充业务参数


            String form="";
            try {
                form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            response.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//
//        /**
//         * 获取access_token
//         */
//        String url = WechatConfig.tokenUrl.replace("APPID", WechatConfig.appid).replace("APPSECRET", WechatConfig.secret);
//        String result = HttpClientUtils.httpGet(url);
//        System.out.println(result);
//        JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
//        String access_token = jsonObject.get("access_token").getAsString();
//        if(access_token==null){
//            //log.error("");
//            return;
//        }
//        /**
//         * 改变菜单按钮
//         */
//        String changeMenuUrl = WechatConfig.changeMenuUrl.replace("ACCESS_TOKEN",access_token);
//
//        /**
//         * 修改灵活数据
//         */
//        String param = "{\"button\":[{\"type\":\"click\",\"name\":\"今日歌曲\",\"key\":\"V1001_TODAY_MUSIC\"},{\"name\":\"菜单\",\"sub_button\":[{\"type\":\"view\",\"name\":\"搜索\",\"url\":\"http://www.soso.com/\"},{\"type\":\"click\",\"name\":\"赞一下我们\",\"key\":\"V1001_GOOD\"}]}]}";
//
//
//
//        String changeMenuResult = HttpClientUtils.httpPost(changeMenuUrl, param);
//        System.out.println(changeMenuResult);
//
//    }


    /**
     * 模板推送消息，用户给固定用户，推送固定消息用
     * @param args
     */
    //public static void main(String[] args) {


        /**
         *这是activemq监听消息地方，有用户的手机号
         * 根据用户的手机号查询到数据库里面对应的用户的openid
         * 由于手机号不是唯一的，查询回来一个集合
         */


        //获取token的操作
//        String url = WechatConfig.tokenUrl.replace("APPID", WechatConfig.appid).replace("APPSECRET", WechatConfig.secret);
//        String result = HttpClientUtils.httpGet(url);
//        System.out.println(result);
//        JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
//        String access_token = jsonObject.get("access_token").getAsString();
//        if(access_token==null){
//            //log.error("");
//            return;
//        }
//
//
//        //拼接templateUrl
//        String tokenUrl = WechatConfig.templateUrl.replace("ACCESS_TOKEN",access_token);
//
//        JSONObject param = new JSONObject();
//        param.put("touser","oKOSgwwuItE14n2HGZ_CRLwsULf8");//openid从数据库查找出来的
//        param.put("template_id","8tQmkicN05YLTXuRuX7ug1zqy32jZPmaMWaFGhjdBt4");
//        /**
//         * //详情页面的地址后面跟一个商品的id，最后点击链接的时候跳转到商品详情页
//         * 录取通知书，传入的是一个用户的id，根据用户的id，查询到用户的详细信息，返回的录取通知书的详情页面
//         * http://www.lizexing.cn/项目名/录取通知书详情页?userid = userid
//         */
//        param.put("url","http://www.baidu.com/");
//
//
//
//        JSONObject firstParam = new JSONObject();
//        firstParam.put("value","恭喜王月闯面试成功98K！！！");
//
//        JSONObject key1Param = new JSONObject();
//        key1Param.put("value","厕所管理员");
//
//        JSONObject key2Param = new JSONObject();
//        key2Param.put("value","20100-10-15");
//
//        JSONObject key3Param = new JSONObject();
//        key3Param.put("value","8栋厕所");
//
//        JSONObject key4Param = new JSONObject();
//        key4Param.put("value","8栋厕所阿姨，张兴晨");
//
//        JSONObject remarkParam = new JSONObject();
//        remarkParam.put("value","请点击查看详细录取书");
//
//        JSONObject jsonParam = new JSONObject();
//        jsonParam.put("first",firstParam);
//        jsonParam.put("keyword1",key1Param);
//        jsonParam.put("keyword2",key2Param);
//        jsonParam.put("keyword3",key3Param);
//        jsonParam.put("keyword4",key4Param);
//        jsonParam.put("remark",remarkParam);
//
//        param.put("data",jsonParam);
//
//
//        String sendTemplateResult = HttpClientUtils.httpPost(tokenUrl, param.toString());
//        System.out.println(sendTemplateResult);
//
//    }

}
