package com.regall.old.adapters;

import java.util.List;

import com.regall.old.network.geocode.json.Result;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GeocodeResultAdapter extends BaseAdapter {

	private List<Result> mGeocodeResults;
	
	public void setData(List<Result> geocodeResults){
		mGeocodeResults = geocodeResults;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mGeocodeResults != null ? mGeocodeResults.size() : 0;
	}

	@Override
	public Result getItem(int position) {
		return mGeocodeResults != null ? mGeocodeResults.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			convertView.setBackgroundColor(Color.argb(128, 255, 255, 255));
			convertView.setPadding(5, 5, 5, 5);
		}
		
		Result result = getItem(position);
		TextView textContainer = (TextView) convertView;
		textContainer.setText(result.getFormattedAddress());
		
		return convertView;
	}
}
