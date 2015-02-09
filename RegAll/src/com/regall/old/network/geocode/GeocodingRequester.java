package com.regall.old.network.geocode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.regall.old.network.geocode.json.GeocodeResponse;

public class GeocodingRequester {

	public final static String STATUS_OK = "OK";
	public final static String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
	public final static String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
	public final static String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
	public final static String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
	
	private final static String URL_PATTERN = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&language=ru&components=country:UA";
	
	private GeocodeListener mGeocodeListener;
	
	private SearchTask mSearchTask;
	
	public GeocodingRequester(GeocodeListener mGeocodeListener) {
		this.mGeocodeListener = mGeocodeListener;
	}

	public void search(String query){
		if(mSearchTask != null && mSearchTask.isInProgress()){
			mSearchTask.cancel(true);
		}
		
		String url = Uri.parse(URL_PATTERN).buildUpon().appendQueryParameter("address", query).build().toString();
		
		mSearchTask = new SearchTask(mGeocodeListener);
		mSearchTask.execute(url);
	}
	
	// =========================================================================================
	// inner classes
	// =========================================================================================
	
	private static class SearchTask extends AsyncTask<String, Void, Object> {
		
		private GeocodeListener mGeocodeListener;
		
		private boolean inProgress;
		
		public SearchTask(GeocodeListener geocodeListener) {
			this.mGeocodeListener = geocodeListener;
		}

		@Override
		protected void onPreExecute() {
			mGeocodeListener.beforeRequest();
			inProgress = true;
		}

		@Override
		protected Object doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);
			try {
				HttpResponse response = httpClient.execute(request);
				String responseString = EntityUtils.toString(response.getEntity(), "utf8");
				return new Gson().fromJson(responseString, GeocodeResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			inProgress = false;
			
			if(result instanceof GeocodeResponse){
				mGeocodeListener.onRequestCompleted((GeocodeResponse) result);
			} else {
				mGeocodeListener.onRequestFailed((Exception) result);
			}
		}

		public boolean isInProgress() {
			return inProgress;
		}
	}
	
	public interface GeocodeListener {
		void beforeRequest();
		void onRequestCompleted(GeocodeResponse response);
		void onRequestFailed(Exception e);
	}
}
