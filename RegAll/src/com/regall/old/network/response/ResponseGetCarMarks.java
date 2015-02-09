package com.regall.old.network.response;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="response")
public class ResponseGetCarMarks extends BasicResponse {
	
	@Root(name="mark")
	public static class CarMark {
		@Attribute(name="name")
		private String name;

		public String getName() {
			return name;
		}
	}

	@ElementList(inline = true, name = "mark")
	private List<CarMark> mCarMarks;

	public List<CarMark> getmCarMarks() {
		return mCarMarks;
	}

	public void setmCarMarks(List<CarMark> mCarMarks) {
		this.mCarMarks = mCarMarks;
	}
}
