package com.regall.old.network.geocode.json;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


public class Location implements Serializable {

	private static final long serialVersionUID = -6088358804721941709L;

	@SerializedName("lat")
	private Double mLatitude;
	
	@SerializedName("lng")
	private Double mLongitude;

	public Double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(Double lat) {
		this.mLatitude = lat;
	}

	public Double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(Double lng) {
		this.mLongitude = lng;
	}

}
