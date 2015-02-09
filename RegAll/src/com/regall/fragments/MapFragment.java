package com.regall.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.regall.R;
import com.regall.controllers.MapPinController;
import com.regall.old.model.AutowashFilter;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestGetOrganizations;
import com.regall.old.network.response.ResponseGetOrganizations;

public class MapFragment extends SupportMapFragment {

	private static final String ARG_LAT_F = "arg_lat_from";
	private static final String ARG_LON_F = "arg_lon_from";

	private static final int BOUNDS_OFFSET = 50;

	private GoogleMap map;
    private MapPinController mapPinController;
    private ProgressDialog progressDialog;
    private AutowashFilter filter;
	
	private double mLatitudeFrom;
	private double mLongitudeFrom;

	public static MapFragment create(double latFrom, double lonFrom, AutowashFilter filter){
		Bundle args = new Bundle();
		args.putDouble(ARG_LAT_F, latFrom);
		args.putDouble(ARG_LON_F, lonFrom);
        args.putSerializable("filter", filter);

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

		Bundle args = getArguments();

		mLatitudeFrom = args.getDouble(ARG_LAT_F);
		mLongitudeFrom = args.getDouble(ARG_LON_F);
        filter = (AutowashFilter) args.getSerializable("filter");

        progressDialog = new ProgressDialog(getActivity());
        mapPinController = new MapPinController(getActivity(), map);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitudeFrom, mLongitudeFrom), 14.0f));
	}

    @Override
    public void onStart() {
        super.onStart();
        API api = new API(getString(R.string.server_url));
        showProgressDialog(R.string.message_loading);
        api.getOrganisations(RequestGetOrganizations.byFilter(filter, 1, 0), callback);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private Callback<ResponseGetOrganizations> callback = new Callback<ResponseGetOrganizations>() {
        @Override
        public void success(Object object) {
            ResponseGetOrganizations response = (ResponseGetOrganizations) object;
            if (response.isSuccess()) {
                mapPinController.swap(response.getOrganisations());
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_loading_data), Toast.LENGTH_LONG).show();
            }
            hideProgressDialog();
        }

        @Override
        public void failure(Exception e) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.error_loading_data), Toast.LENGTH_LONG).show();
            hideProgressDialog();
        }
    };

    public void showProgressDialog(int messageId) {
        hideProgressDialog();
        progressDialog.setMessage(getString(messageId));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
