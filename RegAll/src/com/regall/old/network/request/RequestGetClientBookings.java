package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestGetClientBookings {
	
	@Root(name = "row")
	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "get_client_query";
		
		@Attribute(name = "phone")
		private String mPhone;
		
	}

	@Element(name = "row")
	private Params mParams;
	
	public static RequestGetClientBookings create(String phone){
		Params params = new Params();
		params.mPhone = phone;
		return new RequestGetClientBookings(params);
	}

	private RequestGetClientBookings(Params mParams) {
		this.mParams = mParams;
	}
}
