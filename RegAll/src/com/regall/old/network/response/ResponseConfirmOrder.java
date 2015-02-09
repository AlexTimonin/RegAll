package com.regall.old.network.response;

import org.simpleframework.xml.Element;

public class ResponseConfirmOrder extends BasicResponse {

	@Element(name="num")
	private int mNum;
}
