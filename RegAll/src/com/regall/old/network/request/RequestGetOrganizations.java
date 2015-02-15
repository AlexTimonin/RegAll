package com.regall.old.network.request;

import android.text.TextUtils;
import android.util.Log;
import com.regall.old.model.AutowashFilter;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

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

        @Attribute(name="finde_text")
        private String searchKey;

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

        if (TextUtils.isEmpty(filter.getSearchKey())) {
            params.searchKey = "";
        } else {
            params.searchKey = filter.getSearchKey().trim();
        }
        params.mLatitude = filter.getLatitude();
        params.mLongitude = filter.getLongitude();

		Log.e("zzz", "lat - " + params.mLatitude + ", lng - " + params.mLongitude);
		return new RequestGetOrganizations(params);
	}

	private RequestGetOrganizations(Params mParams) {
		this.mParams = mParams;
	}
}
