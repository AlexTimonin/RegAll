package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestLogin {

	@Root(name = "row")
	static class Params {

		@Attribute(name = "request_type")
		String mRequestType = "user_login";
		
		@Attribute(name = "phone")
		String mPhone;
		
		@Attribute(name = "pass")
		String mPass;
		
	}
	
	@Element(name = "row")
	Params mParams;

	public static RequestLogin create(String phone, String pass){
		Params params = new Params();
		params.mPass = pass;
		params.mPhone = phone;
		
		return new RequestLogin(params);
	}
	
	private RequestLogin(Params mParams) {
		this.mParams = mParams;
	}
}
