package com.regall.old.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.regall.R;
import com.regall.old.network.response.ResponseGetCarModels.CarModel;

public class AdapterCarModels extends BaseAdapter {

	private List<CarModel> mCarMarks;
	
	public AdapterCarModels(List<CarModel> mCarMarks) {
		this.mCarMarks = mCarMarks;
	}

	@Override
	public int getCount() {
		return mCarMarks != null ? mCarMarks.size() : 0;
	}

	@Override
	public CarModel getItem(int position) {
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
		CarModel mark = getItem(position);
		textView.setText(mark.getName());
		
		return convertView;
	}

}
