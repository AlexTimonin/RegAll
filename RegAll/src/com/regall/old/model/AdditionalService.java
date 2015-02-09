package com.regall.old.model;

import java.util.Collection;

import com.regall.R;

public enum AdditionalService {

	REST_ROOM(1, R.drawable.ic_launcher),
	PAY_VISA(2, R.drawable.ic_launcher),
	FUEL(3, R.drawable.ic_launcher),
	TECH_SUPPORT(4, R.drawable.ic_launcher),
	WIFI(5, R.drawable.ic_launcher),
	FOOD(6, R.drawable.ic_launcher);

	int mCode;
	int mDrawable;

	private AdditionalService(int code, int drawable) {
		this.mCode = code;
		this.mDrawable = drawable;
	}
	
	public static AdditionalService getServiceByServerCode(int serverCode){
		for(AdditionalService service : values()){
			if(service.mCode == serverCode){
				return service;
			}
		}
		
		throw new IllegalArgumentException("No additional service type with code - " + serverCode);
	}
	
	public static String createCodesString(Collection<AdditionalService> additionalServices){
		String delimiter = ",";
		StringBuilder builder = new StringBuilder();
		for(AdditionalService service : additionalServices){
			builder.append(Integer.valueOf(service.mCode).toString()).append(delimiter);
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
}