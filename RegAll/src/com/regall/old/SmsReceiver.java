package com.regall.old;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.regall.R;
import com.regall.old.model.User;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestConfirmOrder;
import com.regall.old.network.request.RequestConfirmRegistration;
import com.regall.old.network.response.ResponseConfirmOrder;
import com.regall.old.network.response.ResponseRegisterClient;

public class SmsReceiver extends BroadcastReceiver {
	
	private final static String tag = SmsReceiver.class.getSimpleName();
	
	public final static String ACTION_BOOKING_CONFIRMED = "com.regall.BOOKING_CONFIRMED";
	public final static String ACTION_REGISTRATION_COMPLETE = "com.regall.REGISTRATION_COMPLETE";
	public final static String EXTRA_USER_PROFILE = "com.regall.extra.USER_PROFILE";
	
	private final static String SMS_REGISTRATION_REGEXP = "smscode: (\\d{6}).*";
	private final static String SMS_CONFIRMATION_REGEXP = "smscode: (\\d{4}).*";
	
	public final static String PREFERENCES_REGISTRATION = "reg_prefs";
	public final static String PREFERENCES_REGISTRATION_PHONE = "reg_prefs_phone";
	
	public final static String PREFERENCES_BOOKING = "reg_prefs";
	public final static String PREFERENCES_BOOKING_QUEUE_ID = "reg_prefs_phone";
	
	private Context mContext;
	private String mPhoneToRegister;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		Bundle extras = intent.getExtras();
		if(extras != null){
			Object[] pdus = (Object[]) extras.get("pdus");
			for(int i = 0, max = pdus.length; i < max; i++){
				SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
				
				String text = message.getMessageBody().replaceAll("\n", " ");
				
				if(text.matches(SMS_REGISTRATION_REGEXP)){
					if(checkForDelayedRegistration(context)){
						proceedDelayedRegistration(context, text);
					}
				} else if(text.matches(SMS_CONFIRMATION_REGEXP)){
					if(checkForDelayedConfirmation()){
						proceedDelayedConfirmation(context, text);
					}
				}
			}
		}		
	}
	
	private boolean checkForDelayedRegistration(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_REGISTRATION, Context.MODE_PRIVATE);
		String delayedPhone = prefs.getString(PREFERENCES_REGISTRATION_PHONE, null);
		return delayedPhone != null;
	}
	
	private void proceedDelayedRegistration(Context context, String sms){ 
		Pattern p = Pattern.compile(SMS_REGISTRATION_REGEXP);
		Matcher matcher = p.matcher(sms);
		if(matcher.find()){
			String smsCode = matcher.group(1);
			
			SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_REGISTRATION, Context.MODE_PRIVATE);
			mPhoneToRegister = prefs.getString(PREFERENCES_REGISTRATION_PHONE, null);
			
			RequestConfirmRegistration request = RequestConfirmRegistration.create(mPhoneToRegister, smsCode);
			API api = new API(context.getString(R.string.server_url));
			api.confirmRegistration(request, mRegistrationConfirmCallback);
		} else {
			Toast.makeText(mContext, R.string.message_illegal_sms_code_format, Toast.LENGTH_LONG).show();
		}
	}
	
	private Callback<ResponseRegisterClient> mRegistrationConfirmCallback = new Callback<ResponseRegisterClient>() {

		@Override
		public void success(Object object) {
			ResponseRegisterClient response = (ResponseRegisterClient) object;
			if (response.isSuccess()) {
				Toast.makeText(mContext, R.string.message_registration_confirmed, Toast.LENGTH_LONG).show();
				
				User user = saveUserProfile(response);
				sendBroadcastUserRegistered(user);
				
				mContext.getSharedPreferences(PREFERENCES_REGISTRATION, Context.MODE_PRIVATE).edit().clear().apply();
			} else {
				Toast.makeText(mContext, String.valueOf(response.getStatusDetail()), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void failure(Exception e) {
			e.printStackTrace();
			Toast.makeText(mContext, String.valueOf(e.getMessage()), Toast.LENGTH_LONG).show();
		}
	};
	
	private User saveUserProfile(ResponseRegisterClient response) {
		int id = response.getId();
		User user = new User(id, mPhoneToRegister, "", "");
		user.save(mContext);
		return user;
	}

	private void sendBroadcastUserRegistered(User user) {
		Intent intent = new Intent(ACTION_REGISTRATION_COMPLETE);
		intent.putExtra(EXTRA_USER_PROFILE, user);
		mContext.sendBroadcast(intent);
	}
	
	private boolean checkForDelayedConfirmation(){
		return mContext.getSharedPreferences(PREFERENCES_BOOKING, Context.MODE_PRIVATE).getInt(PREFERENCES_BOOKING_QUEUE_ID, -1) != -1;
	}
	
	private void proceedDelayedConfirmation(Context context, String sms){
		Pattern p = Pattern.compile(SMS_CONFIRMATION_REGEXP);
		Matcher matcher = p.matcher(sms);
		if(matcher.find()){
			String smsCode = matcher.group(1);
			
			SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_BOOKING, Context.MODE_PRIVATE);
			int queueId = prefs.getInt(PREFERENCES_BOOKING_QUEUE_ID, -1);
			
			RequestConfirmOrder request = new RequestConfirmOrder(queueId, smsCode);
			API api = new API(context.getString(R.string.server_url));
			api.confirmQuery(request, mConfirmCallback);
		} else {
			Toast.makeText(mContext, R.string.message_illegal_sms_code_format, Toast.LENGTH_LONG).show();
		}
	}

	private Callback<ResponseConfirmOrder> mConfirmCallback = new Callback<ResponseConfirmOrder>() {
		
		@Override
		public void success(Object object) {
			showResultNotification(R.string.dialog_title_success, mContext.getString(R.string.notification_success));
			mContext.getSharedPreferences(PREFERENCES_BOOKING, Context.MODE_PRIVATE).edit().clear().apply();
			sendConfirmationBroadcast();
		}
		
		@Override
		public void failure(Exception e) {
			showResultNotification(R.string.dialog_title_error, e.getMessage());
		}
		
		private void showResultNotification(int titleId, String message){
			NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle(mContext.getString(titleId));
			builder.setContentText(message);
			builder.setAutoCancel(true);
			
			NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(0, builder.build());
		}
	};
	
	private void sendConfirmationBroadcast(){
		Intent intent = new Intent(ACTION_BOOKING_CONFIRMED);
		mContext.sendBroadcast(intent);
	}
}
