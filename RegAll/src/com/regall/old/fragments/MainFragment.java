package com.regall.old.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.regall.R;

public class MainFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this, view);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.buttonLogin)
	void toLoginScreen(){
		showLoginFragment();
	}
	
	@OnClick(R.id.buttonRegister)
	void showRegistration(){
		showRegistrationFragment();
	}
}
