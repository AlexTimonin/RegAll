package com.regall.old.fragments;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Optional;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.regall.R;
import com.regall.old.SmsReceiver;
import com.regall.old.adapters.AdapterCarMarks;
import com.regall.old.adapters.AdapterCarModels;
import com.regall.old.db.DAOUserObject;
import com.regall.old.model.User;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestConfirmRegistration;
import com.regall.old.network.request.RequestGetCarMarks;
import com.regall.old.network.request.RequestGetCarModels;
import com.regall.old.network.request.RequestGetUserObjects;
import com.regall.old.network.request.RequestRegisterClient;
import com.regall.old.network.request.RequestSaveUserObject;
import com.regall.old.network.response.ResponseGetCarMarks;
import com.regall.old.network.response.ResponseGetUserObjects;
import com.regall.old.network.response.ResponseSaveUSerObject;
import com.regall.old.network.response.ResponseGetCarMarks.CarMark;
import com.regall.old.network.response.ResponseGetCarModels;
import com.regall.old.network.response.ResponseGetCarModels.CarModel;
import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;
import com.regall.old.network.response.ResponseRegisterClient;
import com.regall.old.utils.DialogHelper;
import com.regall.old.utils.DialogHelper.InputListener;

public class FastRegistrationFragment extends BaseFragment implements InputListener, ValidationListener {
	
	public final static String REGEXP_PHONE = "\\+38\\(0\\d{2}\\)\\d{3}-\\d{2}-\\d{2}";
	
	@Optional @InjectView(R.id.stubCarModelSpinners) ViewStub mStubCarModels;

	@Optional @InjectView(R.id.spinnerCarMark) Spinner mSpinnerMark;
	@Optional @InjectView(R.id.spinnerCarModel) Spinner mSpinnerModel;
	
	@Required(order = 1, messageResId = R.string.message_no_phone_to_register)
	@Regex(order = 2, pattern = REGEXP_PHONE, messageResId = R.string.message_incorrect_phone)
	@Optional @InjectView(R.id.editPhone) EditText mEditPhone;
	
	private String mPhoneToRegister;
	private Validator mValidator;
	private boolean mStageAddCar;
	
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
	
	private Callback<ResponseRegisterClient> mRegistrationFirstStepCallback = new Callback<ResponseRegisterClient>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseRegisterClient response = (ResponseRegisterClient) object;

			int responseCode = response.getStatusCode();
			if (responseCode == ResponseRegisterClient.STATUS_OK || responseCode == ResponseRegisterClient.STATUS_USER_NOT_ACTIVATED) {
				SharedPreferences prefs = getActivity().getSharedPreferences(SmsReceiver.PREFERENCES_REGISTRATION, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(SmsReceiver.PREFERENCES_REGISTRATION_PHONE, mPhoneToRegister);
				editor.apply();
				
				showInputForConfirmationCode();
				
			} else if(responseCode == ResponseRegisterClient.STATUS_USER_EXISTS){
				saveUserProfile(response, false);
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			e.printStackTrace();
			hideProgressDialog();
			showToast(e.getMessage());
		}
	};

