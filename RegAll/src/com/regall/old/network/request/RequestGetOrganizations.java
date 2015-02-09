package com.regall.old.network.request;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.util.Log;

import com.regall.old.model.AutowashFilter;
import com.regall.old.model.AutowashFilter.LocationFilter;
import com.regall.old.network.geocode.json.Location;

@Root(name="request_type")
public class RequestGetOrganizations {
	
	private final static int GPS_DELTA = 5000;

	@Root(name="row")
	public static class Params {
		
		@Attribute(name="request_type")
		private String mRequestType = "get_org_list";
		
		@Attribute(name="long", required = false)
		private double mLongitude;
		
		@Attribute(name="lat", required = false)
		private double mLatitude;
		
		@Attribute(name="gps_delta")
		private int mGpsDelta;
		
		@Attribute(name="serv_in")
		private String mServicesFilter;
		
		@Attribute(name="aserv_in")
		private String mAdditionalServicesFilter;
		
		@Attribute(name="object_id")
		private int mObjectId;
		
		@Attribute(name="city", required = false)
		private String mCityName;
		
		@Attribute(name="page")
		private int mPage;
		
		@Attribute(name="isarc")
		private int mGzip = 0;

	}
	
	@Element(name="row")
	private Params mParams;
	
	public static RequestGetOrganizations byFilter(AutowashFilter filter, int objectId, int page){
		Params params = new Params();
		
		params.mGpsDelta = GPS_DELTA;
		params.mObjectId = objectId;
		params.mPage = page;
		
		params.mServicesFilter = filter.getServicesString();
		params.mAdditionalServicesFilter = filter.getAservicesString();
		
		if(filter.getLocationFilterType().equals(LocationFilter.ADDRESS)){
			Location addressLocation = filter.getGeopointDescription().getGeometry().getLocation();
			params.mLatitude = addressLocation.getLatitude();
			params.mLongitude = addressLocation.getLongitude();
		} else {
			params.mLatitude = filter.getLatitude();
			params.mLongitude = filter.getLongitude();
		}
		
		Log.e("zzz", "lat - " + params.mLatitude + ", lng - " + params.mLongitude);
		return new RequestGetOrganizations(params);
	}

	private RequestGetOrganizations(Params mParams) {
		this.mParams = mParams;
	}
}
