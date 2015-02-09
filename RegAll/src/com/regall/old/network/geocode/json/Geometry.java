package com.regall.old.network.geocode.json;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


public class Geometry implements Serializable {

	private static final long serialVersionUID = -7096852989228697146L;

	@SerializedName("bounds")
	private Viewport mBounds;
	
	@SerializedName("location")
	private Location mLocation;
	
	@SerializedName("location_type")
	private String mLocationType;
	
	@SerializedName("viewport")
	private Viewport mViewport;

	public Viewport getBounds() {
		return mBounds;
	}

	public void setBounds(Viewport bounds) {
		this.mBounds = bounds;
	}

	public Location getLocation() {
		return mLocation;
	}

	public void setLocation(Location location) {
		this.mLocation = location;
	}

	public String getLocationType() {
		return mLocationType;
	}

	public void setLocationType(String location_type) {
		this.mLocationType = location_type;
	}

	public Viewport getViewport() {
		return mViewport;
	}

	public void setViewport(Viewport viewport) {
		this.mViewport = viewport;
	}
}
