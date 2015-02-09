package com.regall.old.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.regall.old.network.response.ResponseGetCities.City;

public class CityAdapter extends BaseAdapter {

	private List<City> mCities;
	
	public CityAdapter(List<City> mCities) {
		this.mCities = mCities;
	}

	@Override
	public int getCount() {
		return mCities != null ? mCities.size() : 0;
	}

	@Override
	public City getItem(int position) {
		return mCities.get(position);
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
		}
		
		City city = getItem(position);
		((TextView) convertView).setText(city.getTitle());
		
		return convertView;
	}
	
	public int getCityPosition(City city){
		for(int i = 0, max = mCities.size(); i < max; i++){
			City c = mCities.get(i);
			if(c.equals(city)){
				return i;
			}
		}
		
		return -1;
	}

}
