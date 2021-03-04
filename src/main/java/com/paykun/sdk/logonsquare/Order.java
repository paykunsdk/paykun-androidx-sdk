package com.paykun.sdk.logonsquare;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Order{

	@JsonField(name ="gateway_fee")
	public Object gatewayFee;

	@JsonField(name ="tax")
	public Object tax;

	@JsonField(name ="gross_amount")
	public double grossAmount;

	@JsonField(name ="order_id")
	public String orderId;

	@JsonField(name ="product_name")
	public String productName;

	public void setGatewayFee(Object gatewayFee){
		this.gatewayFee = gatewayFee;
	}

	public Object getGatewayFee(){
		return gatewayFee;
	}

	public void setTax(Object tax){
		this.tax = tax;
	}

	public Object getTax(){
		return tax;
	}

	public void setGrossAmount(double grossAmount){
		this.grossAmount = grossAmount;
	}

	public double getGrossAmount(){
		return grossAmount;
	}

	public void setOrderId(String orderId){
		this.orderId = orderId;
	}

	public String getOrderId(){
		return orderId;
	}

	public void setProductName(String productName){
		this.productName = productName;
	}

	public String getProductName(){
		return productName;
	}

	@Override
 	public String toString(){
		return 
			"Order{" + 
			"gateway_fee = '" + gatewayFee + '\'' + 
			",tax = '" + tax + '\'' + 
			",gross_amount = '" + grossAmount + '\'' + 
			",order_id = '" + orderId + '\'' + 
			",product_name = '" + productName + '\'' + 
			"}";
		}
}