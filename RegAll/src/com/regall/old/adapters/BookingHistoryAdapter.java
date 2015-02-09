package com.regall.old.adapters;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.regall.R;
import com.regall.old.db.DAOUserObject;
import com.regall.old.network.response.ResponseGetClientBookings.BookedService;
import com.regall.old.network.response.ResponseGetClientBookings.BookingRecord;
import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;
import com.regall.old.utils.DateHelper;

@SuppressLint({ "SimpleDateFormat", "UseSparseArrays" }) 
public class BookingHistoryAdapter extends BaseAdapter {
	
	private static class TimerStuff {
		Timer mTimer;
		UpdateTimeTask mTask;
	}
	
	private final static int UPDATE_TIMER_INTERVAL = 1000;
	
	private final static String BACKEND_DATE_FORMAT = "yyyy-MM-dd kk:mm:ss";
	private final static String VIEW_DATE_FORMAT = "dd MMMM yyyy kk:mm";

	private List<BookingRecord> mData;
	private WeakReference<OnClickListener> mCallListenerRef;
	private WeakReference<OnClickListener> mRouteListenerRef;
	private WeakReference<OnClickListener> mCancelListenerRef;
	
	private Map<Integer, TimerStuff> mTimers;
	private DAOUserObject mDAO;

	public BookingHistoryAdapter(Context context, List<BookingRecord> data, OnClickListener callListener, OnClickListener cancelListener, OnClickListener routeListener) {
		mData = data;
		mCallListenerRef = new WeakReference<View.OnClickListener>(callListener);
		mCancelListenerRef = new WeakReference<View.OnClickListener>(cancelListener);
		mRouteListenerRef = new WeakReference<View.OnClickListener>(routeListener);
		mTimers = new HashMap<Integer, TimerStuff>();
		mDAO = new DAOUserObject(context);
	}

