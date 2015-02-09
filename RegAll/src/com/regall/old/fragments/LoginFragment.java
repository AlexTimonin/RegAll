package com.regall.old.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.regall.old.MainActivity;
import com.regall.R;
import com.regall.old.model.User;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestLogin;
import com.regall.old.network.response.ResponseLogin;

public class LoginFragment extends BaseFragment {

	@InjectView(R.id.editPhone) EditText mEditPhone;
	@InjectView(R.id.editPassword) EditText mEditPassword;
	@InjectView(R.id.editConfirmPassword) EditText mEditPasswordConfirm;
	
	private String mPhone;
	
	private Callback<ResponseLogin> mCallbackLogin = new Callback<ResponseLogin>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseLogin response = (ResponseLogin) object;
			if(response.isSuccess()){
				saveUserProfile(response);
				showSearchFragment();
			} else {
				showToast(response.getStatusDetail());
			}
		}
		
		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			logError(e.getMessage());
			showToast(e.getMessage());
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_registration_new, container, false);
		ButterKnife.inject(this, view);
		mEditPasswordConfirm.setVisibility(View.GONE);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.buttonRegister)
	void login(){
		mPhone = mEditPhone.getText().toString().replaceAll("[\\(\\)-]", "");
		String password = mEditPassword.getText().toString();
		
		showProgressDialog(R.string.message_logging_in);
		RequestLogin request = RequestLogin.create(mPhone, password);
		getApi().login(request, mCallbackLogin);
	}
	
	private void saveUserProfile(ResponseLogin response) {
		int id = response.getUserId();
		String session = response.getSession();
		User user = new User(id, mPhone, "", session);
		MainActivity activity = getMainActivity();
		if (activity != null) {
			user.save(activity);
			activity.setUser(user);
		}
	}
	
}
