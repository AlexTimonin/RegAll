package com.regall.old.model;

import java.io.Serializable;
import java.util.HashSet;

import android.text.TextUtils;

import com.regall.old.network.geocode.json.Result;
import com.regall.old.network.response.ResponseGetServices.Service;

public class AutowashFilter implements Serializable {

	private static final long serialVersionUID = -126395252203579527L;
	
	private final static String SERVICE_DELIMITER = ",";

	public enum LocationFilter {
		CURRENT_LOCATION,
		ADDRESS
	}

	private LocationFilter mLocationFilterType;
	private Result mGeopointDescription;
	private HashSet<Service> mServicesSet;
	private HashSet<AdditionalService> mAdditionalServices;
	private double mLatitude;
	private double mLongitude;
	
	public AutowashFilter(LocationFilter locationFilterType, HashSet<Service> servicesSet, HashSet<AdditionalService> additionalServices) {
		this.mLocationFilterType = locationFilterType;
		this.mServicesSet = servicesSet;
		this.mAdditionalServices = additionalServices;
	}
	
	public AutowashFilter(LocationFilter locationFilterType, Result geopointDescription, HashSet<Service> servicesSet, HashSet<AdditionalService> additionalServices) {
		this.mLocationFilterType = locationFilterType;
		this.mGeopointDescription = geopointDescription;
		this.mServicesSet = servicesSet;
		this.mAdditionalServices = additionalServices;
	}

	public LocationFilter getLocationFilterType() {
		return mLocationFilterType;
	}

	public Result getGeopointDescription() {
		return mGeopointDescription;
	}

	public void setGeopointDescription(Result mGeopointDescription) {
		this.mGeopointDescription = mGeopointDescription;
	}

	public String getServicesString() {
		return TextUtils.join(SERVICE_DELIMITER, mServicesSet);
	}

	public HashSet<Service> getServicesSet() {
		return mServicesSet;
	}

	public void setLocationFilterType(LocationFilter mLocationFilterType) {
		this.mLocationFilterType = mLocationFilterType;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
	
	public boolean isCurrentLocationInitialised(){
		System.out.println("ZZZ is initialized - " + mLatitude + " " + mLongitude);
		return mLatitude > 0.1 && mLongitude > 0.1;
	}

	public HashSet<AdditionalService> getAdditionalServices() {
		return mAdditionalServices;
	}

	public void setAdditionalServices(HashSet<AdditionalService> mAdditionalServices) {
		this.mAdditionalServices = mAdditionalServices;
	}
	
	public String getAservicesString(){
		return mAdditionalServices != null && !mAdditionalServices.isEmpty() ? AdditionalService.createCodesString(mAdditionalServices) : "";
	}
}
