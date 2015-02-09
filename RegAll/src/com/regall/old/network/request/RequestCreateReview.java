package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestCreateReview {
	
	@Root(name = "row")
	public static class Params {
		
		@Attribute(name = "request_type")
		private String mRequestType = "set_stars";
		
		@Attribute(name = "queryid")
		private int mQueryId;
		
		@Attribute(name = "stars")
		private int mStarsCount;
		
	}
	
	@Element(name = "request_type")
	private Params mParams;
	
	public static RequestCreateReview create(int queryId, int stars){
		Params params = new Params();
		params.mQueryId = queryId;
		params.mStarsCount = stars;
		
		return new RequestCreateReview(params);
	}

	public RequestCreateReview(Params params) {
		mParams = params;
	}
}
