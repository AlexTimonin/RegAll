package com.regall.old.fragments;

import java.util.HashSet;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.regall.R;
import com.regall.old.adapters.FilterServiceAdapter;
import com.regall.old.model.AdditionalService;
import com.regall.old.model.AutowashFilter;
import com.regall.old.model.AutowashFilter.LocationFilter;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.geocode.json.Result;
import com.regall.old.network.response.ResponseGetServices;
import com.regall.old.network.response.ResponseGetServices.Service;

public class FilterFragment extends BaseFragment implements android.widget.RadioGroup.OnCheckedChangeListener {

	private final static String ARG_FILTER = "filter";
	
	@InjectView(R.id.radioGroupLocationFilter) RadioGroup mRadioGroupLocation;
	@InjectView(R.id.radioNearestByLocation) RadioButton mRadioButtonByLocation;
	@InjectView(R.id.radioNearestByAddress) RadioButton mRadioButtonByAddress;
	@InjectView(R.id.editAddress) EditText mEditAddress;
	@InjectView(R.id.gridServices) GridView mGridServices;
	
	@InjectView(R.id.checkboxRestRoom) CheckBox mCheckBoxRestRoom;
	@InjectView(R.id.checkboxRestRoom) CheckBox mCheckBoxPayCard;
	@InjectView(R.id.checkboxRestRoom) CheckBox mCheckBoxFuel;
	@InjectView(R.id.checkboxRestRoom) CheckBox mCheckBoxTechSupport;
	@InjectView(R.id.checkboxRestRoom) CheckBox mCheckBoxWifi;
	@InjectView(R.id.checkboxRestRoom) CheckBox mCheckBoxFood;
	
	private boolean mArgsInitialized;

	private HashSet<Service> mSelectedServices = new HashSet<Service>();
	private HashSet<AdditionalService> mAdditionalServices = new HashSet<AdditionalService>();
	private LocationFilter mLocationFilter = LocationFilter.CURRENT_LOCATION;
	private Result mAddressLocationFilter;
	
