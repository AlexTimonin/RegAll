package com.regall.fragments;

import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.regall.R;
import com.regall.old.network.geocode.OnRouteCreated;
import com.regall.old.network.geocode.RouteCreator;
import com.regall.old.network.geocode.model.Bounds;
import com.regall.old.network.geocode.model.Route;

import java.util.ArrayList;

public class MapFragment extends SupportMapFragment {

	private static final String ARG_LAT_F = "arg_lat_from";
	private static final String ARG_LON_F = "arg_lon_from";

	private static final int BOUNDS_OFFSET = 50;

	private GoogleMap map;
	
	private double mLatitudeFrom;
	private double mLongitudeFrom;

	public static MapFragment create(double latFrom, double lonFrom){
		Bundle args = new Bundle();
		args.putDouble(ARG_LAT_F, latFrom);
		args.putDouble(ARG_LON_F, lonFrom);

		MapFragment fragment = new MapFragment();
		fragment.setArguments(args);

		return fragment;
	}

	private OnRouteCreated onRouteCreatedListener = new OnRouteCreated() {

		@Override
		public void onRouteCreated(Route route) {

			if (route == null || map == null) {
				return;
			}

			ArrayList<LatLng> points = route.getPolyline_points();

			if (points == null || points.size() <= 0) {
				return;
			}

			map.addPolyline(RouteCreator.createRoutePolyline(points));
			map.addMarker(RouteCreator.createRouteMarker(points.get(0), getString(R.string.label_start)));
			map.addMarker(RouteCreator.createRouteMarker(points.get(points.size() - 1), getString(R.string.label_end)));
			
			Bounds bounds = route.getBounds();

			if (bounds != null && bounds.getNortheast() != null && bounds.getSouthwest() != null) {
				map.moveCamera(CameraUpdateFactory.newLatLngBounds(RouteCreator.createRouteBounds(bounds), BOUNDS_OFFSET));
			}
		}
	};

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

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitudeFrom, mLongitudeFrom), 14.0f));
	}

}
