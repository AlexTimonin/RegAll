package com.regall.old.network.response;

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

public class ResponseGetServices extends BasicResponse {

	@Root(name="services")
	public static class Service implements Serializable {
		
		private static final long serialVersionUID = 5685296814690353170L;

		@Attribute(name="id")
		int mId;
		
		@Attribute(name="name")
		String mName;
		
		@Attribute(name="description")
		String mDescription;

		public int getId() {
			return mId;
		}

		public String getName() {
			return mName;
		}

		public String getDescription() {
			return mDescription;
		}

		@Override
		public String toString() {
			return Integer.valueOf(mId).toString();
		}

		@Override
		public int hashCode() {
			return mId << 2;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj != null && obj instanceof Service){
				Service another = (Service) obj;
				return mId == another.mId;
			} else {
				return false;
			}
		}
	}
	
	@ElementList(name="services", required=false, inline=true)
	private List<Service> mServices;

	public List<Service> getServices() {
		return mServices;
	}
}