	private Callback<ResponseRegisterClient> mRegistrationConfirmCallback = new Callback<ResponseRegisterClient>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseRegisterClient response = (ResponseRegisterClient) object;
			if (response.isSuccess()) {
				saveUserProfile(response, true);
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			e.printStackTrace();
			hideProgressDialog();
			showToast(e.getMessage());
		}
	};
	
	private void showInputForConfirmationCode() {
		String dialogTitle = getString(R.string.dialog_confirm_title);
		String dialogText = getString(R.string.dialog_confirm_text);
		String emptyInputMessage = getString(R.string.message_set_confirm_code);

		mDialogConfirmation = DialogHelper.showInputDialog(getActivity(), dialogTitle, dialogText, this, emptyInputMessage);
		mDialogConfirmation.show();
	}
	
	private BroadcastReceiver mRegistrationReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(SmsReceiver.ACTION_REGISTRATION_COMPLETE)){
				if(mDialogConfirmation != null && mDialogConfirmation.isShowing()){
					mDialogConfirmation.dismiss();
				}
				User user = (User) intent.getSerializableExtra(SmsReceiver.EXTRA_USER_PROFILE);
				getMainActivity().setUser(user);
				proceedUserRegistration(true);
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_registration_inapp, container, false);
		ButterKnife.inject(this, view);
		mValidator = new Validator(this);
		mValidator.setValidationListener(this);
		
		mIntentFilterRegistration = new IntentFilter(SmsReceiver.ACTION_REGISTRATION_COMPLETE);
		getActivity().registerReceiver(mRegistrationReceiver, mIntentFilterRegistration);
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mRegistrationReceiver);
		ButterKnife.reset(this);
	}

	private void saveUserProfile(ResponseRegisterClient response, boolean newUser) {
		int id = response.getId();
		User user = new User(id, mPhoneToRegister, "", "");
		user.save(getActivity());
		getMainActivity().setUser(user);
		
		proceedUserRegistration(newUser);
	}
	
	private void proceedUserRegistration(boolean newUser){
		if(newUser){
			mStageAddCar = true;
			
			mStubCarModels.inflate();
			mEditPhone.setVisibility(View.GONE);
			ButterKnife.reset(this);
			ButterKnife.inject(this, getView());
			
			getMarks();
		} else {
			//TODO get user objects
			getUserObjects();
		}
	}
	
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
				getMainActivity().onBackPressed();
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			hideProgressDialog();
		}
	};
	
	private void getUserObjects(){
		RequestGetUserObjects request = RequestGetUserObjects.create(getUser().getPhone());
		showProgressDialog(R.string.message_receiving_user_objects);
		API api = getApi();
		api.getUserObjects(request, mGetObjectsCallback);
	}
	
	private void getMarks(){
		API api = getApi();
		showProgressDialog(R.string.message_get_car_marks);
		api.getCarMarks(new RequestGetCarMarks(), callbackGetCarMarks);
	}
	
	private void fillMarks(List<CarMark> carMarks){
		AdapterCarMarks adapter = new AdapterCarMarks(carMarks);
		mSpinnerMark.setAdapter(adapter);
	}
	
	private void fillModels(List<CarModel> carModels){
		AdapterCarModels adapter = new AdapterCarModels(carModels);
		mSpinnerModel.setAdapter(adapter);
	}

	@Optional @OnItemSelected(R.id.spinnerCarMark)
	public void onModelSelectionChanged(){
		CarMark model = (CarMark) mSpinnerMark.getSelectedItem();

		RequestGetCarModels request = new RequestGetCarModels(model.getName());
		API api = getApi();
		showProgressDialog(R.string.message_get_car_models);
		api.getCarModels(request, callbackGetCarModels);
	}

	@Override
	public void onInputComplete(String userInput) {
		sendConfirmationRequest(userInput);
	}
	
	private void sendConfirmationRequest(String smsCode) {
		showProgressDialog(R.string.message_completing_registration);
		RequestConfirmRegistration request = RequestConfirmRegistration.create(mPhoneToRegister, smsCode);
		API api = getApi();
		api.confirmRegistration(request, mRegistrationConfirmCallback);
	}
	
	@Override
	public void onValidationSucceeded() {
		mPhoneToRegister = mEditPhone.getText().toString().replaceAll("[\\(\\)\\-]", "");

		showProgressDialog(R.string.message_registration_in_progress);
		RequestRegisterClient request = RequestRegisterClient.create(mPhoneToRegister, "");
		API api = getApi();
		api.registerNewClient(request, mRegistrationFirstStepCallback);
	}

	@Override
	public void onValidationFailed(View failedView, Rule<?> failedRule) {
		if (failedView instanceof EditText) {
			EditText input = (EditText) failedView;
			input.setError(failedRule.getFailureMessage());
			input.requestFocus();
		}
	}

	@Override
	public void onViewValidationSucceeded(View succeededView) {
		if (succeededView instanceof EditText) {
			EditText input = (EditText) succeededView;
			input.setError(null);
		}
	}
	
	@OnClick(R.id.buttonRegister)
	void register() {
		if(!mStageAddCar){
			mValidator.validate();
		} else {
			CarModel model = (CarModel) mSpinnerModel.getSelectedItem();
			if(model != null){
				saveNewCarOnBackend(model);
				
				ClientObject object = ClientObject.fromModel(model);
				DAOUserObject dao = new DAOUserObject(getActivity());
				dao.insert(object);
			} else {
				showToast(R.string.message_no_model_selected);
			}
		}
	}
	
	private void saveNewCarOnBackend(CarModel carModel){
		RequestSaveUserObject request = RequestSaveUserObject.create(getUser().getPhone(), carModel.getObjectId());
		API api = getApi();
		showProgressDialog(R.string.message_saving_user_object);
		api.saveUserObject(request, mCallbackSaveUserObject);
	}
	
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

	private IntentFilter mIntentFilterRegistration;

	private AlertDialog mDialogConfirmation;
}
