package com.paykun.sdk.logonsquare;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Customer{

	@JsonField(name ="email_id")
	public String emailId;

	@JsonField(name ="name")
	public String name;

	@JsonField(name ="mobile_no")
	public String mobileNo;

	public void setEmailId(String emailId){
		this.emailId = emailId;
	}

	public String getEmailId(){
		return emailId;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setMobileNo(String mobileNo){
		this.mobileNo = mobileNo;
	}

	public String getMobileNo(){
		return mobileNo;
	}

	@Override
 	public String toString(){
		return 
			"Customer{" + 
			"email_id = '" + emailId + '\'' + 
			",name = '" + name + '\'' + 
			",mobile_no = '" + mobileNo + '\'' + 
			"}";
		}
}