package com.paykun.sdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paykun.sdk.helper.PaykunHelper;
import com.paykun.sdk.logonsquare.Billing;
import com.paykun.sdk.logonsquare.Customer;
import com.paykun.sdk.logonsquare.Order;
import com.paykun.sdk.logonsquare.Shipping;
import com.paykun.sdk.logonsquare.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
/* this is main activity of library which contain all functionality - like call order web service and get response,
 *   after getting response call webview to open and make payment and also notify application about transaction status */

public class  PaykunPaymentActivity extends  AppCompatActivity  {
    public  static boolean issendstate=false;
    private static final String TAG = "Paykun";
    long startTime;
    long endTime;
    private RequestQueue mRequestQueue;
    WebView webView;
    ProgressBar progressBar;
    ImageView imgProgress;
    String orderUrl = "";
    String regexStr = "^[0-9]{10,30}$";
    private String merchantId, accessToken, packageName="";
    String isLive = "";
    Toolbar toolbar;
    private final int UPI_PAYMENT=555;
    String req_id="";
    @Override
    protected void onStart() {
        super.onStart();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
      /*  mCustomTabActivityHelper = new CustomTabActivityHelper(this);
        mCustomTabActivityHelper.setConnectionCallback(this);*/
        setContentView(R.layout.activity_paykun_payment);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                if (webView != null) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                        webView.loadUrl(orderUrl + "#failed");
                        if (toolbar.getVisibility() == View.VISIBLE) {
                            toolbar.setVisibility(View.GONE);
                        }
                    } else {
                        finish();
                    }
                }
            }
        });

        progressBar = findViewById(R.id.progressbar);
        webView = findViewById(R.id.webview);
        imgProgress = findViewById(R.id.img_progress);

        StartAnimate();
        if (PaykunHelper.isNetworkAvailable(PaykunPaymentActivity.this)) {
            //Disable_Certificate_Validation_Java_SSL_Connections();
            callCreateOrderService(PaykunPaymentActivity.this);
        } else {
            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE, "0", null);
            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
            finish();
        }
       /* if (PaykunHelper.isNetworkAvailable(this)) {
            //Disable_Certificate_Validation_Java_SSL_Connections();
            callCreateOrderService(this);
        }*/ /*else {
            Events.PaymentMessage activityActivityMessageEvent = new Events.PaymentMessage(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE, "0", null);
            GlobalBus.getBus().postSticky(activityActivityMessageEvent);
            finish();
        }*/

    }
    public void  StartAnimate()
    {

        imgProgress.setVisibility(View.VISIBLE);
        final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        final Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        imgProgress.startAnimation(animFadeIn);
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgProgress.startAnimation(animFadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgProgress.startAnimation(animFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //replaces the default 'Back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null) {
                if (webView.canGoBack()) {
                    String url= webView.getUrl();
                    if(url!=null) {
                        if (url.equalsIgnoreCase(orderUrl + "#failed")) {
                            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, req_id, null);
                            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                            finish();
                        } else {
                            webView.goBack();
                            webView.loadUrl(orderUrl + "#failed");
                            if (toolbar.getVisibility() == View.VISIBLE) {
                                toolbar.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, req_id, null);
                    PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                    finish();
                }
            }
        }
        return true;
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        mCustomTabActivityHelper.bindCustomTabsService(this);
    }
*/
/*
    @Override
    protected void onStop() {
        super.onStop();
        mCustomTabActivityHelper.unbindCustomTabsService(this);

    }
*/

    @Override
    protected void onStop() {
        super.onStop();
     /*   if (mConnection == null) return;
        unbindService(mConnection);
        mConnection = null;*/
        //   mLaunchButton.setEnabled(false);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void callCreateOrderService(final AppCompatActivity activity) {
        if(!req_id.equals(""))
        {
            return;
        }
        startTime = System.currentTimeMillis();
        Log.e(" start time ", " calculate time consuming : " + startTime);
        JSONArray AppInfoData=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppInfoData= PaykunIntentHelper.getUpiIntentsDataInJsonArray(PaykunPaymentActivity.this);
        }

        if(AppInfoData!=null)
        {
            Log.e("Json App",AppInfoData.toString());
        }
        Intent intent = getIntent();
        showProgressbar();
        JSONObject object = null;
        try {
            if (PaykunApiCall.paykunTransactionrequest == null) {
                return;
            }
            else
            {
                object=new JSONObject();
            }
            if (PaykunApiCall.paykunTransactionrequest.getMerchant_id()!=null) {
                if (PaykunApiCall.paykunTransactionrequest.getAccess_token()!=null) {
                    if ( PaykunApiCall.paykunTransactionrequest.getOrder_no()!=null   && PaykunApiCall.paykunTransactionrequest.getAmount()!=null ) {
                        if (  PaykunApiCall.paykunTransactionrequest.getOrder_no().matches(regexStr)) {

                            merchantId = PaykunApiCall.paykunTransactionrequest.getMerchant_id();
                            object.put("merchant_id",merchantId);
                            accessToken = PaykunApiCall.paykunTransactionrequest.getAccess_token();
                            object.put("access_token",accessToken);
                            String customerName = "", customerEmail = "", customerPhone = "";
                            if ( PaykunApiCall.paykunTransactionrequest.getCustomer_name()!=null) {
                                customerName =PaykunApiCall.paykunTransactionrequest.getCustomer_name();
                                object.put("customer_name",customerName);
                            }
                            if (PaykunApiCall.paykunTransactionrequest.getCustomer_email()!=null) {
                                customerEmail =PaykunApiCall.paykunTransactionrequest.getCustomer_email();
                                object.put("customer_email",customerEmail);
                            }
                            if (PaykunApiCall.paykunTransactionrequest.getCustomer_phone()!=null) {
                                customerPhone =PaykunApiCall.paykunTransactionrequest.getCustomer_phone() ;
                                object.put("customer_phone",customerPhone);
                            }
                            String productName =PaykunApiCall.paykunTransactionrequest.getProduct_name();
                            object.put("product_name",productName);
                            String orderNo =PaykunApiCall.paykunTransactionrequest.getOrder_no();
                            object.put("order_no",orderNo);
                            String amount = PaykunApiCall.paykunTransactionrequest.getAmount();
                            object.put("amount",amount);
                            String timeStamp = String.valueOf(System.currentTimeMillis());
                            isLive =String.valueOf(PaykunApiCall.paykunTransactionrequest.isLive());
                            // if(PaykunApiCall.paykunTransactionrequest.getPackage_name()!=null) {

                            //   packageName = PaykunApiCall.paykunTransactionrequest.getPackage_name();
                            //   }
                            //  else
                            {
                                packageName=activity.getPackageName();
                            }

                            Log.e("packageName",packageName);

                            if(AppInfoData!=null)
                            {
                                object.put("available_intents", AppInfoData);
                            }
                            object.put("package_name", packageName);
                            object.put("timestamp", timeStamp);
                            if(PaykunApiCall.paykunTransactionrequest.getPayment_methods()==null)
                            {
                                object.put("config", "");
                            }else
                            {
                                Log.e("payment_methods Json 1",new Gson().toJson(PaykunApiCall.paykunTransactionrequest.getPayment_methods()));
                                String json=  new Gson().toJson(PaykunApiCall.paykunTransactionrequest.getPayment_methods());
                                JsonObject jsonarr=  new Gson().fromJson(json, JsonObject.class);
                                JsonObject jsondata=  new JsonObject();
                                jsondata.addProperty("theme_color", PaykunApiCall.paykunTransactionrequest.getTheme_color());
                                jsondata.addProperty("theme_logo", PaykunApiCall.paykunTransactionrequest.getTheme_logo());
                                jsondata.add("payment_methods",jsonarr);
                                Log.e("payment Methods Json 2",new Gson().toJson(jsondata));
                                object.put("config", jsondata);
                            }

                            /* new code for signature generation */
                            String[] _inputToBeHashed = {
                                    "access_token",
                                    "amount",
                                    "currency",
                                    "customer_email",
                                    "customer_name",
                                    "customer_phone",
                                    "merchant_id",
                                    "order_no",
                                    "package_name",
                                    "product_name",
                                    "timestamp",
                            };
                            String stringForSignature = "";
                            Iterator<?> keys = object.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                if (Arrays.asList(_inputToBeHashed).contains(key)) {
                                    String value = object.get(key).toString().trim();
                                    stringForSignature = stringForSignature.concat(value);
                                }
                            }
                            //String signanture = merchantId + accessToken + customerName + customerEmail + customerPhone + productName + orderNo + amount + packageName + timeStamp;
                            String signature2 = generateHashWithHmac256(stringForSignature, timeStamp);
                            String signatureFinal = generateHashWithHmac256(signature2, accessToken);

                            object.put(PaykunHelper.KEY_SIGNATURE, signatureFinal);
                            object.put("sdk_version", android.os.Build.VERSION.SDK_INT);
                            object.put("sdk_os", "Android");
                            //object.put("upiapp", AppInfoData);
                            object.put("callback", "paykun://checkout");
                            //object.put("callback", "http://www.test.com");

                            String url = "";
                            if (isLive.equals("false")) {
                                Log.e("isLive","false");
                                url = PaykunHelper.urlOrderTest;
                            } else {
                                Log.e("isLive","true");
                                url = PaykunHelper.urlOrder;
                            }

                            Log.e("PaymentRequest",object.toString());
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    url, object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response",response.toString());
                                    endTime = System.currentTimeMillis();
                                    long finalTime = endTime - startTime;
                                    Log.e(" final time ", " calculate time consuming : " + finalTime);
                                    String token = "";
                                    startTime = System.currentTimeMillis();
                                    String status = "";
                                    try {
                                        status = response.getString("status");
                                        if (status.equalsIgnoreCase("true")) {
                                            token = response.getString("_token");
                                            orderUrl = response.getString("order_url");
                                        } else {
                                            String message = response.getString("message");
                                            PaymentMessage activityActivityMessageEvent = new PaymentMessage(message, "0", null);
                                            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    Uri uri = Uri.parse(orderUrl);
                                    if (uri != null) {
                                        openWebView(token);
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    hideProgressbar();
                                    if (error.networkResponse != null) {
                                        if (error.networkResponse.data != null) {
                                            try {
                                                String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                                if (!TextUtils.isEmpty(body)) {
                                                    JSONObject objectMain = new JSONObject(body);
                                                    PaykunHelper.MESSAGE_SERVER_ISSUE = objectMain.getString("message");
                                                    PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_SERVER_ISSUE, "0", null);
                                                    PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                                                    finish();
                                                } else {
                                                    PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_SERVER_ISSUE, "0", null);
                                                    PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }


                                }
                            });
                            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(PaykunHelper.VOLLEY_TIME_OUT,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            addToRequestQueue(jsonObjReq);

                        } else {
                            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_INVALID_REQUEST, "0", null);
                            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                            finish();
                        }
                    } else {
                        PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_INVALID_REQUEST, "0", null);
                        PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        finish();
                    }
                } else {
                    PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_ACCESS_TOKEN_MISSING, "0", null);
                    PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                    finish();
                }
            } else {
                PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_MERCHANT_ID_MISSING, "0", null);
                PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_INVALID_REQUEST, "0", null);
            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
            finish();
        }

    }



    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void openWebView(String token) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        //  webView.set
        webView.getSettings().setDomStorageEnabled(true);
       // webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        String cookieString = "_token=" + token;
        progressBar.setVisibility(View.GONE);
        CookieManager.getInstance().setCookie(orderUrl, cookieString);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        //  webView.addJavascriptInterface(new JavaScriptInterface(PaykunPaymentActivity.this, webView), PaykunHelper.WEBVIEW_HANDLER);
        Log.e(TAG,orderUrl);
        webView.loadUrl(orderUrl);
        //   webView.set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //todo for debug
            //  WebView.setWebContentsDebuggingEnabled(true);

        }

    }




    /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
     @Override
     public void onCustomTabsConnected() {
         if (PaykunHelper.isNetworkAvailable(this)) {
             //Disable_Certificate_Validation_Java_SSL_Connections();
             callCreateOrderService(PaykunPaymentActivity.this);
         } else {
             PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE, "0", null);
             PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
             finish();
         }
     }

     @Override
     public void onCustomTabsDisconnected() {

     }*/
    public static  String lasturl="";
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Uri uri = Uri.parse(url);
            return handleUri(uri);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            return handleUri(uri);
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("onPageStarted",url);
        }
        private boolean handleUri(final Uri uri) {

            String url=uri.toString().trim();
            if(!lasturl.equals(url))
            {
                lasturl=url;
            }else
            {
                return true;
            }
            Log.e(TAG, "Uri =" + uri);
            Log.e("shouldOverride", url);
            if (url.contains("paykun://checkout")) {
                webView.setVisibility(View.INVISIBLE);
                StartAnimate();
                Uri myUri = Uri.parse(url);
                if (myUri.getQueryParameter("status") == null) {
                    return false;
                }
                if (myUri.getQueryParameter("req_id") == null) {
                    return false;
                }
                String status = Objects.requireNonNull(myUri.getQueryParameter("status")).trim();
                String reqId = Objects.requireNonNull(myUri.getQueryParameter("req_id")).trim();
                // Toast.makeText(getApplicationContext(),status+"-"+reqId,Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(status)) {
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        // PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_CANCELLED, reqId, null);
                        //  PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        //finish();
                        /*if (isLive.equalsIgnoreCase("false")) {
                            getTransactionDetail(reqId);
                        }*/
                        getTransactionDetail(reqId);
                    } else if (status.equalsIgnoreCase("CANCELLED")) {
                        PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_CANCELLED, reqId, null);
                        PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        finish();
                    } else if (status.equalsIgnoreCase("FAILED")) {
                        PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, reqId, null);
                        PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        finish();
                    }
                }
            }
            //  return false;

            if (url.contains("upi://pay")) {
                Log.e("upi", url);

                Uri myUri = Uri.parse(url);


                int n = url.indexOf("&intent=");
                if (n > 0) {
                    String pcg_nam = "";
                    req_id = "";
                    if (myUri.getQueryParameter("intent") != null) {
                        pcg_nam = myUri.getQueryParameter("intent");
                    }
                    if (myUri.getQueryParameter("req_id") != null) {
                        req_id = myUri.getQueryParameter("req_id");
                    }
                    url = url.substring(0, n);
                    Log.e("url", url);
                    Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                    upiPayIntent.setData(Uri.parse(url));
                    Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
                    upiPayIntent.setPackage(pcg_nam);
                    PackageManager packageManager = getPackageManager();
                    if (null != chooser.resolveActivity(packageManager)) {
                        startActivityForResult(chooser, UPI_PAYMENT);
                    } else {
                        Toast.makeText(PaykunPaymentActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            //  webView.loadUrl(url);
            return false;
        }

        /* @RequiresApi(api = Build.VERSION_CODES.M)
         @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
             Log.e("shouldOverride",url);
             if(url.contains("paykun://checkout")) {
                 view.setVisibility(View.INVISIBLE);
                 StartAnimate();
                 Uri myUri = Uri.parse(url);

                 if(myUri.getQueryParameter("status")==null)
                 {
                     return false;
                 }
                 if(myUri.getQueryParameter("req_id")==null)
                 {
                     return false;
                 }
                 String status = Objects.requireNonNull(myUri.getQueryParameter("status")).trim();
                 String reqId =  Objects.requireNonNull(myUri.getQueryParameter("req_id")).trim();
                // Toast.makeText(getApplicationContext(),status+"-"+reqId,Toast.LENGTH_SHORT).show();
                 if(!TextUtils.isEmpty(status)) {
                     if (status.equalsIgnoreCase("SUCCESS")) {
                        // PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_CANCELLED, reqId, null);
                       //  PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                         //finish();
                        *//* if (isLive.equalsIgnoreCase("false")) {
                            getTransactionDetail(reqId);
                        }*//*
                        getTransactionDetail(reqId);
                    } else  if (status.equalsIgnoreCase("CANCELLED")) {
                        PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_CANCELLED, reqId, null);
                        PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        finish();
                    } else if (status.equalsIgnoreCase("FAILED")) {
                        PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, reqId, null);
                        PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        finish();
                    }
                }
                return false;
            }
            if(url.contains("upi://pay")) {
                Log.e("upi",url);

                Uri myUri = Uri.parse(url);

               *//* *//*
                int n=url.indexOf("&intent=");
                if(n>0)
                {
                    String pcg_nam="";
                    req_id="";
                    if(myUri.getQueryParameter("intent")!=null)
                    {
                        pcg_nam=myUri.getQueryParameter("intent");
                    }
                    if(myUri.getQueryParameter("req_id")!=null)
                    {
                        req_id=myUri.getQueryParameter("req_id");
                    }
                    url=url.substring(0,n);
                    Log.e("url",url);
                    Intent upiPayIntent =new Intent(Intent.ACTION_VIEW);
                    upiPayIntent.setData(Uri.parse(url));
                    Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
                    upiPayIntent.setPackage(pcg_nam);
                    PackageManager packageManager = getPackageManager();
                    if (null != chooser.resolveActivity(packageManager)) {
                        startActivityForResult(chooser, UPI_PAYMENT);
                    } else {
                        Toast.makeText(PaykunPaymentActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
                    }
                }
                return  true;
            }
            view.loadUrl(url);
            return true;
        }
*/

        @Override
        public void onPageFinished(WebView view, final String url) {
             if (url.contains("failed")) {
                 if(!issendstate)
                 {
                     issendstate=true;
                 }else
                 {
                     return;
                 }
                PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_CANCELLED, req_id, null);
                PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                finish();
            } else if (url.contains("cancelledOrder")) {
                 if(!issendstate)
                 {
                     issendstate=true;

                 }else
                 {
                     return;
                 }
                PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, req_id, null);
                PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                finish();
            }
            Log.e("onPageFinished",url);
            if(!url.contains("paykun://checkout")) {
                imgProgress.setVisibility(View.GONE);
                imgProgress.setAnimation(null);
            }
            long finalTime = System.currentTimeMillis() - startTime;
            Log.e(" final time OF WEBVIEW ", " calculate time consuming : " + finalTime);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == UPI_PAYMENT) {
            final JSONObject jsonObject = new JSONObject();
            if (data != null) {
                final Bundle bundle = data.getExtras();
                if (bundle != null) {
                    final Set<String> keys = (Set<String>) bundle.keySet();
                    for (final String key : keys) {
                        try {
                            jsonObject.put(key, bundle.get(key));
                        } catch (JSONException ex) {
                            if(ex.getLocalizedMessage()!=null)
                                Log.e("JSONException",ex.getLocalizedMessage());

                        }
                    }
                }
            }
            Log.e("pollStatus", jsonObject.toString());

            getRequestinfo(req_id,jsonObject);
            //  Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_LONG).show();
               /* Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Set<String> keys = bundle.keySet();

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("IntentDump \n\r");
                    stringBuilder.append("-------------------------------------------------------------\n\r");

                    for (String key : keys) {
                        stringBuilder.append(key).append("=").append(bundle.get(key)).append("\n\r");
                    }

                    stringBuilder.append("-------------------------------------------------------------\n\r");
                    Log.e("UPI Apyment", stringBuilder.toString());
                }*/
        }
    }
