package com.regall.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.location.LocationListener;
import com.regall.R;
import com.regall.old.adapters.AutowashAdapter;
import com.regall.old.adapters.AutowashAdapter.ViewHolderParent;
import com.regall.old.db.DAOUserObject;
import com.regall.old.fragments.BaseFragment;
import com.regall.old.fragments.FastRegistrationFragment;
import com.regall.old.model.AutowashFilter;
import com.regall.old.model.AutowashFilter.LocationFilter;
import com.regall.old.model.User;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestGetOrganizations;
import com.regall.old.network.response.ResponseGetOrganizations;
import com.regall.old.network.response.ResponseGetOrganizations.Organisation;
import com.regall.old.network.response.ResponseGetOrganizations.Point;
import com.regall.old.network.response.ResponseGetOrganizations.ResultPage;
import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;
import com.regall.old.utils.DialogHelper;

import java.util.List;

public class AutowashListFragment extends BaseFragment implements OnScrollListener, OnGroupClickListener {

	private final static int PAGE_NOT_EXISTS = -1;
	private final static String ARG_FILTER = "filter";

	@InjectView(android.R.id.list)
	ExpandableListView mAutowashesList;

	private AutowashAdapter mAdapterAutowashes;
	private AutowashFilter mFilter;
	private ResultPage mResultPage;

	private boolean mRequestInProgress;
	private boolean mLastItemVisible;

	private double mLatitude;
	private double mLongitude;

	private boolean mRefreshRequestDelayed;
	
	private Callback<ResponseGetOrganizations> mCallbackGetWashes = new Callback<ResponseGetOrganizations>() {

		@Override
		public void success(Object object) {
			logDebug("mCallbackGetWashes.onSuccess");
			hideProgressDialog();
			mRequestInProgress = false;
			ResponseGetOrganizations response = (ResponseGetOrganizations) object;
			if (response.isSuccess()) {
				displayDataForUser(response);
			} else {
				showToast(response.getStatusDetail());
			}
		}

		private void displayDataForUser(ResponseGetOrganizations response) {
			logDebug("mCallbackGetWashes.displayDataForUser");
			List<Organisation> organisations = response.getOrganisations();
			if (organisations != null && mAutowashesList != null) {
				mResultPage = response.getPage();
				if(mAdapterAutowashes == null){
					mAdapterAutowashes = new AutowashAdapter(organisations, mSignupClickListener, mFilter.getLatitude(), mFilter.getLongitude());
					mAutowashesList.setAdapter(mAdapterAutowashes);
				} else {
					mAdapterAutowashes.addAll(organisations);
				}
			} else if (organisations == null) {
				showToast(R.string.message_no_autowashes_found);
			}
		}

		@Override
		public void failure(Exception e) {
			logDebug("mCallbackGetWashes.failure " + e.getMessage());
			mRequestInProgress = false;
			e.printStackTrace();
			hideProgressDialog();
			showToast(e.getMessage());
		}
	};

