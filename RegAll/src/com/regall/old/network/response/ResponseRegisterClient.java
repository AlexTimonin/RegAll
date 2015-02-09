package com.regall.old.network.response;

import org.simpleframework.xml.Element;

public class ResponseRegisterClient extends BasicResponse {
	
	public final static int STATUS_OK = 0;
	public final static int STATUS_USER_EXISTS = -1;
	public final static int STATUS_USER_NOT_ACTIVATED = 1;
	
	@Element(name = "id")
	private int mId;

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}
}
