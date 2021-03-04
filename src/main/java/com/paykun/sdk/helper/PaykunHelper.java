package com.paykun.sdk.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class PaykunHelper {


    public static String urlTransactionInfoSandbox= "https://sandbox.paykun.com/api/v1/merchant/transaction/";
    public static String urlTransactionInfo= "https://api.paykun.com/v1/merchant/transaction/";

    //public static String urlOrder="https://checkout.paykun.com/";///order/create";
    //public static String urlOrderTest="https://sandbox.paykun.com/";///order/create";

    //public static String urlOrderUAT="https://uatcheckout.paykun.com/order/create";
    public static String urlOrder="https://checkout.paykun.com/v2/order/create";
   // public static String urlOrder="https://checkout2.paykun.com/v2/order/create";
    public static String upistatus="https://checkout.paykun.com/v2/order/process_sdk_intent_response";

    public static String urlOrderTest="https://sandbox.paykun.com/order/create";

    //public static String urlOrderTest="https://test.checkout.paykun.com/order/create";

    public static String KEY_SIGNATURE="signature";
    public static String WEBVIEW_HANDLER="WebViewHandler";
    public static int VOLLEY_TIME_OUT=50000;
    public static String MESSAGE_FAILED="failed";
    public static String MESSAGE_CANCELLED="cancelled";
    public static String MESSAGE_SERVER_ISSUE="there is some service issue";
    public static String MESSAGE_MERCHANT_ID_MISSING="You merchant id is missing";
    public static String MESSAGE_ACCESS_TOKEN_MISSING="Your access token is missing";
    public static String MESSAGE_INVALID_REQUEST="invalid request";
    public static String MESSAGE_NETWORK_NOT_AVAILABLE="network is not available";

    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager conMgr =  (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        return netInfo != null;
    }
}
