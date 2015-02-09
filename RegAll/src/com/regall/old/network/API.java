package com.regall.old.network;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.net.URLConnection;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.regall.old.model.User;
import com.regall.old.network.request.RequestAddQueue;
import com.regall.old.network.request.RequestCancelOrder;
import com.regall.old.network.request.RequestConfirmOrder;
import com.regall.old.network.request.RequestConfirmRegistration;
import com.regall.old.network.request.RequestDeleteCar;
import com.regall.old.network.request.RequestGetCarMarks;
import com.regall.old.network.request.RequestGetCarModels;
import com.regall.old.network.request.RequestGetCities;
import com.regall.old.network.request.RequestGetClientBookings;
import com.regall.old.network.request.RequestGetFreeTime;
import com.regall.old.network.request.RequestGetOrganizations;
import com.regall.old.network.request.RequestGetRecentAutowashes;
import com.regall.old.network.request.RequestGetServices;
import com.regall.old.network.request.RequestGetUserObjects;
import com.regall.old.network.request.RequestLogin;
import com.regall.old.network.request.RequestRegisterClient;
import com.regall.old.network.request.RequestSaveUserObject;

public class API {

	private final static String mTag = API.class.getSimpleName();

	private static boolean mDebug;

	private String mUrl;

	public API(String mUrl) {
		this.mUrl = mUrl;
	}

	public void debug() {
		mDebug = true;
	}

	public void deleteCar(RequestDeleteCar request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getCities(RequestGetCities request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void login(RequestLogin request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getRecentAutowashes(RequestGetRecentAutowashes request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void cancelQuery(RequestCancelOrder request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void confirmQuery(RequestConfirmOrder request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getBookingsHistory(RequestGetClientBookings request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void requestBooking(RequestAddQueue request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getUserObjects(RequestGetUserObjects request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void registerNewClient(RequestRegisterClient request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void confirmRegistration(RequestConfirmRegistration request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getCarMarks(RequestGetCarMarks request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getCarModels(RequestGetCarModels request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getOrganisations(RequestGetOrganizations request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void saveUserObject(RequestSaveUserObject request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	public void getServices(Callback<?> callback) {
		RequestGetServices request = new RequestGetServices();
		prepareAndRunTask(request, callback);
	}

	public void getFreeTime(RequestGetFreeTime request, Callback<?> callback) {
		prepareAndRunTask(request, callback);
	}

	private void prepareAndRunTask(Object requestBody, Callback<?> callback) {
		RequestTask task = new RequestTask(mUrl, requestBody, callback);
		task.execute();
	}

	private static class RequestTask extends AsyncTask<Void, Void, Object> {

		private String mUrl;
		private Object mRequestBody;
		private Class<?> mResponseType;
		private Callback<?> mCallback;

		public RequestTask(String mUrl, Object mRequestBody, Callback<?> mCallback) {
			this.mUrl = mUrl;
			this.mRequestBody = mRequestBody;
			this.mResponseType = (Class<?>) ((ParameterizedType) mCallback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
			this.mCallback = mCallback;
		}

//		@Override
//		protected Object doInBackground(Void... params) {
//
//			try {
//				Serializer serializer = new Persister();
//				HttpClient client = new DefaultHttpClient();
//				HttpPost request = new HttpPost(mUrl);
//
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				serializer.write(mRequestBody, baos);
//				String requestString = new String(baos.toByteArray());
//
//				if (mDebug) {
//					logRequest(baos, requestString);
//				}
//
//				request.setEntity(new StringEntity(requestString));
//
//				HttpResponse response = client.execute(request);
//				String responseString = EntityUtils.toString(response.getEntity());
//
//				if (mDebug) {
//					logResponse(responseString);
//				}
//
//				BufferedReader zis = new BufferedReader(new InputStreamReader(new ZipInputStream(new ByteArrayInputStream(responseString.getBytes()))));
//				StringBuilder builder = new StringBuilder();
//				String line = null;
//				while ((line = zis.readLine()) != null) {
//					builder.append(line);
//				}
//
//				String unzippedResponse = builder.toString();
//
//				if (mDebug) {
//					logResponse(unzippedResponse);
//				}
//
//				return serializer.read(mResponseType, unzippedResponse);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return null;
//		}

		private void logRequest(ByteArrayOutputStream baos, String requestString) throws IOException {
			Log.d(mTag, "// --- SENDING HTTP REQUEST --- //");
			Log.d(mTag, requestString);
			Crashlytics.log(1, String.valueOf(User.PHONE), requestString);
			Log.d(mTag, "// --- END OF HTTP REQUEST --- //");
			baos.close();
		}

		private void logResponse(String response) {
			Log.i(mTag, "// --- <RESPONSE> --- //");
			Log.i(mTag, response);
			Log.i(mTag, "// --- </RESPONSE> --- //");
		}

		@Override
		protected Object doInBackground(Void... params) {
			Serializer serializer = new Persister();
			try {
				URL url = new URL(mUrl);
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("Accept-Encoding", "identity");
				conn.setDoOutput(true);

				if (mDebug) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					serializer.write(mRequestBody, baos);
					String requestString = new String(baos.toByteArray());
					Log.d(mTag, "// --- SENDING HTTP REQUEST --- //");
					Log.d(mTag, requestString);
					Crashlytics.log(1, String.valueOf(User.PHONE), requestString);
					Log.d(mTag, "// --- END OF HTTP REQUEST --- //");
					baos.close();
				}

				OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
				serializer.write(mRequestBody, writer);
				writer.close();

				StringBuilder builder = new StringBuilder();
				InputStream responseStream = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					if (!reader.ready()) {
						break;
					}
				}

				responseStream.close();
				String responseString = builder.toString();

				if (mDebug) {
					Log.d(mTag, responseString);
					Crashlytics.log(1, String.valueOf(User.PHONE), responseString);
				}

				return serializer.read(mResponseType, responseString);
			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			if (!(result instanceof Exception)) {
				mCallback.success(result);
			} else {
				mCallback.failure((Exception) result);
			}
		}

	}
}
