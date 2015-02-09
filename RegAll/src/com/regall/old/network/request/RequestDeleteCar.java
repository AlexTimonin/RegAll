package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.util.Log;

@Root(name = "request_type")
public class RequestDeleteCar {
	
	@Root(name = "row")
	static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "del_user_object";
		
		@Attribute(name = "object_id")
		private int mObjectId;
		
		@Attribute(name = "userid")
		private int mUserId;
		
		@Attribute(name = "sessionid")
		private String mSessionId;
	}
	
	@Element(name = "row")
	private Params mParams;

	public static RequestDeleteCar create(int objectId, int userId, String sessionId){
		Log.e("zzz", "ZZZ " + objectId + " " + userId + " " + sessionId);
		Params params = new Params();
		params.mObjectId = objectId;
		params.mUserId = userId;
		params.mSessionId = sessionId;
		
		return new RequestDeleteCar(params);
	} 
	
	private RequestDeleteCar(Params mParams) {
		this.mParams = mParams;
	}
}
