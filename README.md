# cordova-plugin-alipay
最新(2017)支付宝APP支付插件,支付Cordova,phonegap,ionic。本插件为服务器端（PHP）签名的版本，稍候会上传APP内实现签名的版本。

## 诚邀精英
欢迎技术牛人加入我们的团队，共同开发，为中国的开源事业做出贡献！
另，欢迎绍兴地区的PHP,前端应聘
QQ:88433062

## 支持的系统

* iOS*(暂不支持)
* Android

## 使用方法
```
window.alipay.pay({
    orderInfo: '订单信息',
}, function(successResults){alert(successResults)}, function(errorResults){alert(errorResults)});
```
## 支付宝开放平台签名设置
1.签名（RSA2）生成工具：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1

2.生成后点击工具中的『上传公钥』

## PHP生成订单信息
1.下载PHP SDK：https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.xQY82W&treeId=204&articleId=106079&docType=1

2.配置SDK
```
<?php
define('APPID', 'xxx');
define('PID', 'xxx');
define('NOTIFY_URL', 'xxx');
// 应用公钥
define('RSA2_PUPLIC', 'xxx');
// 应用私钥
define('RSA2_PRIVATE', 'xxx');
// 支付宝公钥
define('ALIPAY_RSA2_PUPLIC', 'xxx');
```
3.生成签名
```
$aop = new AopClient;
$aop->gatewayUrl = "https://openapi.alipay.com/gateway.do";
$aop->appId = APPID;
$aop->rsaPrivateKey = RSA2_PRIVATE;
$aop->format = "json";
$aop->charset = "UTF-8";
$aop->signType = "RSA2";
$aop->alipayrsaPublicKey = RSA2_PUPLIC;
//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
$request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数
$bizcontent = "{\"body\":\"".$ordername."\","
                . "\"subject\": \"".$ordername."\","
                . "\"out_trade_no\": \"".$orderid."\","
                . "\"timeout_express\": \"30m\","
                . "\"total_amount\": \"".$total_fee."\","
                . "\"product_code\":\"QUICK_MSECURITY_PAY\""
                . "}";
$request->setNotifyUrl(NOTIFY_URL);
$request->setBizContent($bizcontent);
$response = $aop->sdkExecute($request);


exit(json_encode(array(
    'status' => 0,
    'orderInfo' => $response,
    'orderid' => $orderid
)));
```

4.回调处理
```
<?php
$aop = new AopClient;
$aop->alipayrsaPublicKey = ALIPAY_RSA2_PUPLIC;
// 因为我们选择的是RSA2加密，所以参数三要传RSA2
$flag = $aop->rsaCheckV1($_POST, NULL, 'RSA2');
if($flag) {
    //todo
}
```

## PHP SDK常见错误
1.Fatal error: Class 'LtObjectUtil' not found
解决：请在 php.ini 开启 scandir 参数