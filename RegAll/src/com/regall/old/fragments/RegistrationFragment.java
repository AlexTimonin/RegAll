package com.regall.old.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;
import com.regall.old.MainActivity;
import com.regall.R;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestConfirmRegistration;
import com.regall.old.network.request.RequestRegisterClient;
import com.regall.old.network.response.ResponseRegisterClient;
import com.regall.old.utils.DialogHelper;
import com.regall.old.utils.DialogHelper.InputListener;

public class RegistrationFragment extends BaseFragment implements InputListener, ValidationListener {

	public final static String REGEXP_PHONE = "\\+38\\(0\\d{2}\\)\\d{3}-\\d{2}-\\d{2}";

	@Required(order = 1, messageResId = R.string.message_no_phone_to_register)
	@Regex(order = 2, pattern = REGEXP_PHONE, messageResId = R.string.message_incorrect_phone)
	@InjectView(R.id.editPhone)
	EditText mEditPhone;

	@Password(order = 3, messageResId = R.string.message_no_password_to_register)
	@TextRule(order = 4, minLength = 6, messageResId = R.string.message_password_is_weak)
	@InjectView(R.id.editPassword)
	EditText mEditPassword;

	@ConfirmPassword(order = 5, messageResId = R.string.message_passwords_do_not_match)
	@InjectView(R.id.editConfirmPassword)
	EditText mEditPasswordConfirm;

	private Validator mValidator;

	private String mPhoneToRegister;

	private Callback<ResponseRegisterClient> mRegistrationFirstStepCallback = new Callback<ResponseRegisterClient>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseRegisterClient response = (ResponseRegisterClient) object;

			int responseCode = response.getStatusCode();
			if (responseCode == ResponseRegisterClient.STATUS_OK || responseCode == ResponseRegisterClient.STATUS_USER_NOT_ACTIVATED) {
				showInputForConfirmationCode();
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
				saveUserProfile(response);
				showMainFragment();
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_registration_new, container, false);
		ButterKnife.inject(this, view);
		mValidator = new Validator(this);
		mValidator.setValidationListener(this);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.buttonRegister)
	void register() {
		mValidator.validate();
	}

	private void sendConfirmationRequest(String smsCode) {
		showProgressDialog(R.string.message_completing_registration);
		RequestConfirmRegistration request = RequestConfirmRegistration.create(mPhoneToRegister, smsCode);
		API api = getApi();
		api.confirmRegistration(request, mRegistrationConfirmCallback);
	}

	private void showInputForConfirmationCode() {
		String dialogTitle = getString(R.string.dialog_confirm_title);
		String dialogText = getString(R.string.dialog_confirm_text);
		String emptyInputMessage = getString(R.string.message_set_confirm_code);

		AlertDialog dialog = DialogHelper.showInputDialog(getActivity(), dialogTitle, dialogText, this, emptyInputMessage);
		dialog.show();
	}

	@Override
	public void onInputComplete(String userInput) {
		sendConfirmationRequest(userInput);
	}

	private void saveUserProfile(ResponseRegisterClient response) {
		MainActivity activity = getMainActivity();
		if (activity != null) {
			activity.showLoginFragment();
		}
	}

	@Override
	public void onValidationSucceeded() {
		mPhoneToRegister = mEditPhone.getText().toString().replaceAll("[\\(\\)\\-]", "");
		String password = mEditPassword.getText().toString();

		showProgressDialog(R.string.message_registration_in_progress);
		RequestRegisterClient request = RequestRegisterClient.create(mPhoneToRegister, password);
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
}
