package com.regall.old.model;

import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;

public class User implements Serializable {
	
	private static final long serialVersionUID = 8375150959779430855L;

	public static String PHONE;
	
	private final static int VALUE_DOESNT_EXIST = -1;
	
	private final static String PREFERENCES_NAME = "preferences";
	
	private final static String FIELD_ID = "id";
	private final static String FIELD_PHONE = "phone";
	private final static String FIELD_EMAIL = "email";
	private final static String FIELD_SESSION_ID = "session_id";
	private final static String FIELD_CITY = "city";
	
	public static User fromPreferences(Context context){
		SharedPreferences prefs = getPreferences(context);
		
		int userId = prefs.getInt(FIELD_ID, VALUE_DOESNT_EXIST);
		String userPhone = prefs.getString(FIELD_PHONE, "");
		String userEmail = prefs.getString(FIELD_EMAIL, "");
		String sessionId = prefs.getString(FIELD_SESSION_ID, "");
		String city = prefs.getString(FIELD_CITY, "");
		
		PHONE = userPhone;
		
		return userId != VALUE_DOESNT_EXIST ? new User(userId, userPhone, userEmail, sessionId, city) : null;
	}
	
	private int mId;
	private String mPhone;
	private String mEmail;
	private String mSessionId;
	private String mCity;
	
	public User(int mId, String mPhone, String mEmail, String mSessionId, String mCity) {
		this.mId = mId;
		this.mPhone = mPhone;
		this.mEmail = mEmail;
		this.mSessionId = mSessionId;
		this.mCity = mCity;
	}

	public User(int id, String phone, String email, String sessionId) {
		mId = id;
		mPhone = phone;
		mEmail = email;
		mSessionId = sessionId;
	}

	public int getId() {
		return mId;
	}
	
	public String getPhone() {
		return mPhone;
	}
	
	public String getEmail() {
		return mEmail;
	}

	public void setPhone(String mPhone) {
		this.mPhone = mPhone;
	}

	public String getSessionId() {
		return mSessionId;
	}

	public String getCity() {
		return mCity;
	}
	
	public void setCity(String mCity) {
		this.mCity = mCity;
	}
	
	public void logout(Context context){
		SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.clear();
		editor.apply();
	}

	public void save(Context context){
		SharedPreferences.Editor editor = getPreferences(context).edit();
		editor.putInt(FIELD_ID, mId);
		editor.putString(FIELD_PHONE, mPhone);
		editor.putString(FIELD_EMAIL, mEmail);
		editor.putString(FIELD_SESSION_ID, mSessionId);
		editor.putString(FIELD_CITY, mCity);
		editor.apply();
	}
	
	private static SharedPreferences getPreferences(Context context){
		return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}
}
