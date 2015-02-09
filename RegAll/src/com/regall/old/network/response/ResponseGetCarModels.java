package com.regall.old.network.response;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

public class ResponseGetCarModels extends BasicResponse {

	@Root(name="model")
	public static class CarModel {
		@Attribute(name="name")
		private String name;
		
		@Attribute(name="object_id")
		private int objectId;

		public String getName() {
			return name;
		}

		public int getObjectId() {
			return objectId;
		}
	}

	@ElementList(inline = true, name = "mark")
	private List<CarModel> mCarModels;

	public List<CarModel> getmCarModels() {
		return mCarModels;
	}

	public void setmCarModels(List<CarModel> mCarModels) {
		this.mCarModels = mCarModels;
	}
}
