package com.regall.old.fragments;

import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import com.regall.R;
import com.regall.old.adapters.AdapterSpinnerCar;
import com.regall.old.adapters.CityAdapter;
import com.regall.old.db.DAOUserObject;
import com.regall.old.model.User;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestDeleteCar;
import com.regall.old.network.request.RequestGetCities;
import com.regall.old.network.request.RequestGetUserObjects;
import com.regall.old.network.response.BasicResponse;
import com.regall.old.network.response.ResponseGetCities;
import com.regall.old.network.response.ResponseGetCities.City;
import com.regall.old.network.response.ResponseGetUserObjects;
import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;
import com.regall.old.utils.DialogHelper;

public class CabinetFragment extends BaseFragment {

	@InjectView(R.id.spinnerCity) Spinner mSpinnerCity;
	@InjectView(R.id.spinnerUserCars) Spinner mSpinnerCars;
	
	@InjectView(R.id.buttonDelete) ImageButton mButtonDelete;
	
	@InjectView(R.id.editPhone) EditText mEditPhone;
	@InjectView(R.id.editEmail) EditText mEditEmail;
	@InjectView(R.id.checkboxAgreeToUsePersonnalData) CheckBox mCheckBoxPersonnalData;
	
	private CityAdapter mAdapterCities;
	private AdapterSpinnerCar mAdapterCars;
	
	private boolean mNeedUpdateUserObjects;
	
	private Callback<BasicResponse> mDeleteCarCallback = new Callback<BasicResponse>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			BasicResponse response = (BasicResponse) object;
			if(response.isSuccess()){
				mNeedUpdateUserObjects = true;
				getUserObjects();
			} else {
				showToast(response.getStatusDetail());
			}
		}
		
		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			showToast("Request failed - " + e.getMessage());
		}
	};
	
	private Callback<ResponseGetCities> mGetCitiesCallback = new Callback<ResponseGetCities>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetCities response = (ResponseGetCities) object;
			if(response.isSuccess()){
				mAdapterCities = new CityAdapter(response.getCities());
				mSpinnerCity.setAdapter(mAdapterCities);
				
				String cityTitle = getUser().getCity();
				if(cityTitle != null && !cityTitle.isEmpty()){
					City city = new City(cityTitle);
					int cityPosition = mAdapterCities.getCityPosition(city);
					if(cityPosition > -1){
						mSpinnerCity.setSelection(cityPosition);
					}
				}
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			hideProgressDialog();
		}
	};

	private Callback<ResponseGetUserObjects> mGetObjectsCallback = new Callback<ResponseGetUserObjects>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetUserObjects response = (ResponseGetUserObjects) object;
			if (response.isSuccess()) {
				List<ClientObject> clientObjects = response.getClientObjects();
				DAOUserObject dao = new DAOUserObject(getActivity());
				dao.deleteAll();

				if(clientObjects != null){
					dao.insertAll(clientObjects);
				}
				
				mNeedUpdateUserObjects = false;
				
				setupUserObjects(clientObjects);
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			hideProgressDialog();
		}
	};

	private DialogInterface.OnClickListener onUseDataRejectCancelled = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			mCheckBoxPersonnalData.setChecked(true);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cabinet, container, false);
		ButterKnife.inject(this, view);
		mButtonDelete.setEnabled(false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(mAdapterCities == null){
			showProgressDialog(R.string.message_requesting_for_cities);
			RequestGetCities request = new RequestGetCities();
			getApi().getCities(request, mGetCitiesCallback);
		} else {
			mSpinnerCity.setAdapter(mAdapterCities);
		}

		User user = getUser();
		if (user != null) {
			String phone = user.getPhone();
			mEditPhone.setText(phone);

			String email = user.getEmail();
			mEditEmail.setText(email);

			getUserObjects();
		} else {
			showToast(R.string.message_no_user_data);
		}
	}
	
	private void getUserObjects(){
		DAOUserObject dao = new DAOUserObject(getActivity());
		List<ClientObject> objects = dao.getAll();
		if(!objects.isEmpty() && !mNeedUpdateUserObjects){
			setupUserObjects(objects);
		} else {
			RequestGetUserObjects request = RequestGetUserObjects.create(getUser().getPhone());
			showProgressDialog(R.string.message_receiving_user_objects);
			API api = getApi();
			api.getUserObjects(request, mGetObjectsCallback);
		}
	}
	
	private void setupUserObjects(List<ClientObject> objects){
		mAdapterCars = new AdapterSpinnerCar(objects);
		mSpinnerCars.setAdapter(mAdapterCars);
		mButtonDelete.setEnabled(objects != null && objects.size() > 0);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.buttonSave)
	void saveSettings() {
		if(mCheckBoxPersonnalData.isChecked()){
			City city = (City) mSpinnerCity.getSelectedItem();
			if(city != null){
				User user = getUser();
				user.setCity(city.getTitle());
				user.save(getActivity());
			}
			
			getMainActivity().onBackPressed();
		} else {
			getMainActivity().logout();
		}
	}

	@OnCheckedChanged(R.id.checkboxAgreeToUsePersonnalData)
	void onPersonnalDataAgreementChanged(boolean checked) {
		if (!checked) {
			confirmUseDataRejection();
		}
	}

	private void confirmUseDataRejection() {
		Dialog dialog = DialogHelper.createConfirmDialog(getActivity(), R.string.dialog_title_confirm_data_rejection, R.string.message_confirm_decline_use_my_data, onUseDataRejectCancelled);
		dialog.show();
	}

	@OnClick(R.id.buttonAddNewCar)
	public void showAddNewCard(){
		showNewCardFragment();
	}

	@OnClick(R.id.buttonDelete)
	void onDeleteCar(View v) {
		final ClientObject object = (ClientObject) mSpinnerCars.getSelectedItem();
		if(object != null){
			String message = getString(R.string.message_confirm_delete_object, object.getObjectName());
			Dialog dialog = DialogHelper.createConfirmDialog(getActivity(), R.string.dialog_title_confirm_action, message, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteUserObject(object);
				}
			});
			
			dialog.show();
		}
	}
	
	private void deleteUserObject(ClientObject object){
		User user = getUser();
		showProgressDialog(R.string.message_removing_car);
		RequestDeleteCar request = RequestDeleteCar.create(object.getObjectId(), user.getId(), user.getSessionId());
		getApi().deleteCar(request, mDeleteCarCallback);
	}

	public void setNeedUpdateUserObjects(boolean mNeedUpdateUserObjects) {
		this.mNeedUpdateUserObjects = mNeedUpdateUserObjects;
	}
	
	
}
