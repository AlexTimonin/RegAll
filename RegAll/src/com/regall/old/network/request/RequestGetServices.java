package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestGetServices {

	@Root(name = "row")
	public static class Params {
		@Attribute(name = "request_type")
		private String mRequestType = "get_services";
	}

	@Element(name = "row")
	private Params mParams = new Params();

}
