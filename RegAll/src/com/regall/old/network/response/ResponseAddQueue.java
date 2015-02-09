package com.regall.old.network.response;

import org.simpleframework.xml.Element;

public class ResponseAddQueue extends BasicResponse {

	@Element(name = "id")
	private int mId;

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}
}
