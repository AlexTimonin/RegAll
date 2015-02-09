package com.regall.old.fragments;

import java.util.HashSet;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.regall.R;
import com.regall.old.adapters.AdapterSpinnerCar;
import com.regall.old.adapters.SelectServiceAdapter;
import com.regall.old.db.DAOUserObject;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestGetUserObjects;
import com.regall.old.network.response.ResponseGetOrganizations.Point;
import com.regall.old.network.response.ResponseGetOrganizations.Point.ServiceDescription;
import com.regall.old.network.response.ResponseGetUserObjects;
import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;

public class SelectWashServiceFragment extends BaseFragment {
	
	private final static String ARG_ORGANISATION = "organisation";
	
	@InjectView(R.id.textAutowashTitle) TextView mTextAutowashTitle;
	@InjectView(R.id.textAutowashAddress) TextView mTextAutowashAddress;
	@InjectView(R.id.spinnerCar) Spinner mSpinnerCar;
	@InjectView(R.id.gridServices) GridView mGridServices;

	private Point mOrganisation;
	private HashSet<ServiceDescription> mSelectedServices = new HashSet<ServiceDescription>();
	
	private Callback<ResponseGetUserObjects> mGetObjectsCallback = new Callback<ResponseGetUserObjects>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetUserObjects response = (ResponseGetUserObjects) object;
			if (response.isSuccess()) {
				List<ClientObject> clientObjects = response.getClientObjects();
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
	
	private OnCheckedChangeListener mServiceSelectedListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			ServiceDescription service = (ServiceDescription) buttonView.getTag();
			if(isChecked) {
				mSelectedServices.add(service);
			} else {
				mSelectedServices.remove(service);
			}
		}
	};

	public static SelectWashServiceFragment create(Point organisation){
		Bundle args = new Bundle();
		args.putSerializable(ARG_ORGANISATION, organisation);
		
		SelectWashServiceFragment fragment = new SelectWashServiceFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_select_wash_service, container, false);
		ButterKnife.inject(this, view);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		DAOUserObject dao = new DAOUserObject(getActivity());
		List<ClientObject> objects = dao.getAll();
		if(!objects.isEmpty()){
			setupUserObjects(objects);
		} else {
			RequestGetUserObjects request = RequestGetUserObjects.create(getUser().getPhone());
			showProgressDialog(R.string.message_receiving_user_objects);
			API api = getApi();
			api.getUserObjects(request, mGetObjectsCallback);
		}
		
		Bundle args = getArguments();
		if(args != null) {
			mOrganisation = (Point) args.getSerializable(ARG_ORGANISATION);
			setupContent(mOrganisation);
		} else {
			throw new IllegalStateException("Fragment should be provided with Organisation object to display!");
		}
	}
	
	private void setupUserObjects(List<ClientObject> objects){
		AdapterSpinnerCar adapter = new AdapterSpinnerCar(objects);
		if(mSpinnerCar != null){
			mSpinnerCar.setAdapter(adapter);
		}
	}

	private void setupContent(Point organisation){
		mTextAutowashTitle.setText(organisation.getName());
		mTextAutowashAddress.setText(organisation.getAddress());
		SelectServiceAdapter adapter = new SelectServiceAdapter(organisation.getServices(), mSelectedServices, mServiceSelectedListener);
		mGridServices.setAdapter(adapter);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.buttonNext)
	void showWashTimeOrder() {
		ClientObject object = (ClientObject) mSpinnerCar.getSelectedItem();
		if(!mSelectedServices.isEmpty()){
			showSelectWashTimeFragment(object.getObjectId(), object.getObjectName(), mOrganisation, mSelectedServices);
		} else {
			showToast(R.string.message_no_service_selected);
		}
	}

}
