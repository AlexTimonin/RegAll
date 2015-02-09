package com.regall.old.adapters;

import java.lang.ref.WeakReference;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.regall.R;
import com.regall.old.network.response.ResponseGetFreeTime.Time;

public class TimeAdapter extends BaseAdapter {

	private List<Time> mTimeAvailable;
	
	private WeakReference<RadioGroup> mGroupReference;
	private WeakReference<OnCheckedChangeListener> mOnCheckedListenerReference;
	
	public TimeAdapter(List<Time> timeAvailable, RadioGroup group, OnCheckedChangeListener onCheckedListener) {
		mTimeAvailable = timeAvailable;
		mGroupReference = new WeakReference<RadioGroup>(group);		
		mOnCheckedListenerReference = new WeakReference<OnCheckedChangeListener>(onCheckedListener);
	}

	@Override
	public int getCount() {
		return mTimeAvailable.size();
	}

	@Override
	public Time getItem(int position) {
		return mTimeAvailable.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		
		RadioButton button = (RadioButton) inflater.inflate(R.layout.grid_item_time, parent, false);
		LayoutParams lp = (LayoutParams) button.getLayoutParams();

		Time time = getItem(position);
		button.setText(time.getValue());
		button.setTag(time);
		button.setOnCheckedChangeListener(mOnCheckedListenerReference.get());
		
		RadioGroup group = mGroupReference.get();
		if(group != null){
			group.addView(button);
		}
		
		button.setLayoutParams(lp);
		
		return button;
	}

}
