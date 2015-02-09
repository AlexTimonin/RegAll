package com.regall.old.network.response;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

public class ResponseGetClientBookings extends BasicResponse {
	
	private final static int STATUS_IN_QUEUE = 0;
	private final static int STATUS_NOT_CONFIRMED = -1;
	
	@Root(name = "page")
	public static class ResultPage {
		@Attribute(name = "all")
		int mTotalPages;

		@Attribute(name = "num")
		int mPageNumber;

		public int getTotalPages() {
			return mTotalPages;
		}

		public int getPageNumber() {
			return mPageNumber;
		}
		
		public boolean isLastPage(){
			return mPageNumber == mTotalPages;
		}
	}

	@Root(name = "service")
	public static class BookedService {
		
		@Attribute(name = "serv_id")
		private int mServiceId;
		
		@Attribute(name = "service_name")
		private String mServiceTitle;

		public int getServiceId() {
			return mServiceId;
		}

		public String getServiceTitle() {
			return mServiceTitle;
		}
	}
	
	@Root(name = "equery")
	public static class BookingRecord {
		
		@Attribute(name = "client_id")
		private int mClientId;
		
		@Attribute(name = "phone")
		private String mPhone;
		
		@Attribute(name = "query_id")
		private int mBookingId;
		
		@Attribute(name = "equery_num")
		private int mBookingNum;
		
		@Attribute(name = "time_start")
		private String mTimeStart;
		
		@Attribute(name = "time_end")
		private String mTimeEnd;
		
		@Attribute(name = "stars")
		private int mStars;
		
		@Attribute(name = "point_name")
		private String mPointName;
		
		@Attribute(name = "point_adress")
		private String mPointAddress;
		
		@Attribute(name = "point_phone")
		private String mPointPhone;
		
		@Attribute(name = "latitude")
		private double mLatitude;
		
		@Attribute(name = "longitude")
		private double mLongitude;
		
		@Attribute(name = "objectid")
		private int mObjectId; 
		
		@Attribute(name = "status_code")
		private int mStatusCode;
		
		@Attribute(name = "status_text")
		private String mStatusText;
		
		@ElementList(name = "service", inline = true)
		private List<BookedService> mBookedServices;

		public int getClientId() {
			return mClientId;
		}

		public String getPhone() {
			return mPhone;
		}

		public int getBookingId() {
			return mBookingId;
		}

		public int getBookingNum() {
			return mBookingNum;
		}

		public String getTimeStart() {
			return mTimeStart;
		}

		public String getTimeEnd() {
			return mTimeEnd;
		}

		public int getStars() {
			return mStars;
		}

		public String getPointName() {
			return mPointName;
		}

		public String getPointAddress() {
			return mPointAddress;
		}

		public String getPointPhone() {
			return mPointPhone;
		}

		public double getLatitude() {
			return mLatitude;
		}

		public double getLongitude() {
			return mLongitude;
		}

		public int getObjectId() {
			return mObjectId;
		}

		public int getStatusCode() {
			return mStatusCode;
		}

		public String getStatusText() {
			return mStatusText;
		}

		public List<BookedService> getBookedServices() {
			return mBookedServices;
		}
		
		public boolean isInQueue(){
			return mStatusCode == STATUS_IN_QUEUE;
		}
		
		public boolean isWaitingConfirmation(){
			return mStatusCode == STATUS_NOT_CONFIRMED;
		}
	}
	
	@Element(name="page", required = false)
	private ResultPage mPage;
	
	@ElementList(name = "equery", inline = true)
	private List<BookingRecord> mBookings = new ArrayList<BookingRecord>();

	public List<BookingRecord> getBookings() {
		return mBookings;
	}

	public ResultPage getmPage() {
		return mPage;
	}
	
}
