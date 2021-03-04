package com.paykun.sdk.helper;


import com.paykun.sdk.PaymentMessage;
import com.paykun.sdk.logonsquare.Transaction;

public interface PaykunResponseListener {


    void onPaymentSuccess(Transaction paymentMessage);

    void onPaymentError(PaymentMessage paymentMessage);

}
