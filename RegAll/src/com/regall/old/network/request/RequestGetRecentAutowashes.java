package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestGetRecentAutowashes {

	@Root(name = "row")
	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "get_last_org";
		
		@Attribute(name = "row")
		private String mPhone;
		
		@Attribute(name = "date_start")
		private String mDateStart;
		
		@Attribute(name = "date_end")
		private String mDateEnd;
		
	} 

	@Element(name = "row")
	private Params mParams;

	public static RequestGetRecentAutowashes create(String dateStart, String dateEnd, String phone){
		Params params = new Params();
		params.mPhone = phone;
		params.mDateStart = dateStart;
		params.mDateEnd = dateEnd;
		
		return new RequestGetRecentAutowashes(params);
	}

	private RequestGetRecentAutowashes(Params mParams) {
		this.mParams = mParams;
	}
}
