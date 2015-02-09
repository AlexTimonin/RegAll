package com.regall.old.adapters;

import java.util.Collection;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.regall.R;
import com.regall.old.network.response.ResponseGetServices.Service;

public class FilterServiceAdapter extends BaseAdapter {

	private List<Service> mServices;
	private Collection<Service> mSelectedServices;
	private OnCheckedChangeListener mCheckedChangeListener;
	
	public FilterServiceAdapter(List<Service> services, Collection<Service> selectedServices, OnCheckedChangeListener checkedChangeListener) {
		mServices = services;
		mSelectedServices = selectedServices;
		mCheckedChangeListener = checkedChangeListener;
	}

	@Override
	public int getCount() {
		return mServices.size();
	}

	@Override
	public Service getItem(int position) {
		return mServices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Service service = getItem(position);

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		
		View view = inflater.inflate(R.layout.grid_item_service, parent, false);
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkboxService);
		checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
		checkbox.setText(service.getName());
		checkbox.setTag(service);
		
		if(mSelectedServices.contains(service)){
			checkbox.setChecked(true);
		}
		
		return view;
	}
}
