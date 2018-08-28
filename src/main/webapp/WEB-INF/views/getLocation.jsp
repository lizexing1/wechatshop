<%--
  Created by IntelliJ IDEA.
  User: lizexing
  Date: 2018/8/23
  Time: 9:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>

<body>



<script type="text/javascript">

    document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
        getLocationWX();
    });

    function getLocationWX(){
        wx.getLocation({
            type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
            success: function (res) {
                var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                var speed = res.speed; // 速度，以米/每秒计
                var accuracy = res.accuracy; // 位置精度
                console.log(latitude+"--latitude");
                console.log(longitude+"--longitude");
                console.log(speed+"---speed");
                console.log(accuracy+"---accuracy");
            },
            error: function (res) {
                debugger;
            }
        });
    }



</script>

</body>
</html>
