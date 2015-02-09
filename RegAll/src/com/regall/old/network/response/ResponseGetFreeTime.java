package com.regall.old.network.response;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

public class ResponseGetFreeTime extends BasicResponse {
	
	@Root(name = "time")
	public static class Time {
		
		@Attribute(name = "value")
		private String mValue;

		public String getValue() {
			return mValue;
		}
	}
	
	@Element(name = "service-time", required = false)
	private String mServiceTime;
	
	@Element(name = "service-cost", required = false)
	private int mServiceCost;
	
	@ElementList(name = "time", required = false, inline = true)
	private List<Time> mAvailableTime;

	public String getServiceTime() {
		return mServiceTime;
	}

	public int getServiceCost() {
		return mServiceCost;
	}

	public List<Time> getAvailableTime() {
		return mAvailableTime;
	}
}
