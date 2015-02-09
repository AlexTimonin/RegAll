package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestConfirmRegistration {

	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "client_final_registr";
		
		@Attribute(name = "phone")
		private String mPhone;
		
		@Attribute(name = "sms_code")
		private String mSmsCode;

	}
	
	@Element(name = "row")
	private Params mParams;
	
	public static RequestConfirmRegistration create(String phone, String smsCode){
		Params params = new Params();
		params.mPhone = phone;
		params.mSmsCode = smsCode;
		
		return new RequestConfirmRegistration(params);
	}

	private RequestConfirmRegistration(Params mParams) {
		this.mParams = mParams;
	}
}
