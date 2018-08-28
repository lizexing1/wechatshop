package com.lizx.wechatshop.controller;


import com.lizx.wechatshop.entity.wechat.TextImgMessage;
import com.lizx.wechatshop.entity.wechat.TextMessage;
import com.lizx.wechatshop.util.XMLUtil;
import net.sf.json.JSONObject;
import org.jdom2.JDOMException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/wechat")
public class WechatHandler {

    /**
     * 微信接入
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/connect",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void connectWeixin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");  //微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        response.setCharacterEncoding("UTF-8"); //在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        System.out.println("----------------------");
        PrintWriter out = response.getWriter();

        try {
            if (isGet) {
                String signature = request.getParameter("signature");// 微信加密签名
                String timestamp = request.getParameter("timestamp");// 时间戳
                String nonce = request.getParameter("nonce");// 随机数
                String echostr = request.getParameter("echostr");//随机字符串

                // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  if (SignUtil.checkSignature(DNBX_TOKEN, signature, timestamp, nonce)) {

                response.getWriter().write(echostr);
                return;
            }

            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            String result = new String(outSteam.toByteArray(), "UTF-8");
            Map<String, String> map = null;
            try {
                map = XMLUtil.doXMLParseString(result);
            } catch (JDOMException e) {
                e.printStackTrace();
            }
            //logger.info("request MSG:" + JSONObject.fromObject(map).toString());

            String respontMSG = "";
            String event = map.get("Event");
            String msgType = map.get("MsgType");

            System.out.println("--------"+msgType);
            System.out.println("--------"+map.get("Content"));

            String resultMsg = switchMessage(msgType, map);
            response.getWriter().write(resultMsg);

//            if(msgType.equals("text")){
//                if(map.get("Content").equals("你好")){
//
//                    String content = "<xml><ToUserName><![CDATA["+map.get("FromUserName")+"]]></ToUserName><FromUserName><![CDATA["+map.get("ToUserName")+"]]></FromUserName><CreateTime>12345678</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[你猜我好不好！！！]]></Content></xml>";
//                    response.getWriter().write(content);
//                }
//            }

    } catch (Exception e) {

    }finally{
        out.close();
    }
}


    public String switchMessage(String msgType,Map<String, String> map){
        String result ="";
        switch (msgType){
            case "text":
                result = sendTextMessage(map);
                break;
            case "event":
                result = sendEventMessage(map);
                break;
            default:
                result = "success";
                break;

        }
        return result;
    }



    public String sendTextMessage(Map<String, String> map){

        String result = "";

        String content = map.get("Content");
        /**
         * 根据用户传过来的文本内容进行相应的操作，可以拿着用户传过来的文本内容去solr当中检索到相应的数据类型
         * 最后给用户返回相应的结果
         */

        if(content.equals("你好")){
            result = textMessage(map,"我一点也不好");
        }else if(content.equals("图文消息")){

            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

            Map<String,Object> resultMap = new HashMap<String,Object>();

            resultMap.put("title","测试一下");
            resultMap.put("description","返回一个美女");
            resultMap.put("picUrl","http://t2.hddhhn.com/uploads/tu/201804/9999/e257d1b578.jpg");
            resultMap.put("url","https://image.baidu.com/");


            Map<String,Object> resultMap1 = new HashMap<String,Object>();

            resultMap1.put("title","我是第二个");
            resultMap1.put("description","换一个美女");
            resultMap1.put("picUrl","http://t2.hddhhn.com/uploads/tu/201803/9999/fdb931ccd6.jpg");
            resultMap1.put("url","https://v.qq.com/");

            list.add(resultMap);
            list.add(resultMap1);

            result = textImageMessage(map,list);
        }else{
            result = "success";
        }

        return  result;
    }

    //返回文本消息
    public String textMessage(Map<String, String> map,String resultContent){
        TextMessage text = new TextMessage();
        text.setToUserName(map.get("FromUserName"));
        text.setFromUserName(map.get("ToUserName"));
        text.setMsgType("text");
        text.setCreateTime(System.currentTimeMillis());
        text.setContent(resultContent);
        //根据用户传来的文本内容
        return text.toXML();
    }

    //返回图文消息
    public String textImageMessage(Map<String, String> map, List<Map<String, Object>> list){

        TextImgMessage textImgMessage = new TextImgMessage();
        textImgMessage.setToUserName(map.get("FromUserName"));
        textImgMessage.setFromUserName(map.get("ToUserName"));
        textImgMessage.setMsgType("news");
        textImgMessage.setCreateTime(System.currentTimeMillis());
        textImgMessage.setList(list);

        return textImgMessage.toXML();
    }

    /**
     * 关注或取消关注后，执行一个事件
     * @param map
     * @return
     */
    private String sendEventMessage(Map<String,String> map) {

        String event = map.get("Event");
        String result ="";
        switch (event){
            case "subscribe"://关注公众号
                result = sendTextImageMessage(map);
                break;
            case "unsubscribe"://取消关注公众号
                //操作数据库
                break;
            case "scancode_push"://扫码事件
                result = sendScancodeMessage(map);
                break;
            default:
                break;

        }
        return result;
    }

    /**
     * 用户扫描二维码时的操作
     * @param map
     * @return
     */
    private String sendScancodeMessage(Map<String,String> map) {

        return null;
    }

    /**
     * 关注公众号后发送图文消息
     * @param map
     * @return
     */
    private String sendTextImageMessage(Map<String,String> map) {

        String result = "";

        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

        Map<String,Object> resultMap = new HashMap<String,Object>();

        resultMap.put("title","测试一下");
        resultMap.put("description","返回一个美女");
        resultMap.put("picUrl","http://t2.hddhhn.com/uploads/tu/201804/9999/e257d1b578.jpg");
        resultMap.put("url","https://image.baidu.com/");


        Map<String,Object> resultMap1 = new HashMap<String,Object>();

        resultMap1.put("title","我是第二个");
        resultMap1.put("description","换一个美女");
        resultMap1.put("picUrl","http://t2.hddhhn.com/uploads/tu/201803/9999/fdb931ccd6.jpg");
        resultMap1.put("url","https://v.qq.com/");

        list.add(resultMap);
        list.add(resultMap1);

        result = textImageMessage(map,list);
        return result;
    }
}
