package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestSaveUserObject {
	
	@Root(name = "row")
	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "set_user_object";
		
		@Attribute(name = "phone")
		private String mPhone;
		
		@Attribute(name = "object_id")
		private int mObjectId;
		
	}

	@Element(name = "row")
	private Params mParams;
	
	private RequestSaveUserObject(Params mParams) {
		this.mParams = mParams;
	}

	public static RequestSaveUserObject create(String phone, int objectId){
		Params params = new Params();
		params.mObjectId = objectId;
		params.mPhone = phone;
		return new RequestSaveUserObject(params);
	}
}
