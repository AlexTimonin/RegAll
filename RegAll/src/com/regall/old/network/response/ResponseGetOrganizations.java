package com.regall.old.network.response;

import android.text.TextUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseGetOrganizations extends BasicResponse {

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
	
	@Root(name = "org")
	public static class Organisation implements Serializable {

		private static final long serialVersionUID = -6395388082450583031L;
		
		@Attribute(name = "org_id")
		private String mId;
		
		@Attribute(name = "org_shortname")
		private String mShortTitle;
		
		@Attribute(name = "org_name")
		private String mTitle;
		
		@Attribute(name = "adress")
		private String mAddress;
		
		@Attribute(name = "phone")
		private String mPhone;
		
		@ElementList(name="org_point", inline = true, required = false)
		private List<Point> mOraganisations;

		public String getId() {
			return mId;
		}

		public String getShortTitle() {
			return mShortTitle;
		}

		public String getTitle() {
			return mTitle;
		}

		public String getAddress() {
			return mAddress;
		}

		public String getPhone() {
			return mPhone;
		}

		public List<Point> getOrganisations() {
			return mOraganisations;
		}

        @Override
        public int hashCode() {
            return getId() == null ? 0 : getId().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof Organisation && TextUtils.equals(getId(), ((Organisation) o).getId());
        }

        public boolean isPremium() {
            //TODO waiting for API update. Always true until then
            return true;
        }

    }

	@Root(name = "org_point")
	public static class Point implements Serializable {
		
		private static final long serialVersionUID = -2510493954145294269L;

		@Root(name="point_service")
		public static class ServiceDescription implements Serializable {
			
			private static final long serialVersionUID = -8667365620517481510L;

			@Attribute(name="service_id")
			private int mId; 
			
			@Attribute(name="service_name")
			private String mTitle; 
			
			@Attribute(name="service_description")
			private String mDescription;

			public int getId() {
				return mId;
			}

			public String getTitle() {
				return mTitle;
			}

			public String getDescription() {
				return mDescription;
			}
		}
		
		@Root(name="point_aservice")
		public static class AdditionalServiceDescription implements Serializable {
			
			private static final long serialVersionUID = 3908438171794691820L;

			@Attribute(name="aserv_id")
			private int mId; 
			
			@Attribute(name="aservice_name")
			private String mTitle; 
			
			@Attribute(name="service_description")
			private String mDescription;

			public int getId() {
				return mId;
			}

			public String getTitle() {
				return mTitle;
			}

			public String getDescription() {
				return mDescription;
			}
		}
		
		@Attribute(name="point_id")
		private String mId;

		@Attribute(name="point_name")
		private String mName;

		@Attribute(name="point_adress")
		private String mAddress;

		@Attribute(name="point_phone")
		private String mPhone;

		@Attribute(name="service_time", required = false)
		private String mServiceTime;

		@Attribute(name="service_cost", required = false)
		private int mServiceCost;

		@Attribute(name="f_time", required = false)
		private String mFirstTime;

		@Attribute(name="work_start")
		private String mWorkStart;

		@Attribute(name="work_end")
		private String mWorkEnd;

		@Attribute(name="break_start")
		private String mBreakStart;

		@Attribute(name="break_end")
		private String mBreakEnd;
		
		@Attribute(name="pix_link", required = false)
		private String mPictureUrl;

		@Attribute(name = "latitude")
		private double mLatitude;
		
		@Attribute(name = "longitude")
		private double mLongitude;
		
		@ElementList(name="point_service", inline = true, required = false)
		private ArrayList<ServiceDescription> mServices;
		
		@ElementList(name="point_aservice", inline = true, required = false)
		private ArrayList<AdditionalServiceDescription> mAdditionalServices;

		public String getId() {
			return mId;
		}

		public String getName() {
			return mName;
		}

		public String getAddress() {
			return mAddress;
		}

		public String getPhone() {
			return mPhone;
		}

		public String getServiceTime() {
			return mServiceTime;
		}

		public int getServiceCost() {
			return mServiceCost;
		}

		public String getFirstTime() {
			return mFirstTime;
		}

		public String getWorkStart() {
			return mWorkStart;
		}

		public String getWorkEnd() {
			return mWorkEnd;
		}

		public String getBreakStart() {
			return mBreakStart;
		}

		public String getBreakEnd() {
			return mBreakEnd;
		}

		public String getPictureUrl() {
			return mPictureUrl;
		}

		public ArrayList<ServiceDescription> getServices() {
			return mServices;
		}

		public ArrayList<AdditionalServiceDescription> getAdditionalServices() {
			return mAdditionalServices;
		}

		public double getLatitude() {
			return mLatitude;
		}

		public double getLongitude() {
			return mLongitude;
		}

        @Override
        public int hashCode() {
            return getId() == null ? 0 : getId().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof Point && TextUtils.equals(getId(), ((Point) o).getId());
        }
    }
	
	@Element(name="page", required = false)
	private ResultPage mPage;
	
	@ElementList(name = "org", inline = true, required = false)
	private List<Organisation> mOrganisations;
	
	public ResultPage getPage() {
		return mPage;
	}

	public List<Organisation> getOrganisations() {
		return mOrganisations;
	}
}
