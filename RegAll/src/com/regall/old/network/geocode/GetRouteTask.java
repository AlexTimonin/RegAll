package com.regall.old.network.geocode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.regall.old.network.geocode.model.Polyline;
import com.regall.old.network.geocode.model.Route;
import com.regall.old.network.geocode.model.Routes;

public class GetRouteTask extends AsyncTask<Void, Void, Route> {

	private static final String ROUTE_URL = "http://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&mode=driving&sensor=false";

	private OnRouteCreated onRoutedCreatedListener;

	private ProgressDialog progress;

	private String errorMsg;
	
	private double mLatitudeFrom;
	private double mLongitudeFrom;

	private double mLatitudeTo;
	private double mLongitudeTo;
	
	public GetRouteTask(Activity a, String msg, String errorMsg, OnRouteCreated onRoutedCreatedListener, double latFrom, double lonFrom, double latTo, double lonTo) {
		if (a != null) {
			progress = new ProgressDialog(a);
			progress.setCancelable(false);
			progress.getWindow().setGravity(Gravity.CENTER);
			progress.setMessage(msg);
		}

		this.errorMsg = errorMsg;
		this.onRoutedCreatedListener = onRoutedCreatedListener;
		
		mLatitudeFrom = latFrom;
		mLongitudeFrom = lonFrom;
		
		mLatitudeTo = latTo;
		mLongitudeTo = lonTo;
	}

	@Override
	protected void onPreExecute() {
		showProgress();
	}

	@Override
	protected Route doInBackground(Void... params) {
		try {
			URL url = new URL(String.format(ROUTE_URL, mLatitudeFrom, mLongitudeFrom, mLatitudeTo, mLongitudeTo));
			InputStream in = url.openConnection().getInputStream();
			Reader rd = new InputStreamReader(in, "UTF-8");
			Routes routes = new Gson().fromJson(rd, Routes.class);
			Log.e("zzz", new Gson().toJson(routes));

			if (routes == null || routes.getRoutes() == null || routes.getRoutes().size() <= 0) {
				return null;
			}

			Route route = routes.getRoutes().get(0);

			if (route == null || route.getOverview_polyline() == null) {
				return null;
			}

			Polyline path = route.getOverview_polyline();

			String points = path.getPoints();

			if (TextUtils.isEmpty(points)) {
				return null;
			}

			ArrayList<LatLng> geoPoints = RoutePolylineDecoder.decodePoly(points);
			if (geoPoints == null || geoPoints.size() <= 0) {
				return null;
			}

			route.setPolyline_points(geoPoints);
			return route;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Route result) {
		if (result == null) {
			Toast.makeText(progress.getContext(), errorMsg, Toast.LENGTH_SHORT).show();
		}

		else if (onRoutedCreatedListener != null) {
			onRoutedCreatedListener.onRouteCreated(result);
		}
		hideProgress();
	}

	@Override
	protected void onCancelled() {
		hideProgress();
	}

	@Override
	protected void onCancelled(Route result) {
		hideProgress();
	}

	protected void showProgress() {
		if (progress != null) {
			progress.show();
		}
	}

	protected void hideProgress() {
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}

}