package com.regall.old.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import com.regall.R;
import com.regall.old.adapters.AdapterCarMarks;
import com.regall.old.adapters.AdapterCarModels;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestGetCarMarks;
import com.regall.old.network.request.RequestGetCarModels;
import com.regall.old.network.request.RequestSaveUserObject;
import com.regall.old.network.response.ResponseGetCarMarks;
import com.regall.old.network.response.ResponseGetCarMarks.CarMark;
import com.regall.old.network.response.ResponseGetCarModels;
import com.regall.old.network.response.ResponseGetCarModels.CarModel;
import com.regall.old.network.response.ResponseSaveUSerObject;

public class AddCarFragment extends BaseFragment {
	
	@InjectView(R.id.spinnerMark) Spinner mSpinnerMark;
	@InjectView(R.id.spinnerModel) Spinner mSpinnerModel;
	
	private Callback<ResponseGetCarModels> callbackGetCarModels = new Callback<ResponseGetCarModels>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetCarModels response = (ResponseGetCarModels) object;
			if(response.isSuccess()){
				List<CarModel> carMarks = response.getmCarModels();
				fillModels(carMarks);
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			showToast(e.getMessage());
			logError(e.getMessage());
		}
	};
	
	private Callback<ResponseGetCarMarks> callbackGetCarMarks = new Callback<ResponseGetCarMarks>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetCarMarks response = (ResponseGetCarMarks) object;
			if(response.isSuccess()){
				List<CarMark> carMarks = response.getmCarMarks();
				fillMarks(carMarks);
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			showToast(e.getMessage());
			logError(e.getMessage());
		}
	};
	
	private Callback<ResponseSaveUSerObject> mCallbackSaveUserObject = new Callback<ResponseSaveUSerObject>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseSaveUSerObject response = (ResponseSaveUSerObject) object;
			if(response.isSuccess()){
				getMainActivity().addCarToUserProfile();
			} else {
				showToast(response.getStatusDetail());
			}
		}
		
		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			showToast(e.getMessage());
			logError(e.getMessage());
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_new_car, container, false);
		ButterKnife.inject(this, view);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		API api = getApi();
		showProgressDialog(R.string.message_get_car_marks);
		api.getCarMarks(new RequestGetCarMarks(), callbackGetCarMarks);
	}
	
	@OnClick(R.id.buttonSave)
	public void onSave(){
		CarModel model = (CarModel) mSpinnerModel.getSelectedItem();
		if(model != null){
			saveNewCarOnBackend(model);
		} else {
			showToast(R.string.message_no_model_selected);
		}
	}
	
	private void saveNewCarOnBackend(CarModel carModel){
		RequestSaveUserObject request = RequestSaveUserObject.create(getUser().getPhone(), carModel.getObjectId());
		API api = getApi();
		showProgressDialog(R.string.message_saving_user_object);
		api.saveUserObject(request, mCallbackSaveUserObject);
	}
	
	private void fillMarks(List<CarMark> carMarks){
		AdapterCarMarks adapter = new AdapterCarMarks(carMarks);
		mSpinnerMark.setAdapter(adapter);
	}
	
	private void fillModels(List<CarModel> carModels){
		AdapterCarModels adapter = new AdapterCarModels(carModels);
		mSpinnerModel.setAdapter(adapter);
	}
	
	@OnItemSelected(R.id.spinnerMark)
	public void onModelSelectionChanged(){
		CarMark model = (CarMark) mSpinnerMark.getSelectedItem();

		RequestGetCarModels request = new RequestGetCarModels(model.getName());
		API api = getApi();
		showProgressDialog(R.string.message_get_car_models);
		api.getCarModels(request, callbackGetCarModels);
	}
}
