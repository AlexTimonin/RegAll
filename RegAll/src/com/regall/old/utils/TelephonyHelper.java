package com.regall.old.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

public class TelephonyHelper {

	public static boolean isTelephonySupported(Context context){
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM ? tm.getSimState() == TelephonyManager.SIM_STATE_READY : true; 
		} else {
			return false;
		}
	}
	
}
