package com.paykun.sdk.logonsquare;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Billing{

	@JsonField(name ="country")
	public Object country;

	@JsonField(name ="pincode")
	public Object pincode;

	@JsonField(name ="address")
	public Object address;

	@JsonField(name ="city")
	public Object city;

	@JsonField(name ="state")
	public Object state;

	public void setCountry(Object country){
		this.country = country;
	}

	public Object getCountry(){
		return country;
	}

	public void setPincode(Object pincode){
		this.pincode = pincode;
	}

	public Object getPincode(){
		return pincode;
	}

	public void setAddress(Object address){
		this.address = address;
	}

	public Object getAddress(){
		return address;
	}

	public void setCity(Object city){
		this.city = city;
	}

	public Object getCity(){
		return city;
	}

	public void setState(Object state){
		this.state = state;
	}

	public Object getState(){
		return state;
	}

	@Override
 	public String toString(){
		return
			"Billing{" +
			"country = '" + country + '\'' +
			",pincode = '" + pincode + '\'' +
			",address = '" + address + '\'' +
			",city = '" + city + '\'' +
			",state = '" + state + '\'' +
			"}";
		}
}