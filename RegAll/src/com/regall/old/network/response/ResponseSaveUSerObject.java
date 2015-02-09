package com.regall.old.network.response;

import org.simpleframework.xml.Element;

public class ResponseSaveUSerObject extends BasicResponse {

	@Element(name = "id")
	private int mId;

	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}
	
}
