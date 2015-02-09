package com.regall.old.network.geocode.model;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Route {

	private Bounds bounds;
	private ArrayList<Leg> legs;
	private Polyline overview_polyline;
	private String summary;
	private ArrayList<String> warnings;
//	private ArrayList<WaypointOrder> waipoint_order;
	private ArrayList<LatLng> polyline_points; //decoded overview_polyline data
	
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	public ArrayList<Leg> getLegs() {
		return legs;
	}
	public void setLegs(ArrayList<Leg> legs) {
		this.legs = legs;
	}
	public Polyline getOverview_polyline() {
		return overview_polyline;
	}
	public void setOverview_polyline(Polyline overview_polyline) {
		this.overview_polyline = overview_polyline;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public ArrayList<String> getWarnings() {
		return warnings;
	}
	public void setWarnings(ArrayList<String> warnings) {
		this.warnings = warnings;
	}
	public ArrayList<LatLng> getPolyline_points() {
		return polyline_points;
	}
	public void setPolyline_points(ArrayList<LatLng> polyline_points) {
		this.polyline_points = polyline_points;
	}
	
}