	public static FilterFragment create(AutowashFilter filter){
		Bundle args = new Bundle();
		args.putSerializable(ARG_FILTER, filter);
		
		FilterFragment fragment = new FilterFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	private Callback<ResponseGetServices> mGetServicesCallback = new Callback<ResponseGetServices>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetServices response = (ResponseGetServices) object;
			fillAvailableServices(response.getServices());
		}
		
		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			showToast(e.getMessage());
		}
	};
	
	private OnCheckedChangeListener mServicecheckListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Service service = (Service) buttonView.getTag();
			if(isChecked){
				mSelectedServices.add(service);
			} else {
				mSelectedServices.remove(service);
			}
		}
	};
	
	private OnFocusChangeListener mAddressFocusListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				showGeocodeFragment();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_filter, container, false);
		ButterKnife.inject(this, view);
		mRadioGroupLocation.setOnCheckedChangeListener(this);
		if(mLocationFilter.equals(LocationFilter.ADDRESS)) {
			mRadioButtonByAddress.setSelected(true);
		} else {
			mRadioButtonByLocation.setSelected(true);
		}
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle args = getArguments();
		if(args != null && !mArgsInitialized){
			initializeFilterWithBasicValues(args);
			mArgsInitialized = true;
		} 
		
		showProgressDialog(R.string.message_requesting_for_services);
		API api = getApi();
		api.getServices(mGetServicesCallback);
		
		mCheckBoxRestRoom.setSelected(mAdditionalServices.contains(AdditionalService.REST_ROOM));
		mCheckBoxPayCard.setSelected(mAdditionalServices.contains(AdditionalService.PAY_VISA));
		mCheckBoxFuel.setSelected(mAdditionalServices.contains(AdditionalService.FUEL));
		mCheckBoxTechSupport.setSelected(mAdditionalServices.contains(AdditionalService.TECH_SUPPORT));
		mCheckBoxWifi.setSelected(mAdditionalServices.contains(AdditionalService.WIFI));
		mCheckBoxFood.setSelected(mAdditionalServices.contains(AdditionalService.FOOD));
	}

	private void initializeFilterWithBasicValues(Bundle args) {
		AutowashFilter currentFilter = (AutowashFilter) args.getSerializable(ARG_FILTER);
		if(currentFilter != null){
			mLocationFilter = currentFilter.getLocationFilterType();
			mAddressLocationFilter = currentFilter.getGeopointDescription();
			mSelectedServices = currentFilter.getServicesSet();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}
	
	
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		logDebug("onViewStateRestored");
		if(mLocationFilter.equals(LocationFilter.ADDRESS)){
			mRadioGroupLocation.check(R.id.radioNearestByAddress);
			if(mAddressLocationFilter != null){
				mEditAddress.setText(mAddressLocationFilter.getFormattedAddress());
				mEditAddress.setError(null);
			}
		} else {
			mRadioGroupLocation.check(R.id.radioNearestByLocation);
		}
	}
	
	private void fillAvailableServices(List<Service> services){
		if(services != null){
			if(mGridServices != null){
				FilterServiceAdapter adapterServices = new FilterServiceAdapter(services, mSelectedServices, mServicecheckListener);
				mGridServices.setAdapter(adapterServices);
			}
		} else {
			showToast(R.string.message_cannon_load_services);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.radioNearestByAddress){
			mLocationFilter = LocationFilter.ADDRESS;
			setAddressFieldActive(true);
		} else {
			mLocationFilter = LocationFilter.CURRENT_LOCATION;
			mAddressLocationFilter = null;
			mEditAddress.setText("");
			setAddressFieldActive(false);
		}
	}
	
	private void setAddressFieldActive(boolean active){
		mEditAddress.setEnabled(active);
		mEditAddress.setOnFocusChangeListener(active ? mAddressFocusListener : null);
	}
	
	public void setLocationFilter(Result result){
		mAddressLocationFilter = result;
	}
	
	@OnClick(R.id.buttonApplyFilter)
	public void applyFilter(){
		if (validate()){
			AutowashFilter filter = new AutowashFilter(mLocationFilter, mAddressLocationFilter, mSelectedServices, mAdditionalServices);
			applyAutowashFilter(filter);
		}
	}
	
	@OnClick(R.id.buttonResetFilter)
	public void resetFilter(){
		mEditAddress.setText("");
		mRadioButtonByLocation.setSelected(true);
		
		mCheckBoxRestRoom.setChecked(false);
		mCheckBoxPayCard.setChecked(false);
		mCheckBoxFuel.setChecked(false);
		mCheckBoxTechSupport.setChecked(false);
		mCheckBoxWifi.setChecked(false);
		mCheckBoxFood.setChecked(false);
		
		AutowashFilter filter = new AutowashFilter(LocationFilter.CURRENT_LOCATION, new HashSet<Service>(), new HashSet<AdditionalService>());
		applyAutowashFilter(filter);
	}
	
	private boolean validate(){
		if (mLocationFilter.equals(LocationFilter.ADDRESS) && mAddressLocationFilter == null) {
			mEditAddress.setError(getString(R.string.message_error_addrees_not_specified));
			return false;
		}
		
		return true;
	}
	
	@OnCheckedChanged(R.id.checkboxRestRoom)
	void checkRestRoom(boolean checked){
		if(checked){
			mAdditionalServices.add(AdditionalService.REST_ROOM);
		} else {
			mAdditionalServices.remove(AdditionalService.REST_ROOM);
		}
	}
	
	@OnCheckedChanged(R.id.checkboxPayCard)
	void checkPayCard(boolean checked){
		if(checked){
			mAdditionalServices.add(AdditionalService.PAY_VISA);
		} else {
			mAdditionalServices.remove(AdditionalService.PAY_VISA);
		}
	}
	
	@OnCheckedChanged(R.id.checkboxFuel)
	void checkFuel(boolean checked){
		if(checked){
			mAdditionalServices.add(AdditionalService.FUEL);
		} else {
			mAdditionalServices.remove(AdditionalService.FUEL);
		}
	}
	
	@OnCheckedChanged(R.id.checkboxTexhSupport)
	void checkTechSupport(boolean checked){
		if(checked){
			mAdditionalServices.add(AdditionalService.TECH_SUPPORT);
		} else {
			mAdditionalServices.remove(AdditionalService.TECH_SUPPORT);
		}
	}
	
	@OnCheckedChanged(R.id.checkboxWifi)
	void checkWifi(boolean checked){
		if(checked){
			mAdditionalServices.add(AdditionalService.WIFI);
		} else {
			mAdditionalServices.remove(AdditionalService.WIFI);
		}
	}
	
	@OnCheckedChanged(R.id.checkboxFood)
	void checkFood(boolean checked){
		if(checked){
			mAdditionalServices.add(AdditionalService.FOOD);
		} else {
			mAdditionalServices.remove(AdditionalService.FOOD);
		}
	}
}
