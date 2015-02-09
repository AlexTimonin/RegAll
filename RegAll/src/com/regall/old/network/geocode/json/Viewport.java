package com.regall.old.network.geocode.json;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


public class Viewport implements Serializable {

	private static final long serialVersionUID = 7345094302653502683L;

	@SerializedName("northeast")
	private Location northeast;
	
	@SerializedName("southwest")
	private Location southwest;

	public Location getNortheast() {
		return northeast;
	}

	public void setNortheast(Location northeast) {
		this.northeast = northeast;
	}

	public Location getSouthwest() {
		return southwest;
	}

	public void setSouthwest(Location southwest) {
		this.southwest = southwest;
	}
}