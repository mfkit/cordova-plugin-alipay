package com.mfkit.plugin.alipay;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import com.alipay.sdk.app.PayTask;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class AliPayPlugin extends CordovaPlugin {

    private static String TAG = "AliPayPlugin";

    private static final int SDK_PAY_FLAG = 1;

    protected CallbackContext callbackContext = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;

        try {
            JSONObject arguments = args.getJSONObject(0);
            String orderInfo = arguments.getString("orderInfo");
            this.pay(orderInfo);
        } catch (JSONException e) {
            callbackContext.error(new JSONObject());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SDK_PAY_FLAG: {
                @SuppressWarnings("unchecked")
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                /**
                 *对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    callbackContext.success(resultInfo);
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    callbackContext.error(resultInfo);
                }
                break;
            }
            default:
                break;
            }
        };
    };

    public void pay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(cordova.getActivity());
                Log.d(TAG, orderInfo);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
