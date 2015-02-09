package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestGetCarModels {

	@Root(name = "row")
	public static class Params {
		@Attribute(name = "request_type")
		private String mRequestType = "get_cars_model";

		@Attribute(name = "mark")
		private String mMarkTitle;

		public Params(String mMarkTitle) {
			this.mMarkTitle = mMarkTitle;
		}
	}

	@Element(name = "row")
	private Params mParams;

	public RequestGetCarModels(String markTitle) {
		mParams = new Params(markTitle);
	}
}
