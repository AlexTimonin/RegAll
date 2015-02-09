package com.regall.old.fragments;

import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.mobsandgeeks.saripaar.annotation.Regex;
import com.regall.R;
import com.regall.old.adapters.AutowashAdapter;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestGetRecentAutowashes;
import com.regall.old.network.response.ResponseGetOrganizations;
import com.regall.old.network.response.ResponseGetOrganizations.Organisation;
import com.regall.old.network.response.ResponseGetOrganizations.Point;

public class RecentAutowashesFragment extends BaseFragment implements OnFocusChangeListener, OnClickListener {

	private final static String DATE_FORMAT = "dd.MM.yyyy";
	
	private final static String PATTERN_DATE = "\\d{2}\\.\\d{2}\\.\\d{4}";
	
	@InjectView(android.R.id.list) ExpandableListView mAutowashesList;
	@InjectView(android.R.id.empty) TextView mEmptyView;
	
	@Regex(order = 2, pattern = PATTERN_DATE, messageResId = R.string.message_date_format_incorrect)
	@InjectView(R.id.editDateStart) TextView mEditStart;
	
	@Regex(order = 3, pattern = PATTERN_DATE, messageResId = R.string.message_date_format_incorrect)
	@InjectView(R.id.editDateEnd) TextView mEditEnd;
	
	private Callback<ResponseGetOrganizations> mCallbackGetWashes = new Callback<ResponseGetOrganizations>() {

		@Override
		public void success(Object object) {
			hideProgressDialog();
			ResponseGetOrganizations response = (ResponseGetOrganizations) object;
			if (response.isSuccess()) {
				displayDataForUser(response);
			} else {
				showToast(response.getStatusDetail());
			}
		}

		private void displayDataForUser(ResponseGetOrganizations response) {
			List<Organisation> organisations = response.getOrganisations();
			if (organisations != null && mAutowashesList != null) {
				mAdapterAutowashes = new AutowashAdapter(organisations, mSignupClickListener, 0, 0);
				mAutowashesList.setAdapter(mAdapterAutowashes);
			} else if (organisations == null) {
				showToast(R.string.message_no_autowashes_found);
			}
		}

		@Override
		public void failure(Exception e) {
			e.printStackTrace();
			hideProgressDialog();
			showToast(e.getMessage());
		}
	};
	
	private View.OnClickListener mSignupClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Point organisation = (Point) v.getTag();
			showSelectWashServiceFragment(organisation);
		}
	};
	
	private AutowashAdapter mAdapterAutowashes;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recent_autowashes, container, false);
		ButterKnife.inject(this, view);
		mAutowashesList.setEmptyView(mEmptyView);
		
		Calendar calendar = Calendar.getInstance();
		CharSequence today = DateFormat.format(DATE_FORMAT, calendar);
		calendar.add(Calendar.DATE, -7);
		CharSequence weekAgo = DateFormat.format(DATE_FORMAT, calendar);
		
		mEditStart.setText(weekAgo);
		mEditEnd.setText(today);

		mEditStart.setOnClickListener(this);
		mEditEnd.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshAutowashes();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			showDatePickerForView((EditText)v);
		}
	}
	
	private void showDatePickerForView(final TextView editText){
		Calendar currentDate = Calendar.getInstance();
		int year = currentDate.get(Calendar.YEAR);
		int month = currentDate.get(Calendar.MONTH);
		int day = currentDate.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				String dateString = String.format("%02d.%02d.%d", dayOfMonth, monthOfYear + 1, year);
				editText.setText(dateString);
				refreshAutowashes();
			}
		}, year, month, day);
		dialog.show();
	}

	private void refreshAutowashes(){
		showProgressDialog(R.string.message_requesting_for_autowashes);
		RequestGetRecentAutowashes request = RequestGetRecentAutowashes.create(mEditStart.getText().toString(), mEditEnd.getText().toString(), "0672639312");
		getApi().getRecentAutowashes(request, mCallbackGetWashes);
	}

	@Override
	public void onClick(View v) {
		showDatePickerForView((TextView)v);
	}
	
}