/*    public void onSuccess(String transacitonId) {
        if (isLive.equalsIgnoreCase("false")) {
            getTransactionDetail(transacitonId);
        }
        *//*Events.PaymentMessage activityActivityMessageEvent = new Events.PaymentMessage(PaykunHelper.MESSAGE_SUCCESS,transacitonId);
        GlobalBus.getBus().postSticky(activityActivityMessageEvent);
        finish();*//*
    }*/

/*    public void onFailed(String transacitonId) {

        if (isLive.equalsIgnoreCase("false")) {
            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, transacitonId, null);
            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
            finish();
        }
    }*/

    /*public void onPaybuttonClicked(String response){
            try {
                JSONObject object = new JSONObject(response);
                String isShowBackbutton = object.getString("isShowBackBUtton");
                if(isShowBackbutton.equalsIgnoreCase("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toolbar.setVisibility(View.VISIBLE);
                        }
                    });

                }
            }catch (Exception e){
                Log.e(" eeeee "," exceptoin while parsing : "+e.toString());
            }

    }*/
    /*public void onTransactionCancelled(String response){
        // close activity here close webview
        Log.e(""," on transaction called : "+response);
        Events.PaymentMessage activityActivityMessageEvent = new Events.PaymentMessage(PaykunHelper.MESSAGE_FAILED,"0",null);
        GlobalBus.getBus().postSticky(activityActivityMessageEvent);
        finish();

    }*/
