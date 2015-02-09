package com.regall.old.fragments;

import java.util.HashSet;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import com.regall.R;
import com.regall.old.SmsReceiver;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestAddQueue;
import com.regall.old.network.request.RequestConfirmOrder;
import com.regall.old.network.response.ResponseAddQueue;
import com.regall.old.network.response.ResponseConfirmOrder;
import com.regall.old.network.response.ResponseGetOrganizations.Point;
import com.regall.old.network.response.ResponseGetOrganizations.Point.ServiceDescription;

public class ConfirmBookingFragment extends BaseFragment {
	
	private final static String ARGUMENT_DATE_TIME = "argument_datetime";
	private final static String ARGUMENT_SERVICES = "argument_services";
	private final static String ARGUMENT_POINT = "argument_point";
	private final static String ARGUMENT_OBJECT_ID = "argument_object_id";
	private final static String ARGUMENT_SERVICE_COST = "argument_service_cost";
	private final static String ARGUMENT_SERVICE_TIME = "argument_service_time";
	
	@Optional @InjectView(R.id.stubConfirmBooking) ViewStub mStubConfirmationLayout;
	
	@InjectView(R.id.textTime) TextView mTextTime;
	@InjectView(R.id.textAutowashAddress) TextView mTextAddress;
	@InjectView(R.id.textSelectedServices) TextView mTextServices;
	@InjectView(R.id.textAutowashTitle) TextView mTextTitle;
	@InjectView(R.id.textServiceDuration) TextView mTextDuration;
	@InjectView(R.id.textServiceCost) TextView mTextCost;
	
	@Optional @InjectView(R.id.editSmsCode) EditText mEditSmsCode;
	
	private int mQueueId;
	
	private int mObjectId;
	private Point mPoint;
	private String mDateTime;
	private HashSet<ServiceDescription> mServices;
	private String mServiceTime;
	private int mServiceCost;
	
