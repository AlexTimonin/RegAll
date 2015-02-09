package com.regall.old.utils;

public class DistanceCalc {

	public static double getDistanceKm(double lat1, double lon1, double lat2, double lon2){
		double latRad1 = lat1 * Math.PI / 180;
		double lonRad1 = lon1 * Math.PI / 180;
		
		double latRad2 = lat2 * Math.PI / 180;
		double lonRad2 = lon2 * Math.PI / 180;
		
		double cosLat1 = Math.cos(latRad1);
		double cosLat2 = Math.cos(latRad2);
		double sinLat1 = Math.sin(latRad1);
		double sinLat2 = Math.sin(latRad2);
		
		double delta = Math.abs(lonRad2 - lonRad1);
		double cosDelta = Math.cos(delta);
		double sinDelta = Math.sin(delta);
		
		double y = Math.sqrt(Math.pow(cosLat2 * sinDelta, 2) + Math.pow(cosLat1 * sinLat2 - sinLat1 * cosLat2 * cosDelta, 2));
		double x = sinLat1 * sinLat2 + cosLat1 * cosLat2 * cosDelta;
		
		double ad = Math.atan2(y, x);
		
		return (ad * 6372795)/1000;
	}
	
}