	private View inflateNewParentItem(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.list_item_booking_record, parent, false);
		ParentViewHolder holder = new ParentViewHolder(view);
		view.setTag(holder);
		return view;
	}

	@Override
	public int getCount() {
		return mData != null ? mData.size() : 0;
	}

	@Override
	public BookingRecord getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getBookingId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflateNewParentItem(parent);
		}

		Context context = parent.getContext();
		BookingRecord bookingRecord = getItem(position);
		ParentViewHolder holder = (ParentViewHolder) convertView.getTag();
		
		String autowashTitle = context.getString(R.string.template_autowash, bookingRecord.getPointName());
		String autowashAddress = context.getString(R.string.template_address, bookingRecord.getPointAddress());
		ClientObject carObject = mDAO.getById(bookingRecord.getObjectId());
		String carTitle = "";
		if(carObject != null){
			carTitle = carObject.getObjectName();
		}
		String autowashCar = context.getString(R.string.template_automobile, carTitle);
		
		String autowashTime = "";
		try {
			autowashTime = DateHelper.convert(bookingRecord.getTimeStart(), BACKEND_DATE_FORMAT, VIEW_DATE_FORMAT);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		autowashTime = context.getString(R.string.template_time, autowashTime);
		
		String autowashState = context.getString(R.string.template_status, bookingRecord.getStatusText());
		String servicesString = getSelectedServicesReadableString(bookingRecord.getBookedServices());
		String autowashServices = context.getString(R.string.template_services, servicesString);
		
		holder.mTitle.setText(Html.fromHtml(autowashTitle));
		holder.mAddress.setText(Html.fromHtml(autowashAddress));
		holder.mCar.setText(Html.fromHtml(autowashCar));
		holder.mTime.setText(Html.fromHtml(autowashTime));
		holder.mState.setText(Html.fromHtml(autowashState));
		holder.mServices.setText(Html.fromHtml(autowashServices));

		holder.mLayoutTimeRemaining.setVisibility(View.VISIBLE);
		
		holder.mButtonCancelBooking.setVisibility(View.VISIBLE);
		holder.mButtonCancelBooking.setOnClickListener(null);
		holder.mButtonCancelBooking.setTag(bookingRecord);
		
		holder.mButtonShowRoute.setVisibility(View.VISIBLE);
		holder.mButtonShowRoute.setOnClickListener(null);
		holder.mButtonShowRoute.setTag(bookingRecord);
		
		holder.mButtonCall.setVisibility(View.VISIBLE);
		holder.mButtonCall.setOnClickListener(null);
		holder.mButtonCall.setTag(bookingRecord);
		holder.mButtonCall.setBackgroundResource(R.drawable.aux_button_left);

		if (bookingRecord.isInQueue()) {
			holder.mButtonCall.setOnClickListener(mCallListenerRef.get());
			holder.mButtonShowRoute.setOnClickListener(mRouteListenerRef.get());
			holder.mButtonCancelBooking.setOnClickListener(mCancelListenerRef.get());
		} else if (bookingRecord.isWaitingConfirmation()) {
			holder.mLayoutTimeRemaining.setVisibility(View.GONE);
			holder.mButtonCall.setOnClickListener(mCallListenerRef.get());
			holder.mButtonCall.setBackgroundResource(R.drawable.aux_button_single);
			holder.mButtonCancelBooking.setVisibility(View.GONE);
			holder.mButtonShowRoute.setVisibility(View.GONE);
		} else {
			holder.mLayoutTimeRemaining.setVisibility(View.GONE);
			holder.mButtonCancelBooking.setVisibility(View.GONE);
			holder.mButtonShowRoute.setVisibility(View.GONE);
			holder.mButtonCall.setVisibility(View.GONE);
		}

		if(bookingRecord.isInQueue()){
			TimerStuff timer = mTimers.get(position);
			if(timer != null){
				timer.mTask.clear();
				timer.mTask.cancel();
				timer.mTimer.cancel();
				timer.mTimer.purge();
			} 

			timer = new TimerStuff();
			timer.mTimer = new Timer();
			
			try {
				Date timeServiceEnd = new SimpleDateFormat(BACKEND_DATE_FORMAT).parse(bookingRecord.getTimeStart());
				long msAtServiceEnd = timeServiceEnd.getTime();
				long msAtNow = System.currentTimeMillis();
				
				if(msAtNow > msAtServiceEnd){
					holder.mLayoutTimeRemaining.setVisibility(View.GONE);
				} else {
					long millisecondsRemain = msAtServiceEnd - msAtNow;
					
					timer.mTask = new UpdateTimeTask(holder.mTextDays, holder.mTextHours, holder.mTextMinutes, holder.mTextSeconds, millisecondsRemain);
					timer.mTimer.schedule(timer.mTask, UPDATE_TIMER_INTERVAL, UPDATE_TIMER_INTERVAL);
					mTimers.put(position, timer);
					
					String[] dateDetails = getDateDetails(millisecondsRemain);
					
					holder.mTextDays.setText(dateDetails[0]);
					holder.mTextHours.setText(dateDetails[1]);
					holder.mTextMinutes.setText(dateDetails[2]);
					holder.mTextSeconds.setText(dateDetails[3]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				holder.mLayoutTimeRemaining.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}

	public static class ParentViewHolder {
		@InjectView(R.id.textAutowashTitle) TextView mTitle;
		@InjectView(R.id.textAutowashAddress) TextView mAddress;
		@InjectView(R.id.textAutoTitle) TextView mCar;
		@InjectView(R.id.textServiceTime) TextView mTime;
		@InjectView(R.id.textSelectedServices) TextView mServices;
		@InjectView(R.id.textServiceState) TextView mState;

		@InjectView(R.id.buttonCall) ImageButton mButtonCall;
		@InjectView(R.id.buttonShowRoute) ImageButton mButtonShowRoute;
		@InjectView(R.id.buttonCancelBooking) ImageButton mButtonCancelBooking;
		
		@InjectView(R.id.layoutTimeRemaining) ViewGroup mLayoutTimeRemaining;
		
		@InjectView(R.id.textDays) TextView mTextDays;
		@InjectView(R.id.textHours) TextView mTextHours;
		@InjectView(R.id.textMinutes) TextView mTextMinutes;
		@InjectView(R.id.textSeconds) TextView mTextSeconds;

		public ParentViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
	
	private String getSelectedServicesReadableString(List<BookedService> services){
		String delimiter = ", ";
		StringBuilder builder = new StringBuilder();
		for(BookedService service : services){
			builder.append(service.getServiceTitle()).append(delimiter);
		}
		builder.delete(builder.length() - delimiter.length(), builder.length());
		return builder.toString();
	}
	
	private class UpdateTimeTask extends TimerTask {
		
		private WeakReference<TextView> mTextDaysRef;
		private WeakReference<TextView> mTextHoursRef;
		private WeakReference<TextView> mTextMinutesRef;
		private WeakReference<TextView> mTextSecondsRef;
		
		private long mServiceTime;
		
		public UpdateTimeTask(TextView textDays, TextView textHours, TextView textMinutes, TextView textSeconds, long serviceTime) {
			mTextDaysRef = new WeakReference<TextView>(textDays);
			mTextHoursRef = new WeakReference<TextView>(textHours);
			mTextMinutesRef = new WeakReference<TextView>(textMinutes);
			mTextSecondsRef = new WeakReference<TextView>(textSeconds);
			mServiceTime = serviceTime;
		}
		
		public synchronized void clear(){
			mTextDaysRef.clear();
			mTextHoursRef.clear();
			mTextMinutesRef.clear();
			mTextSecondsRef.clear();
		}

		@Override
		public void run() {
			mServiceTime -= UPDATE_TIMER_INTERVAL;
			
			final TextView days = mTextDaysRef.get();
			final TextView hours = mTextHoursRef.get();
			final TextView minutes = mTextMinutesRef.get();
			final TextView seconds = mTextSecondsRef.get();
			
			final String[] dateDetails = getDateDetails(mServiceTime);

			if(days != null && hours != null && minutes != null && seconds != null){
				days.post(new Runnable() {
					
					@Override
					public void run() {
						days.setText(dateDetails[0]);
						hours.setText(dateDetails[1]);
						minutes.setText(dateDetails[2]);
						seconds.setText(dateDetails[3]);
					}
				});
			}
		}
	}
	
	private long SECOND = 1000;
	private long MINUTE = 60 * SECOND;
	private long HOUR = 60 * MINUTE;
	private long DAY = 24 * HOUR;
	
	private String[] getDateDetails(long ms){
		long days = ms / DAY;
		ms -= days * DAY;
		long hours = ms / HOUR;
		ms -= hours * HOUR;
		long minutes = ms / MINUTE;
		ms -= minutes * MINUTE;
		long seconds = ms / SECOND;
		
		return new String[]{Long.valueOf(days).toString(), Long.valueOf(hours).toString(), Long.valueOf(minutes).toString(), Long.valueOf(seconds).toString()};
	}
}
