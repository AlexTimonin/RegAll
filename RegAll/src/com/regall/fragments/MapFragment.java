package com.regall.fragments;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.regall.R;
import com.regall.controllers.AutowashController;
import com.regall.controllers.MapPinController;
import com.regall.old.MainActivity;
import com.regall.old.model.AutowashFilter;
import com.regall.old.network.Callback;
import com.regall.old.network.response.ResponseGetOrganizations;

public class MapFragment extends SupportMapFragment implements GoogleMap.OnCameraChangeListener, View.OnClickListener, TextWatcher {

	private GoogleMap map;
    private MapPinController mapPinController;

	public static MapFragment create(){
		Bundle args = new Bundle();
		MapFragment fragment = new MapFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		map = getMap();
		map.setMyLocationEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraChangeListener(this);

        mapPinController = new MapPinController(getActivity(), map);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()), 14.0f));
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mapView = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getActivity()).inflate(R.layout.new_search, root);
        root.addView(mapView, new LinearLayout.LayoutParams(-1, -1));
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.searchButton).setOnClickListener(this);
        ((EditText) view.findViewById(R.id.searchEditText)).removeTextChangedListener(this);
        ((EditText) view.findViewById(R.id.searchEditText)).setText(getCurrentAutowashFilter().getSearchKey());
        ((EditText) view.findViewById(R.id.searchEditText)).addTextChangedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getAutowashController().loadOrganizations(0, getCurrentAutowashFilter(), callback);
    }

    private Callback<ResponseGetOrganizations> callback = new Callback<ResponseGetOrganizations>() {
        @Override
        public void success(Object object) {
            mapPinController.swap(getAutowashController().getOrganisations());
        }

        @Override
        public void failure(Exception e) {}
    };

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    private AutowashController getAutowashController() {
        return getMainActivity().getAutowashController();
    }

    private Location getCurrentLocation() {
        return  getMainActivity().getCurrentLocation();
    }

    private Location getDefaultLocation() {
        return  getMainActivity().getDefaultLocation();
    }

    private AutowashFilter getCurrentAutowashFilter() {
        return getMainActivity().getCurrentAutowashFilter();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        getDefaultLocation().setLatitude(cameraPosition.target.latitude);
        getDefaultLocation().setLongitude(cameraPosition.target.longitude);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                getAutowashController().loadOrganizations(0, getCurrentAutowashFilter(), callback);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (getCurrentAutowashFilter() != null) {
            getCurrentAutowashFilter().setSearchKey(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}
}
