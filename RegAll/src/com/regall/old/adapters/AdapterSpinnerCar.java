package com.regall.old.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;

public class AdapterSpinnerCar extends BaseAdapter {
	
	private List<ClientObject> mClientObjects;
	
	public AdapterSpinnerCar(List<ClientObject> clientObjects) {
		mClientObjects = clientObjects;
	}

	@Override
	public int getCount() {
		return mClientObjects != null ? mClientObjects.size() : 0;
	}

	@Override
	public ClientObject getItem(int position) {
		return mClientObjects.get(position);
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

		ClientObject object = getItem(position);

		((TextView)convertView).setText(object.getObjectName());
		
		return convertView;
	}
}
