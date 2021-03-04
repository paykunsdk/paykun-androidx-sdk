package com.paykun.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.paykun.sdk.helper.PaykunResponseListener;

import org.json.JSONObject;

import java.io.Serializable;


public class PaykunApiCall {
    public  static PaykunResponseListener paykunResponseListener;
    public  static PaykunTransaction paykunTransactionrequest;
    public static class Builder implements Serializable {
        @SuppressLint("StaticFieldLeak")
        public static transient Activity context = null;
        public Builder(@NonNull Activity activity) throws Exception {
            context = activity;
            if (activity instanceof PaykunResponseListener) {
                try {
                    paykunResponseListener= ((PaykunResponseListener)activity);
                }
                catch (Exception ex) {
                    throw new Exception("Please Implement PaykunResponseListener");
                }
            }
        }
        protected void startPayment(PaykunTransaction paykunTransaction) {
            paykunTransactionrequest=paykunTransaction;
            Intent intent = new Intent(context, PaykunPaymentActivity.class);
            context.startActivity(intent);
        }
        public void sendJsonObject(@NonNull PaykunTransaction jsonObject) {
            startPayment(jsonObject);
        }
    }
}
