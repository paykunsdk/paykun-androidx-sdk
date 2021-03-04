package com.paykun.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaykunTransaction {
   private String merchant_id=null;
    private String access_token=null;
    private String currency="INR";
   private   String customer_name=null;
    private  String customer_email=null;
    private  String customer_phone=null;
    private  String product_name=null;
    private  String order_no=null;
    private  String theme_color="";
    private  String theme_logo="";
    private  HashMap<PaymentTypes, PaymentMethods> payment_methods = new HashMap<>();
    private  String amount=null;
    private boolean isLive=true;
   public  PaykunTransaction(String _merchant_id,String _access_token,boolean _isLive)
   {
       merchant_id=_merchant_id;
       access_token=_access_token;
       isLive=_isLive;

   }
   public void setCurrency(String _currency)
   {
       currency=_currency;
   }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public void setTheme_color(String theme_color) {
        this.theme_color = theme_color;
    }

    public void setTheme_logo(String theme_logo) {
        this.theme_logo = theme_logo;
    }

    public void setPayment_methods(HashMap<PaymentTypes, PaymentMethods> payment_methods) {
        this.payment_methods = payment_methods;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setLive(boolean live) {
        isLive = live;
    }


    public String getMerchant_id() {
        return merchant_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getOrder_no() {
        return order_no;
    }

    public String getTheme_color() {
        return theme_color;
    }

    public String getTheme_logo() {
        return theme_logo;
    }

    public HashMap<PaymentTypes, PaymentMethods> getPayment_methods() {
        return payment_methods;
    }

    public String getAmount() {
        return amount;
    }

    public boolean isLive() {
        return isLive;
    }

}
