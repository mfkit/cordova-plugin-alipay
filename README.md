# cordova-plugin-alipay
最新(2017)支付宝APP支付插件,支付Cordova,phonegap,ionic

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
生成APPID、PID、RSA2_PRIVATE（工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1）

生成后点击工具中的『上传公钥』

## PHP生成订单信息
```
require_once(FANWE_ROOT . 'payment/app/alipay/common.inc.php');
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