package com.paykun.sdk.logonsquare;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Response{

	@JsonField(name ="transaction")
	public Transaction transaction;

	public void setTransaction(Transaction transaction){
		this.transaction = transaction;
	}

	public Transaction getTransaction(){
		return transaction;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"transaction = '" + transaction + '\'' + 
			"}";
		}
}