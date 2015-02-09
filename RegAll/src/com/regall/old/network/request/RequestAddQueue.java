package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.regall.old.model.User;

@Root(name = "request_type")
public class RequestAddQueue {

	@Root(name = "row")
	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "add_query";
		
		@Attribute(name = "object_id")
		private int mObjectId;
		
		@Attribute(name = "service_id")
		private String mServiceId;
		
		@Attribute(name = "client_id")
		private int mClientId;
		
		@Attribute(name = "client_phone")
		private String mClientPhone;
		
		@Attribute(name = "point_id")
		private String mAutowashId;
		
		@Attribute(name = "oper_id")
		private int mOperId = 1;
		
		@Attribute(name = "time_want")
		private String mDesiredTime;
		
		@Attribute(name = "ismobile")
		private int mIsMobile = 1;
	}
	
	@Element(name = "row")
	private Params mParams;

	public Params getParams() {
		return mParams;
	}

	public static RequestAddQueue create(int objectId, String services, String autowashId, String time, User user){
		Params params = new Params();
		params.mObjectId = objectId;
		params.mServiceId = services;
		params.mAutowashId = autowashId;
		params.mDesiredTime = time;
		params.mClientId = user.getId();
		params.mClientPhone = user.getPhone();
		
		return new RequestAddQueue(params);
	}

	public RequestAddQueue(Params params) {
		this.mParams = params;
	}
}
