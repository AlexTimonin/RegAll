package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestCancelOrder {

	@Root(name = "row")
	public static class Params {
		@Attribute(name = "request_type")
		private String mRequestType = "cancel_query";
		
		@Attribute(name = "query_id")
		private int mEqueueId;
					
		public Params(int equeueId){
			mEqueueId = equeueId;
		}
	}

	@Element(name = "row")
	private Params mParams;
	
	public RequestCancelOrder(int equeueId){
		mParams = new Params(equeueId);
	}

}
