package com.regall.old;

import com.regall.old.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class LocationProviderChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("zzz", "onReceive - providers changed");
		
		String locationMode = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		Logger.logDebug("zzz", "Location mode - " + locationMode);
		Log.i("zzz", "Location mode - " + locationMode);
	}

}
