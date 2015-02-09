package com.regall.old.network.response;

import org.simpleframework.xml.Element;

public class ResponseLogin extends BasicResponse {
	
	@Element(name = "user_id")
	private int mUserId;
	
	@Element(name = "session", required = false)
	private String mSession;

	public int getUserId() {
		return mUserId;
	}

	public String getSession() {
		return mSession;
	}
}