/*    public void postMessage(String response) {
        Log.e(" on post message ", " post message string : " + response.toString());
        try {
            JSONObject object = new JSONObject(response.toString());
            String command = object.getString("command");
            if (!TextUtils.isEmpty(command)) {
                if (command.equalsIgnoreCase("showHideButton")) {
                    String isShowBackButton = object.getString("isShowBackBUtton");
                    if (!TextUtils.isEmpty(isShowBackButton) && isShowBackButton.equalsIgnoreCase("true")) {
                        // show back button here from toolbar to go back to webview
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toolbar.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } else if (command.equalsIgnoreCase("transactionStatus")) {
                    String status = object.getString("status");
                    String transactionId = object.getString("transactionId");
                    if (!TextUtils.isEmpty(status) && status.equalsIgnoreCase("SUCCESS")) {
                        getTransactionDetail(transactionId);
                    } else if (!TextUtils.isEmpty(status) && status.equalsIgnoreCase("FAILED")) {
                        if (toolbar != null && toolbar.getVisibility() == View.VISIBLE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toolbar.setVisibility(View.GONE);
                                }
                            });
                        }


                    } else if (!TextUtils.isEmpty(status) && status.equalsIgnoreCase("INVALID_REQUEST")) {
                        PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_INVALID_REQUEST, transactionId, null);
                        PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        finish();
                    }

                } else if (command.equalsIgnoreCase("cancelTransaction")) {
                    // cancel transaction on pressing close button from webview,close activity and webview here
                    String transactionId = object.getString("transactionId");
                    PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, transactionId, null);
                    PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                    finish();
                }
            } else {
                PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, "0", null);
                PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                finish();
            }


        } catch (Exception e) {
            Log.e("", " exception : " + e.toString());
            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, "0", null);
            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
            finish();
        }
    }*/
    public void getRequestinfo(final String reqid,final JSONObject res) {

        if (PaykunApiCall.paykunTransactionrequest.getMerchant_id()!=null) {
            if (PaykunApiCall.paykunTransactionrequest.getAccess_token() != null) {
                JSONObject object = new JSONObject();
                try {


                    object.put("merchant_id",merchantId);
                    object.put("req_id",reqid);
                    object.put("access_token",accessToken);
                    String sts=res.getString("Status");
                    object.put("status",sts);
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    object.put("timestamp", timeStamp);
                    String[] _inputToBeHashed = {
                            "merchant_id",
                            "req_id",
                            "access_token",
                            "status",
                            "timestamp",
                    };
                    String stringForSignature = "";
                    Iterator<?> keys = object.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (Arrays.asList(_inputToBeHashed).contains(key)) {
                            String value = object.get(key).toString().trim();
                            stringForSignature = stringForSignature.concat(value);
                        }
                    }
                    stringForSignature = merchantId +reqid+ accessToken + sts +  timeStamp;
                    String signature2 = generateHashWithHmac256(stringForSignature, timeStamp);
                    String signatureFinal = generateHashWithHmac256(signature2, accessToken);
                    object.put(PaykunHelper.KEY_SIGNATURE, signatureFinal);
                    Log.e("Upi Check Request",object.toString());
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            PaykunHelper.upistatus, object, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Upi Check  Response",response.toString());

                        }
                    }, new Response.ErrorListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressbar();
                            if (error.networkResponse != null) {
                                if (error.networkResponse.data != null) {
                                    try {
                                        String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                        if (!TextUtils.isEmpty(body)) {
                                            JSONObject objectMain = new JSONObject(body);
                                            PaykunHelper.MESSAGE_SERVER_ISSUE = objectMain.getString("message");
                                            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_SERVER_ISSUE, "0", null);
                                            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                                            finish();
                                        } else {
                                            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_SERVER_ISSUE, "0", null);
                                            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                        }
                    });
                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(PaykunHelper.VOLLEY_TIME_OUT,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    addToRequestQueue(jsonObjReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }
    public void getTransactionDetail(final String transactionId) {
        String url = "";
        if (isLive.equalsIgnoreCase("true")) {
            url = PaykunHelper.urlTransactionInfo;
        } else {
            url = PaykunHelper.urlTransactionInfoSandbox;
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url + transactionId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                //Log.e(" success "," transaction detail response : "+response.toString());
                if (response != null) {
                    try {
                        if (response.has("data")) {
                            JSONObject data = response.getJSONObject("data");
                            if (data.has("transaction")) {
                                final JSONObject transactionObject = data.getJSONObject("transaction");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            /* getting order,customer,shipping,billing ojbect from Transaction Object*/
                                            JSONObject objectOrder = transactionObject.getJSONObject("order");
                                            JSONObject objectCustomer = transactionObject.getJSONObject("customer");
                                            JSONObject objectShipping = transactionObject.getJSONObject("shipping");
                                            JSONObject objectBilling = transactionObject.getJSONObject("billing");

                                            /* Create Order class from Order JSONObject */
                                            Order order = new Order();
                                            order.setOrderId(objectOrder.getString("order_id"));
                                            order.setProductName(objectOrder.getString("product_name"));
                                            order.setGrossAmount(Double.parseDouble(objectOrder.getString("gross_amount")));
                                            order.setGatewayFee(objectOrder.getString("gateway_fee"));
                                            order.setTax(objectOrder.getString("tax"));

                                            /* Create Customer class from Customer JSONObject */
                                            Customer customer = new Customer();
                                            customer.setName(objectCustomer.getString("name"));
                                            customer.setEmailId(objectCustomer.getString("email_id"));
                                            customer.setMobileNo(objectCustomer.getString("mobile_no"));

                                            /* Create Shipping class from Shipping JSONObject */
                                            Shipping shipping = new Shipping();
                                            shipping.setAddress(objectShipping.getString("address"));
                                            shipping.setCity(objectShipping.getString("city"));
                                            shipping.setState(objectShipping.getString("state"));
                                            shipping.setCountry(objectShipping.getString("country"));
                                            shipping.setPincode(objectShipping.getString("pincode"));

                                            /* Create Billing class from Billing JSONObject */
                                            Billing billing = new Billing();
                                            billing.setAddress(objectBilling.getString("address"));
                                            billing.setCity(objectBilling.getString("city"));
                                            billing.setState(objectBilling.getString("state"));
                                            billing.setCountry(objectBilling.getString("country"));
                                            billing.setPincode(objectBilling.getString("pincode"));


                                            Transaction transactionMain = new Transaction();

                                            transactionMain.setPaymentId(transactionObject.getString("payment_id"));
                                            transactionMain.setMerchantEmail(transactionObject.getString("merchant_email"));
                                            transactionMain.setMerchantId(transactionObject.getString("merchant_id"));
                                            transactionMain.setStatus(transactionObject.getString("status"));
                                            transactionMain.setStatusFlag(Integer.parseInt(transactionObject.getString("status_flag")));
                                            transactionMain.setPaymentMode(transactionObject.getString("payment_mode"));
                                            transactionMain.setCustomField1(transactionObject.getString("custom_field_1"));
                                            transactionMain.setCustomField2(transactionObject.getString("custom_field_2"));
                                            transactionMain.setCustomField3(transactionObject.getString("custom_field_3"));
                                            transactionMain.setCustomField4(transactionObject.getString("custom_field_4"));
                                            transactionMain.setCustomField5(transactionObject.getString("custom_field_5"));
                                            transactionMain.setDate(transactionObject.getString("date"));

                                            transactionMain.setOrder(order);
                                            transactionMain.setCustomer(customer);
                                            transactionMain.setShipping(shipping);
                                            transactionMain.setBilling(billing);

                                            //     PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_SUCCESS, transactionId, transactionMain);
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    PaykunApiCall.paykunResponseListener.onPaymentSuccess(transactionMain);
                                                }
                                            });


                                            finish();
                                        } catch (JSONException e) {
                                            if(e.getMessage()!=null) {
                                                Log.e("JSONException", e.getMessage());
                                            }
                                            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, transactionId, null);
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                                                }
                                            });
                                            finish();
                                        }
                                    }
                                }).start();
                            } else {
                                PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, transactionId, null);
                                PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                                finish();
                            }
                        } else {
                            PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, transactionId, null);
                            PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                            finish();
                        }
                    } catch (JSONException e) {
                        //e.printStackTrace();
                        PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, transactionId, null);
                        PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                        finish();
                    }

                } else {
                    PaymentMessage activityActivityMessageEvent = new PaymentMessage(PaykunHelper.MESSAGE_FAILED, transactionId, null);
                    PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                    finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(" faill "," transaction detail response : "+error.toString());
                PaymentMessage activityActivityMessageEvent = new PaymentMessage(error.toString(), transactionId, null);
                PaykunApiCall.paykunResponseListener.onPaymentError(activityActivityMessageEvent);
                finish();
            }
        }) {
            /** Passing some request headers* */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //noinspection rawtypes
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MerchantId", merchantId);
                headers.put("AccessToken", accessToken);
                headers.put("PackageName", packageName);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(PaykunHelper.VOLLEY_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(jsonObjReq);
    }

    /*private void Disable_Certificate_Validation_Java_SSL_Connections() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        // Install the all-trusting trust manager
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        if (sc != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @SuppressLint("BadHostnameVerifier")
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }*/

    private String generateHashWithHmac256(String message, String key) {
        String messageDigest = null;
        try {

            byte[] bytes = hmac(key.getBytes(), message.getBytes());
            messageDigest = bytesToHex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageDigest;
    }

    private static byte[] hmac(byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(message);
    }

    /*private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
        imgProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        progressBar.setVisibility(View.INVISIBLE);
        imgProgress.setVisibility(View.GONE);

    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(PaykunPaymentActivity.this);
        }

        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        req.setTag("");
        getRequestQueue().add(req);
    }


}