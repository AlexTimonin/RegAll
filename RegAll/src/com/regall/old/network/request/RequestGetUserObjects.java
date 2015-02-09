package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestGetUserObjects {

	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "get_user_object";
		
		@Attribute(name = "phone")
		private String mPhone;
		
		@Attribute(name = "isarc")
		private int mGzip = 0;
	}
	
	@Element(name = "row")
	private Params mParams;
	
	public static RequestGetUserObjects create(String phone){
		Params params = new Params();
		params.mPhone = phone;
		
		return new RequestGetUserObjects(params);
	}

	private RequestGetUserObjects(Params mParams) {
		this.mParams = mParams;
	}
}
