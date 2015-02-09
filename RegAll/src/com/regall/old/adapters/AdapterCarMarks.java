package com.regall.old.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.regall.R;
import com.regall.old.network.response.ResponseGetCarMarks.CarMark;

public class AdapterCarMarks extends BaseAdapter {

	private List<CarMark> mCarMarks;
	
	public AdapterCarMarks(List<CarMark> mCarMarks) {
		this.mCarMarks = mCarMarks;
	}

	@Override
	public int getCount() {
		return mCarMarks != null ? mCarMarks.size() : 0;
	}

	@Override
	public CarMark getItem(int position) {
		return mCarMarks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			Context context = parent.getContext();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
			int padding = context.getResources().getDimensionPixelSize(R.dimen.spinnerItemPadding);
			convertView.setPadding(padding, padding, padding, padding);
		}

		TextView textView = (TextView) convertView;
		CarMark mark = getItem(position);
		textView.setText(mark.getName());
		
		return convertView;
	}

}
