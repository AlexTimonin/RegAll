package com.regall.old.adapters;

import java.lang.ref.WeakReference;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;

import com.regall.R;
import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;

public class AdapterClientObjects extends BaseAdapter {
	
	private List<ClientObject> mClientObjects;
	private WeakReference<View.OnClickListener> mListenerRef;
	
	public AdapterClientObjects(List<ClientObject> clientObjects, View.OnClickListener deleteClickListener) {
		mClientObjects = clientObjects;
		mListenerRef = new WeakReference<View.OnClickListener>(deleteClickListener);
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
			convertView = inflater.inflate(R.layout.list_item_user_object, parent, false);
			ViewHolder holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		ClientObject object = getItem(position);

		holder.mButtonDelete.setOnClickListener(mListenerRef.get());
		holder.mButtonDelete.setTag(object);
		holder.mObjectTitle.setText(object.getObjectName());
		
		return convertView;
	}
	
	public static class ViewHolder {
		TextView mObjectTitle;
		ImageButton mButtonDelete;
		
		public ViewHolder(View view){
			mObjectTitle = ButterKnife.findById(view, R.id.textCarTitle);
			mButtonDelete = ButterKnife.findById(view, R.id.buttonDelete);
		}
	}

}
