package com.regall.old.fragments;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.regall.R;
import com.regall.old.adapters.TimeAdapter;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestGetFreeTime;
import com.regall.old.network.response.ResponseGetFreeTime;
import com.regall.old.network.response.ResponseGetFreeTime.Time;
import com.regall.old.network.response.ResponseGetOrganizations.Point;
import com.regall.old.network.response.ResponseGetOrganizations.Point.ServiceDescription;

public class SelectWashTimeFragment extends BaseFragment implements OnCheckedChangeListener {

	private final static String ARG_OBJECT_ID = "object_id";
	private final static String ARG_POINT = "point_id";
	private final static String ARG_SERVICES = "services";

	@InjectView(R.id.gridTime) GridView mGridTime;
	@InjectView(R.id.textDay) TextView mTextDay;
	@InjectView(R.id.textMonth) TextView mTextMonth;
	
	@InjectView(R.id.buttonGoToTomorrow) ImageButton mButtonTomorrow;
	@InjectView(R.id.buttonGoToYesterday) ImageButton mButtonYesterday;
	
	private RadioGroup mTimeRadioGroup;
	
	private int mObjectId;
	private Point mPoint;
	private HashSet<ServiceDescription> mServices;
	private Time mSelectedTime;
	
	private Calendar mToday = Calendar.getInstance();
	private Calendar mCurrentSelected = Calendar.getInstance();
	
	private final static String FORMAT_DAY = "dd";
	private final static String FORMAT_MONTH = "MMMM";
	
	private final static String FORMAT_BACKEND = "dd.MM.yyyy";
	
	private String mDay;
	private String mMonth;
	
	private String mDateBackend;
	
	private int mServiceCost;
	private String mServiceTime;
	
	private Callback<ResponseGetFreeTime> mCallbackFreeTime = new Callback<ResponseGetFreeTime>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			
			ResponseGetFreeTime response = (ResponseGetFreeTime) object;
			
			if(response.isSuccess() && mGridTime != null){
				List<Time> freeTime = response.getAvailableTime();
				if(freeTime != null && freeTime.size() > 0){
					mServiceCost = response.getServiceCost();
					mServiceTime = response.getServiceTime();
					TimeAdapter adapter = new TimeAdapter(freeTime, mTimeRadioGroup, SelectWashTimeFragment.this);
					mGridTime.setAdapter(adapter);
				} else {
					showToast(R.string.message_no_available_time);
				}
			} else {
				showToast(response.getStatusDetail());
			}
		}

		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			logError(e.getMessage());;
			showToast(e.getMessage());
		}
	};
	
	public static SelectWashTimeFragment create(int objectId, String objectTitle, Point point, HashSet<ServiceDescription> selectedServices) {
		Bundle args = new Bundle();
		args.putInt(ARG_OBJECT_ID, objectId);
		args.putSerializable(ARG_POINT, point);
		args.putSerializable(ARG_SERVICES, selectedServices);

		SelectWashTimeFragment fragment = new SelectWashTimeFragment();
		fragment.setArguments(args);

		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_select_wash_time, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mTimeRadioGroup = new RadioGroup(getActivity());

		Bundle args = getArguments();
		if (args != null) {
			mObjectId = args.getInt(ARG_OBJECT_ID);
			mPoint = (Point) args.getSerializable(ARG_POINT);
			mServices = (HashSet<ServiceDescription>) args.getSerializable(ARG_SERVICES);
		} else {
			throw new IllegalStateException("Fragment should be provided with PoinId and Services info!");
		}

		changeDate(0);
	}

	private void loadFreeTime(String date) {
		String serviceString = getSelectedServicesCodesString();
		RequestGetFreeTime request = new RequestGetFreeTime(mObjectId, serviceString, mPoint.getId(), date);

		showProgressDialog(R.string.message_get_free_time);
		
		API api = getApi();
		api.getFreeTime(request, mCallbackFreeTime);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
		mTimeRadioGroup = null;
	}

	@OnClick(R.id.buttonNext)
	void showConfirmOrder() {
		
		if(mSelectedTime != null){
			String dateTime = mDateBackend + " " + mSelectedTime.getValue();
			ConfirmBookingFragment fragment = ConfirmBookingFragment.create(mObjectId, mPoint, dateTime, mServices, mServiceTime, mServiceCost);
			getMainActivity().loadFragment(fragment, true);
		} else {
			showToast(R.string.message_set_a_time);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Time time = (Time) buttonView.getTag();
		if(isChecked){
			mSelectedTime = time;
		}
	}
	
	@OnClick(R.id.buttonGoToTomorrow)
	void nextDay(){
		changeDate(1);
	}
	
	@OnClick(R.id.buttonGoToYesterday)
	void previousDay(){
		changeDate(-1);
	}
	
	private void changeDate(int delta){
		mCurrentSelected.add(Calendar.DAY_OF_MONTH, delta);
		mButtonYesterday.setEnabled(mCurrentSelected.after(mToday));
		
		mDay = DateFormat.format(FORMAT_DAY, mCurrentSelected).toString();
		mMonth = DateFormat.format(FORMAT_MONTH, mCurrentSelected).toString();
		
		mTextDay.setText(mDay);
		mTextMonth.setText(mMonth);
		
		mDateBackend = DateFormat.format(FORMAT_BACKEND, mCurrentSelected).toString();
		loadFreeTime(mDateBackend);
	}
	
	private String getSelectedServicesCodesString(){
		StringBuilder builder = new StringBuilder();
		for(ServiceDescription service : mServices){
			builder.append(service.getId()).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
}
