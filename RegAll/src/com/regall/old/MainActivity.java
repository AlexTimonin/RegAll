package com.regall.old;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.regall.R;
import com.regall.controllers.AutowashController;
import com.regall.fragments.ListFragment;
import com.regall.fragments.MapFragment;
import com.regall.old.db.DAOUserObject;
import com.regall.old.fragments.AddCarFragment;
import com.regall.old.fragments.AutowashListFragment;
import com.regall.old.fragments.BaseFragment;
import com.regall.old.fragments.CabinetFragment;
import com.regall.old.fragments.FilterFragment;
import com.regall.old.fragments.GeocodeFragment;
import com.regall.old.fragments.LoginFragment;
import com.regall.old.fragments.MainFragment;
import com.regall.old.fragments.OrdersHistoryFragment;
import com.regall.old.fragments.RecentAutowashesFragment;
import com.regall.old.fragments.RegistrationFragment;
import com.regall.old.fragments.SelectWashServiceFragment;
import com.regall.old.fragments.SelectWashTimeFragment;
import com.regall.old.model.AdditionalService;
import com.regall.old.model.AutowashFilter;
import com.regall.old.model.AutowashFilter.LocationFilter;
import com.regall.old.model.User;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.geocode.json.Result;
import com.regall.old.network.request.RequestGetUserObjects;
import com.regall.old.network.response.ResponseGetOrganizations.Point;
import com.regall.old.network.response.ResponseGetOrganizations.Point.ServiceDescription;
import com.regall.old.network.response.ResponseGetServices.Service;
import com.regall.old.network.response.ResponseGetUserObjects;
import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;
import com.regall.old.utils.DialogHelper;
import com.regall.old.utils.Logger;
import com.regall.utils.PrefUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@SuppressLint("InflateParams") 
public class MainActivity extends SherlockFragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	public final static String tag = MainActivity.class.getSimpleName();

	private final static int GOOGLE_PLAY_SERVICE_REQUEST = 9001;

	private API mApi;
	private ProgressDialog mProgressDialog;
	private AutowashFilter mCurrentAutowashFilter = new AutowashFilter(LocationFilter.CURRENT_LOCATION, new HashSet<Service>(), new HashSet<AdditionalService>());
	private User mUser;

	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private Location mLocation;
	private Location defaultLocation;

	private LocationListener mFragmentLocationListener;
	private boolean mNeedInitializeView;
	private AutowashController autowashController;
    private boolean isGPSEnabled = true;
    private boolean isMapShown;

	private Callback<ResponseGetUserObjects> mGetObjectsCallback = new Callback<ResponseGetUserObjects>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetUserObjects response = (ResponseGetUserObjects) object;
			if (response.isSuccess()) {
				List<ClientObject> clientObjects = response.getClientObjects();
				if(clientObjects != null){
					DAOUserObject dao = new DAOUserObject(MainActivity.this);
					dao.deleteAll();
					dao.insertAll(response.getClientObjects());
				}
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			hideProgressDialog();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		
		String os = System.getProperty("os.version");
		int sdk = Build.VERSION.SDK_INT;
		String deviceName = Build.DEVICE;
		String model = Build.MODEL;

		String installId = deviceName + " " + model + " " + os + "(" + sdk + ")";
		Crashlytics.setApplicationInstallationIdentifier(installId);

		setContentView(R.layout.activity_main);
		setupActionBar();

		mApi = new API(getString(R.string.server_url));
		mApi.debug();

		mProgressDialog = new ProgressDialog(this);

        defaultLocation = PrefUtils.getDefaultLocation(this);
        autowashController = new AutowashController(this);
        showMapFragment(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude(), 0, 0);
//        initializeState(getCurrentLocation());
    }

    private void initializeState(Location location){
        mUser = User.fromPreferences(this);

        if(mUser != null){
            showProgressDialog(R.string.message_receiving_user_objects);
            RequestGetUserObjects request = RequestGetUserObjects.create(mUser.getPhone());
            mApi.getUserObjects(request, mGetObjectsCallback);
        }
    }

    public Location getDefaultLocation() {
        return defaultLocation;
    }

    public Location getCurrentLocation() {
        return mLocation == null ? defaultLocation : mLocation;
    }

    public AutowashController getAutowashController() {
        return autowashController;
    }

    public AutowashFilter getCurrentAutowashFilter() {
        return mCurrentAutowashFilter;
    }
	
	private void setupLocationClient(){
		if(checkGoogleServiceAvailable() && isGPSEnabled){
			mLocationClient = new LocationClient(this, this, this);
			mLocationRequest = LocationRequest.create().setInterval(5000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setNumUpdates(1);
			mNeedInitializeView = true;
			if(mLocationClient.isConnected() || mLocationClient.isConnecting()){
				System.out.println("on init - is connected or connecting");
				mLocationClient.requestLocationUpdates(mLocationRequest, this);
			} else {
				System.out.println("on init - not connected");
				mLocationClient.connect();
			}
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.menuGps) != null) {
            menu.findItem(R.id.menuGps).setIcon(mLocationClient != null && (mLocationClient.isConnected() || mLocationClient.isConnecting()) && isGPSEnabled() ?
                    R.drawable.new_gps_on : R.drawable.new_gps_off);
        }
        if (menu.findItem(R.id.menuToggleMode) != null) {
            menu.findItem(R.id.menuToggleMode).setIcon(isMapShown ? R.drawable.new_list : R.drawable.new_map);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuGps:
                if (!isGPSEnabled()) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    break;
                }
                toggleLocationClientConnectionStatus();
                invalidateOptionsMenu();
                break;
            case R.id.menuToggleMode:
                if (isMapShown) {
                    showListFragment();
                } else {
                    showMapFragment(0, 0, 0, 0);
                }
                break;
            case android.R.id.home:
                if(mUser != null){
                    showCabinetFragment();
                } else {
                    // TODO suggest to register
                }
                break;
        }
        return true;
    }

    private boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void toggleLocationClientConnectionStatus() {
        if (mLocationClient == null) {
            setupLocationClient();
        }

        if (mLocationClient.isConnected() || mLocationClient.isConnecting()) {
            mLocationClient.disconnect();
        } else {
            mLocationClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupLocationClient();
        invalidateOptionsMenu();
    }

    @Override
	protected void onStop() {
		super.onStop();
        if (mLocation != null) {
            PrefUtils.saveDefaultLocation(this, mLocation);
        } else {
            PrefUtils.saveDefaultLocation(this, defaultLocation);
        }
		if (mLocationClient != null && mLocationClient.isConnected()) {
			System.out.println("Removing location updates");
			mLocationClient.removeLocationUpdates(this);
			mLocationClient.disconnect();
		}
	}

	private void setupActionBar() {
		ActionBar ab = getSupportActionBar();
		View customActionBar = getLayoutInflater().inflate(R.layout.action_bar_background, null);
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		ab.setCustomView(customActionBar, lp);

		ab.setLogo(R.drawable.logo);
		ab.setTitle(R.string.app_name);

		ab.setDisplayUseLogoEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
	}

	public void loadFragment(BaseFragment fragment, boolean addToBackStack) {
		hideProgressDialog();

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		transaction.replace(R.id.container, fragment, fragment.tag());
		if (addToBackStack) {
			transaction.addToBackStack(fragment.tag());
		}

		transaction.commit();
	}

	public void showMainFragment() {
		loadFragment(new MainFragment(), false);
	}

	public void showSearchFragment() {
		loadFragment(AutowashListFragment.create(mCurrentAutowashFilter), false);
	}

	public void showCabinetFragment() {
		loadFragment(new CabinetFragment(), true);
	}

	public void showOrdersHistoryFragment() {
		loadFragment(new OrdersHistoryFragment(), true);
	}

	public void showAutowashesFilter() {
		loadFragment(FilterFragment.create(mCurrentAutowashFilter), true);
	}

	public void showGeocodeFragment() {
		loadFragment(new GeocodeFragment(), true);
	}

	public void showLoginFragment() {
		loadFragment(new LoginFragment(), false);
	}

	public void showRegistrationFragment() {
		loadFragment(new RegistrationFragment(), true);
	}

	public void showMapFragment(double latFrom, double lonFrom, double latTo, double lonTo) {
		hideProgressDialog();

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		MapFragment map = MapFragment.create();
		
		transaction.replace(R.id.container, map, "map");
//		transaction.addToBackStack("map");

		transaction.commit();
        isMapShown = true;
        invalidateOptionsMenu();
	}

    public void showListFragment() {
        hideProgressDialog();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, new ListFragment(), "list");

        transaction.commit();
        isMapShown = false;
        invalidateOptionsMenu();
    }

	public API getApi() {
		return mApi;
	}

	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void showToast(int messageResourceId) {
		Toast.makeText(this, messageResourceId, Toast.LENGTH_LONG).show();
	}

	public void showSelectWashServiceFragment(Point organisation) {
		loadFragment(SelectWashServiceFragment.create(organisation), true);
	}

	public void showSelectWashTimeFragment(int objectId, String objectTitle, Point point, HashSet<ServiceDescription> selectedServices) {
		loadFragment(SelectWashTimeFragment.create(objectId, objectTitle, point, selectedServices), true);
	}

	public void showRecentAutowashes() {
		loadFragment(new RecentAutowashesFragment(), true);
	}

	public void showNewCardFragment() {
		loadFragment(new AddCarFragment(), true);
	}

	public void showProgressDialog(int messageId) {
		hideProgressDialog();
		mProgressDialog.setMessage(getString(messageId));
		mProgressDialog.show();
	}

	public void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.hide();
		}
	}

	public void onGeopointSelectedAsFilter(Result geocodeResult) {
		FilterFragment fragment = getFragment(FilterFragment.class);
		if (fragment != null) {
			FilterFragment target = (FilterFragment) fragment;
			target.setLocationFilter(geocodeResult);
		} else {
			showToast(R.string.message_error_deliver_result_location);
		}

		getSupportFragmentManager().popBackStack();
	}

	public void applyAutowashFilter(AutowashFilter newFilter) {
		mCurrentAutowashFilter = newFilter;
		AutowashListFragment fragment = getFragment(AutowashListFragment.class);
		if (fragment != null) {
			fragment.applyFilter(newFilter);
		} else {
			showToast(R.string.message_error_apply_filter);
		}

		getSupportFragmentManager().popBackStack();
	}

	@SuppressWarnings("unchecked")
	public <T> T getFragment(Class<?> fragmentClass) {
		FragmentManager fManager = getSupportFragmentManager();
		Fragment fragment = fManager.findFragmentByTag(fragmentClass.getSimpleName());
		if (fragmentClass.isInstance(fragment)) {
			return (T) fragment;
		} else {
			return null;
		}
	}

	public void setUser(User user) {
		this.mUser = user;
	}

	public User getUser() {
		return mUser;
	}

	public void addCarToUserProfile() {
		CabinetFragment fragment = getFragment(CabinetFragment.class);
		if(fragment != null){
			fragment.setNeedUpdateUserObjects(true);
		}
		getSupportFragmentManager().popBackStack();
	}

	public void updateLocation(LocationListener locationListener) {
		Logger.logDebug(tag, "updateLocation");
		
		String locationMode = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		Logger.logDebug(tag, "Location mode - " + locationMode);
		Log.i(tag, "Location mode - " + locationMode);
		
		if(locationMode == null || locationMode.isEmpty()){
			showEnableLocationSettingsDialog();
			return;
		}
		
		if (mLocation != null) {
			Logger.logDebug(tag, "updateLocation - old exists");
			locationListener.onLocationChanged(mLocation);
		} else {
			Logger.logDebug(tag, "updateLocation - not initialized, request for updates");
			mFragmentLocationListener = locationListener;
			
			if (!mLocationClient.isConnected() && checkGoogleServiceAvailable()) {
				mLocationClient.connect();
			}
		}
	}

	public boolean checkGoogleServiceAvailable() {
		Logger.logDebug(tag, "checkGooglePlayServiceAvailable");
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, GOOGLE_PLAY_SERVICE_REQUEST).show();
			} else {
				showToast(R.string.message_some_functions_unavailable);
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
        invalidateOptionsMenu();
		Logger.logDebug(tag, "onConnectionFailed - " + arg0.getErrorCode());
	}

	@Override
	public void onConnected(Bundle arg0) {
		Logger.logDebug(tag, "onConnected");
		Location lastLocation = mLocationClient.getLastLocation();
		if (lastLocation != null) {
			Logger.logDebug(tag, "last location is not null");
			mLocation = lastLocation;
		}

		Logger.logDebug(tag, "requesting location updates");
		mLocationRequest = LocationRequest.create().setInterval(5000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setNumUpdates(1);
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
        invalidateOptionsMenu();
	}

	@Override
	public void onDisconnected() {
		Logger.logDebug(tag, "on disconnected");
		mLocationClient.removeLocationUpdates(this);
        invalidateOptionsMenu();
	}

	@Override
	public void onLocationChanged(Location location) {
		Logger.logDebug(tag, "on location changed");
		mLocation = location;
		
		if(mNeedInitializeView){
            //TODO first location update happened
			mNeedInitializeView = false;
		}

//		if (mFragmentLocationListener != null) {
//			mFragmentLocationListener.onLocationChanged(location);
//			mFragmentLocationListener = null;
//		}
	}

	public void enableProfileButton() {
		ActionBar ab = getSupportActionBar();
		ab.setDisplayShowHomeEnabled(true);
		ab.setLogo(R.drawable.profile);
	}

	public void disableProfileButton() {
		ActionBar ab = getSupportActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setLogo(null);
	}
	
	public void resetBackStack(){
		clearBackStack();
		loadFragment(AutowashListFragment.create(mCurrentAutowashFilter), false);
	}
	
	private void clearBackStack(){
		FragmentManager fManager = getSupportFragmentManager();
		for(int i = 0, max = fManager.getBackStackEntryCount(); i < max; i++){
			fManager.popBackStack();
		}
	}
	
	public void logout(){
		if(mUser != null){
			mUser.logout(this);
		}
		finish();
	}
	
	private void showEnableLocationSettingsDialog(){
		Dialog dialog = DialogHelper.createConfirmDialog(this, R.string.dialog_title_error, getString(R.string.message_no_location_services_enabled), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		});
		
		dialog.show();
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) public void addEventToCalendar(String when, String duration, Point point){
		String dateFormat = "dd.MM.yyyy HH:mm";
		String durationFormat = "HH:mm:ss";
		
		SimpleDateFormat formatterWhen = new SimpleDateFormat(dateFormat);
		SimpleDateFormat formatterDuration = new SimpleDateFormat(durationFormat);
		
		Calendar whenCalendar = null;
		Calendar overCalendar = null;
		
		try {
			Date whenDate = formatterWhen.parse(when);
			Date durationDate = formatterDuration.parse(duration);
			
			whenCalendar = Calendar.getInstance();
			whenCalendar.setTime(whenDate);
			
			overCalendar = Calendar.getInstance();
			overCalendar.setTime(durationDate);
			
			overCalendar.add(Calendar.HOUR_OF_DAY, whenCalendar.get(Calendar.HOUR_OF_DAY));
			overCalendar.add(Calendar.MINUTE, whenCalendar.get(Calendar.MINUTE));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setData(Events.CONTENT_URI);
		
		if(whenCalendar != null && overCalendar != null) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, whenCalendar.getTimeInMillis());
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, overCalendar.getTimeInMillis());
		}
		
		intent.putExtra(Events.TITLE, R.string.reminder_title);
		intent.putExtra(Events.EVENT_LOCATION, point.getAddress());
		
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e){
			intent.setAction(Intent.ACTION_EDIT);
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e1) {
				showToast(R.string.message_no_calendar);
			}
		}
		
	}
}
