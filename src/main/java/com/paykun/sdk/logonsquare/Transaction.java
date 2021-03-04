package com.paykun.sdk.logonsquare;

import androidx.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Transaction{

	@JsonField(name ="date")
	public String date;

	@JsonField(name ="merchant_email")
	public String merchantEmail;

	@JsonField(name ="status_flag")
	public int statusFlag;

	@JsonField(name ="payment_mode")
	public String paymentMode;

	@JsonField(name ="custom_field_5")
	public String customField5;

	@JsonField(name ="custom_field_3")
	public String customField3;

	@JsonField(name ="merchant_id")
	public String merchantId;

	@JsonField(name ="custom_field_4")
	public String customField4;

	@JsonField(name ="billing")
	public Billing billing;

	@JsonField(name ="shipping")
	public Shipping shipping;

	@JsonField(name ="custom_field_1")
	public String customField1;

	@JsonField(name ="custom_field_2")
	public String customField2;

	@JsonField(name ="payment_id")
	public String paymentId;

	@JsonField(name ="status")
	public String status;

	@JsonField(name ="order")
	public Order order;

	@JsonField(name ="customer")
	public Customer customer;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setMerchantEmail(String merchantEmail){
		this.merchantEmail = merchantEmail;
	}

	public String getMerchantEmail(){
		return merchantEmail;
	}

	public void setStatusFlag(int statusFlag){
		this.statusFlag = statusFlag;
	}

	public int getStatusFlag(){
		return statusFlag;
	}

	public void setPaymentMode(String paymentMode){
		this.paymentMode = paymentMode;
	}

	public String getPaymentMode(){
		return paymentMode;
	}

	public void setCustomField5(String customField5){
		this.customField5 = customField5;
	}

	public String getCustomField5(){
		return customField5;
	}

	public void setCustomField3(String customField3){
		this.customField3 = customField3;
	}

	public String getCustomField3(){
		return customField3;
	}

	public void setMerchantId(String merchantId){
		this.merchantId = merchantId;
	}

	public String getMerchantId(){
		return merchantId;
	}

	public void setCustomField4(String customField4){
		this.customField4 = customField4;
	}

	public String getCustomField4(){
		return customField4;
	}

	public void setBilling(Billing billing){
		this.billing = billing;
	}

	public Billing getBilling(){
		return billing;
	}

	public void setShipping(Shipping shipping){
		this.shipping = shipping;
	}

	public Shipping getShipping(){
		return shipping;
	}

	public void setCustomField1(String customField1){
		this.customField1 = customField1;
	}

	public String getCustomField1(){
		return customField1;
	}

	public void setCustomField2(String customField2){
		this.customField2 = customField2;
	}

	public String getCustomField2(){
		return customField2;
	}

	public void setPaymentId(String paymentId){
		this.paymentId = paymentId;
	}

	public String getPaymentId(){
		return paymentId;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setOrder(Order order){
		this.order = order;
	}

	public Order getOrder(){
		return order;
	}

	public void setCustomer(Customer customer){
		this.customer = customer;
	}

	public Customer getCustomer(){
		return customer;
	}

	@NonNull
	@Override
 	public String toString(){
		return 
			"Transaction{" + 
			"date = '" + date + '\'' + 
			",merchant_email = '" + merchantEmail + '\'' + 
			",status_flag = '" + statusFlag + '\'' + 
			",payment_mode = '" + paymentMode + '\'' + 
			",custom_field_5 = '" + customField5 + '\'' + 
			",custom_field_3 = '" + customField3 + '\'' + 
			",merchant_id = '" + merchantId + '\'' + 
			",custom_field_4 = '" + customField4 + '\'' + 
			",billing = '" + billing + '\'' + 
			",shipping = '" + shipping + '\'' + 
			",custom_field_1 = '" + customField1 + '\'' + 
			",custom_field_2 = '" + customField2 + '\'' + 
			",payment_id = '" + paymentId + '\'' + 
			",status = '" + status + '\'' + 
			",order = '" + order + '\'' + 
			",customer = '" + customer + '\'' + 
			"}";
		}
}