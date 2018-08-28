<%--
  Created by IntelliJ IDEA.
  User: lizexing
  Date: 2018/8/25
  Time: 9:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-3.3.1.min.js"></script>
</head>
<body>


<div>

    收款方：1601B<br/>
    商品：潘玉鑫<br/>
    价格：0.1亿<br/>
    <input type="button" onclick="onBridgeReady()"/>

</div>


<script type="text/javascript">

    function onBridgeReady(){

        WeixinJSBridge.invoke(
            'getBrandWCPayRequest', {
                "appId":${appId},     //公众号名称，由商户传入
                "timeStamp":${timeStamp},         //时间戳，自1970年以来的秒数
                "nonceStr":${nonceStr}, //随机串
                "package":${packageVal},
                "signType":"MD5",         //微信签名方式：
                "paySign":${paySign} //微信签名
            },
            function(res){
                if(res.err_msg == "get_brand_wcpay_request:ok" ){
                    // 使用以上方式判断前端返回,微信团队郑重提示：
                    //res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。

                }
            });
    }



    // if (typeof WeixinJSBridge == "undefined"){
    //     if( document.addEventListener ){
    //         document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
    //     }else if (document.attachEvent){
    //         document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
    //         document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
    //     }
    // }else{
    //     onBridgeReady();
    // }


</script>

</body>
</html>
