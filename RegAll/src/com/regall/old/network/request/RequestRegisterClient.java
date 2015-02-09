package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestRegisterClient {
	
	@Root(name = "row")
	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "client_registr";
		
		@Attribute(name = "phone")
		private String mPhone;
		
		@Attribute(name = "pass", required = false)
		private String mPass;
		
		@Attribute(name = "isMobile", required = false)
		private int isMobile = 0;
		
		@Attribute(name = "email", required = false)
		private String mEmail;
		
		@Attribute(name = "fname", required = false)
		private String mFirstName;
		
		@Attribute(name = "lname", required = false)
		private String mLastName;
		
		@Attribute(name = "patronymic", required = false)
		private String mPatronymic;
		
	}
	
	@Element(name = "row")
	private Params mParams;
	
	private RequestRegisterClient(Params params) {
		this.mParams = params;
	}

	public static RequestRegisterClient create(String phone, String password){
		Params params = new Params();
		params.mPhone = phone;
		params.mPass = password;
		
		return new RequestRegisterClient(params);
	}

}
