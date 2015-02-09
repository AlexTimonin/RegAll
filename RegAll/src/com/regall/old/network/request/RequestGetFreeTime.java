package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request_type")
public class RequestGetFreeTime {

	@Root(name = "row")
	public static class Params {
		@Attribute(name = "request_type")
		private String mRequestType = "get_free_time";
		
		@Attribute(name = "object_id")
		private int mObjectId;
		
		@Attribute(name = "service_id")
		private String mServiceId;
		
		@Attribute(name = "point_id")
		private String mPointId;
		
		@Attribute(name = "oper_id")
		private int mOperId = 1;
		
		@Attribute(name = "date")
		private String mDate;
		
		public Params(int objectId, String serviceId, String pointId, String date){
			this.mObjectId = objectId;
			this.mServiceId = serviceId;
			this.mPointId = pointId;
			this.mDate = date;
		}
	}

	@Element(name = "row")
	private Params mParams;
	
	public RequestGetFreeTime(int objectId, String serviceId, String pointId, String date){
		mParams = new Params(objectId, serviceId, pointId, date);
	}

}
