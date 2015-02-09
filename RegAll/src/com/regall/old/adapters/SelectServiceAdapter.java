package com.regall.old.adapters;

import java.util.List;
import java.util.Set;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.regall.R;
import com.regall.old.network.response.ResponseGetOrganizations.Point.ServiceDescription;

public class SelectServiceAdapter extends BaseAdapter {

	private List<ServiceDescription> mServices;
	private Set<ServiceDescription> mServicesSelected;
	private OnCheckedChangeListener mCheckedChangeListener;
	
	public SelectServiceAdapter(List<ServiceDescription> services, Set<ServiceDescription> selectedServices, OnCheckedChangeListener checkedChangeListener) {
		this.mServices = services;
		this.mCheckedChangeListener = checkedChangeListener;
		this.mServicesSelected = selectedServices;
	}

	@Override
	public int getCount() {
		return mServices.size();
	}

	@Override
	public ServiceDescription getItem(int position) {
		return mServices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ServiceDescription service = getItem(position);

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		
		View view = inflater.inflate(R.layout.grid_item_service, parent, false);
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkboxService);
		checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
		checkbox.setText(service.getTitle());
		checkbox.setTag(service);
		
		if(mServicesSelected.contains(service)) {
			checkbox.setChecked(true);
		}
		
		return view;
	}
}
