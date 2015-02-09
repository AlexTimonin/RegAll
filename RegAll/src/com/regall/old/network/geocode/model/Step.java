package com.regall.old.network.geocode.model;

public class Step {

	private GeoParam distance;
	private GeoParam duration;
	private GeoLoc end_location;
	private String html_instructions;
	private Polyline polyline;
	private GeoLoc start_location;
	private String travel_mode;
	
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
	public GeoLoc getEnd_location() {
		return end_location;
	}
	public void setEnd_location(GeoLoc end_location) {
		this.end_location = end_location;
	}
	public String getHtml_instructions() {
		return html_instructions;
	}
	public void setHtml_instructions(String html_instructions) {
		this.html_instructions = html_instructions;
	}
	public Polyline getPolyline() {
		return polyline;
	}
	public void setPolyline(Polyline polyline) {
		this.polyline = polyline;
	}
	public GeoLoc getStart_location() {
		return start_location;
	}
	public void setStart_location(GeoLoc start_location) {
		this.start_location = start_location;
	}
	public String getTravel_mode() {
		return travel_mode;
	}
	public void setTravel_mode(String travel_mode) {
		this.travel_mode = travel_mode;
	}
	
}
