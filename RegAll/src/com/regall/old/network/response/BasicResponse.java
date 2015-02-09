package com.regall.old.network.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="response")
public class BasicResponse {

	@Element(name="status-code")
	protected int mStatusCode;
	
	@Element(name="status-detail")
	protected String mStatusDetail;
	
	public boolean isSuccess(){
		return mStatusCode == 0;
	}

	public String getStatusDetail() {
		return mStatusDetail;
	}

	public int getStatusCode() {
		return mStatusCode;
	}
}
