package com.regall.old.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.regall.old.MainActivity;
import com.regall.R;
import com.regall.old.adapters.GeocodeResultAdapter;
import com.regall.old.network.geocode.GeocodingRequester;
import com.regall.old.network.geocode.GeocodingRequester.GeocodeListener;
import com.regall.old.network.geocode.json.GeocodeResponse;
import com.regall.old.network.geocode.json.Result;

public class GeocodeFragment extends BaseFragment implements OnItemClickListener, OnEditorActionListener {
	
	private final static int MIN_SYMBOLS_TO_SEARCH = 5;
	
	@InjectView(android.R.id.list) ListView mAddressesList;
	
	@InjectView(R.id.editAddress) EditText mEditAddressFragment;
	
	private GeocodingRequester mGeocodeRequester;
	private GeocodeResultAdapter mAdapter;
	
	private GeocodeListener mGeocodeRequestListener = new GeocodeListener() {
		
		@Override
		public void beforeRequest() {
			showProgressDialog(R.string.message_search_for_addresses);
		}
		
		@Override
		public void onRequestCompleted(GeocodeResponse response) {
			hideProgressDialog();
			processGeocodeResponse(response);
		}

		@Override
		public void onRequestFailed(Exception e) {
			hideProgressDialog();
			showToast(e.getMessage());
		}
		
		private void processGeocodeResponse(GeocodeResponse response){
			String status = response.getStatus();
			if(status.equals(GeocodingRequester.STATUS_OK)){
				List<Result> results = response.getResults();
				mAdapter.setData(results);
			} else if(status.equals(GeocodingRequester.STATUS_ZERO_RESULTS)){
				showToast(R.string.message_geocode_empty_result);
			}else {
				showToast(R.string.message_geocode_request_failed);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_geocode, container, false);
		ButterKnife.inject(this, view);
		mAdapter = new GeocodeResultAdapter();

		mAddressesList.setAdapter(mAdapter);
		mAddressesList.setOnItemClickListener(this);
		mAddressesList.setEmptyView(mListEmptyView);
		
		mEditAddressFragment.setOnEditorActionListener(this);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mGeocodeRequester = new GeocodingRequester(mGeocodeRequestListener);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Result geocodeResult = (Result) parent.getItemAtPosition(position);
		MainActivity activity = getMainActivity();
		if(activity != null){
			activity.onGeopointSelectedAsFilter(geocodeResult);
		}
	}
	
	@OnClick(R.id.buttonStartSearchForAddress)
	void onSearchRequest(){
		String query = mEditAddressFragment.getText().toString().trim();
		if(query.length() >= MIN_SYMBOLS_TO_SEARCH){
			mGeocodeRequester.search(query);
		} else {
			showToast(R.string.message_geocode_query_too_short);
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_SEARCH){
			onSearchRequest();
			return true;
		}
		
		return false;
	}
}