	private BroadcastReceiver mReceiverConfirmation = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(SmsReceiver.ACTION_BOOKING_CONFIRMED)){
				showResultDialog(R.string.dialog_title_success, getString(R.string.dialog_confirm_success), true);
			}
		}
	};
	
	private Callback<ResponseAddQueue> mAddQueueCallback = new Callback<ResponseAddQueue>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseAddQueue response = (ResponseAddQueue) object;
			if(response.isSuccess()){
				mQueueId = response.getId();
				
				SharedPreferences preferences = getActivity().getSharedPreferences(SmsReceiver.PREFERENCES_BOOKING, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(SmsReceiver.PREFERENCES_BOOKING_QUEUE_ID, mQueueId);
				editor.apply();
				
				proceedRequestConfirmation();
			} else {
				showToast(response.getStatusDetail());
			}
		}
		
		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			logError(e.getMessage());
			showToast(e.getMessage());
		}
	};
	
	private Callback<ResponseConfirmOrder> mConfirmCallback = new Callback<ResponseConfirmOrder>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			showResultDialog(R.string.dialog_title_success, getString(R.string.dialog_confirm_success), true);
		}
		
		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			showResultDialog(R.string.dialog_title_error, e.getMessage(), false);
		}
		
	};
	
	private void showResultDialog(int title, String message, final boolean suggestAddEventToCalendar){
		LayoutInflater inflater = LayoutInflater.from(getActivity());

		View dialogView = inflater.inflate(R.layout.dialog_confirm, null, false);
		TextView dialogText = (TextView) dialogView.findViewById(R.id.text);
		dialogText.setText(message);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setView(dialogView);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(suggestAddEventToCalendar){
					getMainActivity().addEventToCalendar(mDateTime, mServiceTime, mPoint);
				}
				getMainActivity().resetBackStack();
			}
		});
		
		if(suggestAddEventToCalendar) {
			builder.setNegativeButton(android.R.string.no,  new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getMainActivity().resetBackStack();
				}
			});
		}
		
		builder.create().show();
	}
	
	public static ConfirmBookingFragment create(int objectId, Point point, String dateTime, HashSet<ServiceDescription> services, String serviceTime, int serviceCost){
		Bundle args = new Bundle();
		args.putInt(ARGUMENT_OBJECT_ID, objectId);
		args.putSerializable(ARGUMENT_POINT, point);
		args.putString(ARGUMENT_DATE_TIME, dateTime);
		args.putSerializable(ARGUMENT_SERVICES, services);
		args.putString(ARGUMENT_SERVICE_TIME, serviceTime);
		args.putInt(ARGUMENT_SERVICE_COST, serviceCost);
		
		ConfirmBookingFragment fragment = new ConfirmBookingFragment();
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_confirm_booking, container, false);
		ButterKnife.inject(this, view);
		getActivity().registerReceiver(mReceiverConfirmation, new IntentFilter(SmsReceiver.ACTION_BOOKING_CONFIRMED));
		return view;
	}
	
	@Override
	public void onDestroyView() {
		getActivity().unregisterReceiver(mReceiverConfirmation);
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle args = getArguments();
		mObjectId = args.getInt(ARGUMENT_OBJECT_ID);
		mPoint = (Point) args.getSerializable(ARGUMENT_POINT);
		mDateTime = args.getString(ARGUMENT_DATE_TIME);
		mServices = (HashSet<ServiceDescription>) args.getSerializable(ARGUMENT_SERVICES);
		mServiceTime = args.getString(ARGUMENT_SERVICE_TIME);
		mServiceCost = args.getInt(ARGUMENT_SERVICE_COST);
		
		String autowashTitle = getString(R.string.template_autowash, mPoint.getName());
		mTextTitle.setText(Html.fromHtml(autowashTitle));
		
		String autowashAddress = getString(R.string.template_address, mPoint.getAddress());
		mTextAddress.setText(Html.fromHtml(autowashAddress));
		
		String serviceList = getString(R.string.template_services, getSelectedServicesReadableString());
		mTextServices.setText(Html.fromHtml(serviceList));
		
		String requestTime = getString(R.string.template_time, mDateTime);
		mTextTime.setText(Html.fromHtml(requestTime));	
		
		String serviceDuration = getString(R.string.template_duration, mServiceTime);
		mTextDuration.setText(Html.fromHtml(serviceDuration));
		
		String serviceCost = getString(R.string.template_cost, mServiceCost);
		mTextCost.setText(Html.fromHtml(serviceCost));
	}

	@OnClick(R.id.buttonConfirm)
	void initRegistration(){
		RequestAddQueue request = RequestAddQueue.create(mObjectId, getSelectedServicesCodesString(), mPoint.getId(), mDateTime, getUser());
		
		showProgressDialog(R.string.message_confirming_in_progress);
		API api = getApi();
		api.requestBooking(request, mAddQueueCallback);
	}
	
	private void proceedRequestConfirmation(){
		mStubConfirmationLayout.inflate();
		ButterKnife.reset(this);
		ButterKnife.inject(this, getView());
		ButterKnife.findById(getView(), R.id.buttonConfirm).setVisibility(View.GONE);
	}

	private String getSelectedServicesCodesString(){
		StringBuilder builder = new StringBuilder();
		for(ServiceDescription service : mServices){
			builder.append(service.getId()).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	
	@Optional @OnClick(R.id.buttonOk)
	void completeConfirmation(){
		String smsCode = mEditSmsCode.getText().toString().trim();
		if(!smsCode.isEmpty()){
			sendConfirmMessage(mQueueId, smsCode);
		} else {
			showToast(R.string.message_illegal_sms_code);
		}
	}
	
	private void sendConfirmMessage(int queueId, String smsCode){
		RequestConfirmOrder request = new RequestConfirmOrder(queueId, smsCode);
		API api = getApi();
		showProgressDialog(R.string.message_confirming_order);
		api.confirmQuery(request, mConfirmCallback);
	}
	
	private String getSelectedServicesReadableString(){
		String delimiter = ", ";
		StringBuilder builder = new StringBuilder();
		for(ServiceDescription service : mServices){
			builder.append(service.getTitle()).append(delimiter);
		}
		builder.delete(builder.length() - delimiter.length(), builder.length());
		return builder.toString();
	}
}
