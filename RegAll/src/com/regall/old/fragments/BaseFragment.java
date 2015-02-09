package com.regall.old.fragments;

import java.util.HashSet;

import android.util.Log;
import android.view.View;
import butterknife.InjectView;
import butterknife.Optional;

import com.actionbarsherlock.app.SherlockFragment;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationListener;
import com.regall.old.MainActivity;
import com.regall.old.model.AutowashFilter;
import com.regall.old.model.User;
import com.regall.old.network.API;
import com.regall.old.network.response.ResponseGetOrganizations.Point;
import com.regall.old.network.response.ResponseGetOrganizations.Point.ServiceDescription;
import com.regall.old.utils.Logger;

public class BaseFragment extends SherlockFragment {

	public final String mTag;

	@Optional @InjectView(android.R.id.empty) public View mListEmptyView;
	
	protected boolean mArgsInitialised;
	
	public BaseFragment() {
		mTag = getClass().getSimpleName();
		logDebug("Construct: mTag - " + mTag);
		Crashlytics.log(1, String.valueOf(User.PHONE), "Construct: mTag - " + mTag);
	}

	protected MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}

	protected void logDebug(String message) {
		Log.d(mTag, message);
		Logger.logDebug(mTag, message);
	}

	protected void logError(String message) {
		Log.e(mTag, message);
		Logger.logError(mTag, message);
	}

	public String tag() {
		return mTag;
	}

	protected void showMainFragment() {
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showMainFragment();
		}
	}

	protected void showSearchFragment() {
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showSearchFragment();
		}
	}

	protected void showCabinetFragment() {
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showCabinetFragment();
		}
	}

	protected void showOrdersHistoryFragment() {
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showOrdersHistoryFragment();
		}
	}
	
	public void showAutowashesFilter(){
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showAutowashesFilter();
		}
	}
	
	public void showLoginFragment(){
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showLoginFragment();
		}
	}
	
	public void showGeocodeFragment(){
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showGeocodeFragment();
		}
	}
	
	public void showRegistrationFragment(){
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showRegistrationFragment();
		}
	}
	
	public void showToast(String message){
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showToast(message);
		}
	}
	
	public void showToast(int messageResourceId){
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showToast(messageResourceId);
		}
	}
	
	protected API getApi() {
		return getMainActivity().getApi();
	}
	
	protected void showSelectWashServiceFragment(Point organisation){
		MainActivity activity = getMainActivity();
		if (activity != null){
			activity.showSelectWashServiceFragment(organisation);
		}
	}

	protected void showSelectWashTimeFragment(int objectId, String objectTitle, Point point, HashSet<ServiceDescription> selectedServices){
		MainActivity activity = getMainActivity();
		if (activity != null){
			activity.showSelectWashTimeFragment(objectId, objectTitle, point, selectedServices);
		}
	}
	
	protected void applyAutowashFilter(AutowashFilter filter){
		MainActivity activity = getMainActivity();
		if(activity != null){
			activity.applyAutowashFilter(filter);
		}
	}
	
	protected void showProgressDialog(int messageId){
		MainActivity activity = getMainActivity();
		if (activity != null){
			activity.showProgressDialog(messageId);
		}
	}
	
	protected void hideProgressDialog(){
		MainActivity activity = getMainActivity();
		if (activity != null){
			activity.hideProgressDialog();
		}
	}
	
	protected User getUser(){
		MainActivity activity = getMainActivity();
		return activity != null ? activity.getUser() : null;
	}
	
	protected void updateLocation(LocationListener locationListener){
		MainActivity activity = getMainActivity();
		if(activity != null){
			activity.updateLocation(locationListener);
		} else {
			locationListener.onLocationChanged(null);
		}
	}
	
	protected void showNewCardFragment(){
		MainActivity activity = getMainActivity();
		if(activity != null){
			activity.showNewCardFragment();
		}
	}
	
	protected void showRecentAutowashesFragment(){
		MainActivity activity = getMainActivity();
		if(activity != null){
			activity.showRecentAutowashes();
		}
	}
	
}
