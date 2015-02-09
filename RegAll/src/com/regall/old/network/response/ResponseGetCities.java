package com.regall.old.network.response;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

public class ResponseGetCities extends BasicResponse {
	
	@Root(name = "city")
	public static class City {
		
		@Attribute(name = "name")
		String mTitle;

		public String getTitle() {
			return mTitle;
		}

		public void setTitle(String mTitle) {
			this.mTitle = mTitle;
		}
		
		public City() {}

		public City(String mTitle) {
			this.mTitle = mTitle;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			return ((City)obj).getTitle().equals(mTitle);
		}
	}

	@ElementList(name = "city", inline = true)
	List<City> mCities;

	public List<City> getCities() {
		return mCities;
	}

	public void setCities(List<City> mCities) {
		this.mCities = mCities;
	}
}
