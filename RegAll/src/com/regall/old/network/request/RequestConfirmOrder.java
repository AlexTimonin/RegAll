package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestConfirmOrder {

	@Root(name = "row")
	public static class Params {
		@Attribute(name = "request_type")
		private String mRequestType = "confirm_query";
		
		@Attribute(name = "equery_id")
		private int mEqueueId;
		
		@Attribute(name = "smscode")
		private String mSmsCode;

		public Params(int equeueId, String smsCode) {
			this.mEqueueId = equeueId;
			this.mSmsCode = smsCode;
		}
	}

	@Element(name = "row")
	private Params mParams;
	
	public RequestConfirmOrder(int equeueId, String smsCode){
		mParams = new Params(equeueId, smsCode);
	}

}
