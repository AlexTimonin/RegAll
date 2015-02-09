package com.regall.old.fragments;

import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.android.gms.location.LocationListener;
import com.regall.R;
import com.regall.old.adapters.BookingHistoryAdapter;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestCancelOrder;
import com.regall.old.network.request.RequestGetClientBookings;
import com.regall.old.network.response.BasicResponse;
import com.regall.old.network.response.ResponseGetClientBookings;
import com.regall.old.network.response.ResponseGetClientBookings.BookingRecord;
import com.regall.old.utils.DialogHelper;
import com.regall.old.utils.TelephonyHelper;

public class OrdersHistoryFragment extends BaseFragment {

	@InjectView(android.R.id.list)
	ListView mOrdersList;

	private OnClickListener mOnCallClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			BookingRecord bookingRecord = (BookingRecord) v.getTag();
			
			if(TelephonyHelper.isTelephonySupported(getActivity())){
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + bookingRecord.getPointPhone()));
				startActivity(intent);
			} else {
				showToast(R.string.message_telephony_is_not_supported);
			}
		}
	};
	
	private Callback<BasicResponse> mCancelQueryCallback = new Callback<BasicResponse>() {
		
		@Override
		public void success(Object object) {
			hideProgressDialog();
			BasicResponse response = (BasicResponse) object;
			if(response.isSuccess()){
				showToast(R.string.message_order_cancelled);
				getHistory();
			} else {
				showToast(response.getStatusDetail());
			}
		}
		
		@Override
		public void failure(Exception e) {
			hideProgressDialog();
			showToast("" + e.getMessage());
		}
	};

	private OnClickListener mOnCancelClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			BookingRecord bookingRecord = (BookingRecord) v.getTag();
			onCancelBookingRequest(bookingRecord);
		}
	};

	private OnClickListener mOnRouteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			BookingRecord bookingRecord = (BookingRecord) v.getTag();
			showRoute(bookingRecord.getLatitude(), bookingRecord.getLongitude());
		}
	};

	private Callback<ResponseGetClientBookings> mCallbackBookings = new Callback<ResponseGetClientBookings>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetClientBookings response = (ResponseGetClientBookings) object;
			if (response.isSuccess()) {
				if (mOrdersList != null) {
					List<BookingRecord> bookings = response.getBookings();
					BookingHistoryAdapter adapter = new BookingHistoryAdapter(getActivity(), bookings, mOnCallClickListener, mOnCancelClickListener, mOnRouteClickListener);
					mOrdersList.setAdapter(adapter);
				}
			} else {
				logError(response.getStatusDetail());
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
	
	private void onCancelBookingRequest(final BookingRecord bookingRecord){
		Dialog dialog = DialogHelper.createModalMessageDialog(getActivity(), R.string.dialog_title_confirm_action, R.string.message_confirm_canceling_order, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showProgressDialog(R.string.message_canceling_order);
				RequestCancelOrder request = new RequestCancelOrder(bookingRecord.getBookingId());
				getApi().cancelQuery(request, mCancelQueryCallback);
			}
		});
		
		dialog.show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_explist, container, false);
		ButterKnife.inject(this, view);
		mOrdersList.setEmptyView(mListEmptyView);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getHistory();
	}

	private void getHistory() {
		showProgressDialog(R.string.message_requesting_booking_history);
		API api = getApi();
		RequestGetClientBookings request = RequestGetClientBookings.create(getUser().getPhone());
		api.getBookingsHistory(request, mCallbackBookings);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}
	
	private void showRoute(final double latitude, final double longitude){
		
		updateLocation(new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
				double currentLatitude = location.getLatitude();
				double currentLongitude = location.getLongitude();
				getMainActivity().showMapFragment(currentLatitude, currentLongitude, latitude, longitude);
			}
		});
	}
}
