package com.regall.old.network.geocode.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AddressComponent {

	@SerializedName("long_name")
	private String mLongName;
	
	@SerializedName("short_name")
	private String mShortName;
	
	@SerializedName("types")
	private List<String> mTypes = new ArrayList<String>();

	public String getLongName() {
		return mLongName;
	}

	public void setLongName(String longName) {
		this.mLongName = longName;
	}

	public String getShortName() {
		return mShortName;
	}

	public void setShortName(String shortName) {
		this.mShortName = shortName;
	}

	public List<String> getTypes() {
		return mTypes;
	}

	public void setTypes(List<String> types) {
		this.mTypes = types;
	}

}
