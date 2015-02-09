package com.regall.old.network.geocode.model;

import java.util.ArrayList;

public class Leg {

	private GeoParam distance;
	private GeoParam duration;
	private String end_address;
	private GeoLoc end_location;
	private String start_address;
	private GeoLoc start_location;
	private ArrayList<Step> steps;
//	private ArrayList<ViaWaypoint> via_waypoint;
	
	public GeoParam getDistance() {
		return distance;
	}
	public void setDistance(GeoParam distance) {
		this.distance = distance;
	}
	public GeoParam getDuration() {
		return duration;
	}
	public void setDuration(GeoParam duration) {
		this.duration = duration;
	}
	public String getEnd_address() {
		return end_address;
	}
	public void setEnd_address(String end_address) {
		this.end_address = end_address;
	}
	public GeoLoc getEnd_location() {
		return end_location;
	}
	public void setEnd_location(GeoLoc end_location) {
		this.end_location = end_location;
	}
	public String getStart_address() {
		return start_address;
	}
	public void setStart_address(String start_address) {
		this.start_address = start_address;
	}
	public GeoLoc getStart_location() {
		return start_location;
	}
	public void setStart_location(GeoLoc start_location) {
		this.start_location = start_location;
	}
	public ArrayList<Step> getSteps() {
		return steps;
	}
	public void setSteps(ArrayList<Step> steps) {
		this.steps = steps;
	}
	
}