	private OnClickListener mSignupClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			logDebug("mSignupClickListener.onClick");
			User user = getUser();
			if(user != null){
				DAOUserObject dao = new DAOUserObject(getActivity());
				List<ClientObject> objects = dao.getAll();
				if(objects.size() > 0){
					Point organisation = (Point) v.getTag();
					showSelectWashServiceFragment(organisation);
				} else {
					inviteUserToCreateCar();
				}
			} else {
				askUnregisteredUserToRegister();
			}
		}

	};
	
	private void inviteUserToCreateCar(){
		logDebug("inviteUserToCreateCar");
		Dialog dialog = DialogHelper.createConfirmDialog(getActivity(), R.string.dialog_title_wait_a_minute, getString(R.string.dialog_no_cars), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showCabinetFragment();
			}
		});
		dialog.show();
	}
	
	private LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onLocationChanged(Location location) {
			logDebug("mLocationListener.onLocationChanged");
			if (location != null) {
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();
				onLocationUpdated();
			} else {
				onLocationUpdateFailed();
			}
		}
	};

	public static AutowashListFragment create(AutowashFilter filter) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_FILTER, filter);

		AutowashListFragment fragment = new AutowashListFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		logDebug("onCreateView");
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_autowashes_list, container, false);
		ButterKnife.inject(this, view);

		mAutowashesList.setEmptyView(mListEmptyView);
		mAutowashesList.setOnGroupClickListener(this);
		mAutowashesList.setOnScrollListener(this);
		
		if(getUser() != null){
			getMainActivity().enableProfileButton();
		} else {
			getMainActivity().disableProfileButton();
		}

		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		logDebug("onActivityCreated");
		if (!mArgsInitialised) {
			Bundle args = getArguments();
			mFilter = (AutowashFilter) args.getSerializable(ARG_FILTER);
			mArgsInitialised = true;
		}

		if(mAdapterAutowashes != null){
			mAutowashesList.setAdapter(mAdapterAutowashes);
		} else {
			requestAutowashes();
		}
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		logDebug("onViewStateRestored");
		if(mAdapterAutowashes != null){
			mAutowashesList.setAdapter(mAdapterAutowashes);
		} else {
			requestAutowashes();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.menu_autowash_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		logDebug("onOptionsItemSelected");
		int id = item.getItemId();
		User user = getUser();
		switch (id) {
			case R.id.actionFilter:
				showAutowashesFilter();
				return true;
			case R.id.actionRecent:
				if(user != null){
					showRecentAutowashesFragment();
				} else {
					askUnregisteredUserToRegister();
				}
				return true;
			case R.id.actionHistory:
				if(user != null){
					showOrdersHistoryFragment();
				} else {
					askUnregisteredUserToRegister();
				}
				return true;
		}

		return false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		logDebug("onDestroyView");
		ButterKnife.reset(this);
		getMainActivity().disableProfileButton();
	}

	public void applyFilter(AutowashFilter filter) {
		logDebug("applyFilter");
		mFilter = filter;
		if(mAdapterAutowashes != null){
			mAdapterAutowashes.clearAll();
		}
		mAdapterAutowashes = null;
		mResultPage = null;
		if (mFilter.getLocationFilterType().equals(LocationFilter.CURRENT_LOCATION)) {
			mFilter.setLatitude(mLatitude);
			mFilter.setLongitude(mLongitude);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		logDebug("onScroll");
		mLastItemVisible = (totalItemCount - visibleItemCount) == firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		logDebug("onScrollStateChanged");
		if (scrollState == SCROLL_STATE_IDLE && mLastItemVisible && !mRequestInProgress) {
			requestAutowashes();
		}
	}

	private void requestAutowashes() {
		logDebug("requestAutowashes, filter type - " + mFilter.getLocationFilterType());
		if (mFilter.getLocationFilterType().equals(LocationFilter.CURRENT_LOCATION) && !mFilter.isCurrentLocationInitialised()) {
			logDebug("requestAutowashes.updateCurrentLocation");
			updateCurrentLocation();
		} else {
			logDebug("requestAutowashes.sendGetAutowashesRequest, lat - " + mLatitude + " lon - " + mLongitude);
			sendGetAutowashesRequest();
		}
	}

	private void updateCurrentLocation() {
		logDebug("updateCurrentLocation");
		mRefreshRequestDelayed = true;
		updateLocation(mLocationListener);
	}

	private void sendGetAutowashesRequest() {
		logDebug("sendGetAutowashesRequest");
		int page = definePageToRequest();
		if (page != PAGE_NOT_EXISTS) {
			showProgressDialog(R.string.message_requesting_for_autowashes);
			mRequestInProgress = true;
			API api = getApi();
			RequestGetOrganizations request = RequestGetOrganizations.byFilter(mFilter, 1, page);
			api.getOrganisations(request, mCallbackGetWashes);
		} else {
			showToast(R.string.message_no_more_results);
		}
	}

	private int definePageToRequest() {
		logDebug("definePageToRequest");
		if (mResultPage != null && !mResultPage.isLastPage()) {
			return mResultPage.getPageNumber() + 1;
		} else if (mResultPage != null && mResultPage.isLastPage()) {
			return PAGE_NOT_EXISTS;
		} else {
			return 1;
		}
	}

	private void onLocationUpdated() {
		logDebug("onLocationUpdated");
		//TODO check null
		if (mRefreshRequestDelayed) {
			mRefreshRequestDelayed = false;
			if (mFilter != null && mFilter.getLocationFilterType().equals(LocationFilter.CURRENT_LOCATION)) {
				mFilter.setLatitude(mLatitude);
				mFilter.setLongitude(mLongitude);
				sendGetAutowashesRequest();
			}
		}
	}

	private void onLocationUpdateFailed() {
		logDebug("onLocationUpdateFailed");
		showToast(R.string.message_cannot_update_location);
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		logDebug("onGroupClick - " + groupPosition);
		ViewHolderParent holder = (ViewHolderParent) v.getTag();
		View layout = holder.mLayoutContent;
		ImageView iconArrow = holder.mImageArrow; 
		
		int paddingLeft = layout.getPaddingLeft();
		int paddingTop = layout.getPaddingTop();
		int paddingRight = layout.getPaddingRight();
		int paddingBottom = layout.getPaddingBottom();
		
		for (int i = 0, max = parent.getCount(); i < max; i++) {
			if (i != groupPosition && parent.isGroupExpanded(i)) {
				parent.collapseGroup(i);
				layout.setBackgroundResource(R.drawable.shape_white_stroke_dark);
				iconArrow.setImageResource(R.drawable.arrow_down);
			} else if (i == groupPosition) {
				if (parent.isGroupExpanded(i)) {
					parent.collapseGroup(i);
					layout.setBackgroundResource(R.drawable.shape_white_stroke_dark);
					iconArrow.setImageResource(R.drawable.arrow_down);
				} else {
					parent.expandGroup(i);
					layout.setBackgroundResource(R.drawable.blue_stroke_top);
					iconArrow.setImageResource(R.drawable.arrow_top);
				}
			}
		}

		layout.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

		return true;
	}

	private void askUnregisteredUserToRegister() {
		logDebug("askUnregisteredUserToRegister");
		String dialogText = getString(R.string.dialog_text_function_need_registration);
		Dialog dialog = DialogHelper.createConfirmDialog(getActivity(), R.string.dialog_title_wait_a_minute, dialogText, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				FastRegistrationFragment fragment = new FastRegistrationFragment();
				getMainActivity().loadFragment(fragment, true);
			}
		});
		
		dialog.show();
	}
}
