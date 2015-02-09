package com.regall.old.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.regall.R;
import com.regall.old.model.AdditionalService;
import com.regall.old.network.response.ResponseGetOrganizations.Organisation;
import com.regall.old.network.response.ResponseGetOrganizations.Point;
import com.regall.old.network.response.ResponseGetOrganizations.Point.AdditionalServiceDescription;
import com.regall.old.utils.DistanceCalc;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AutowashAdapter extends BaseExpandableListAdapter {

	private List<Point> mData;
	private WeakReference<OnClickListener> mSignupClickListener;
	
	private double mLatitude;
	private double mLongitude;
	
	public AutowashAdapter(List<Organisation> organisations, OnClickListener signupClickListener, double latitude, double longitude) {
		mData = new ArrayList<Point>();
		mLatitude = latitude;
		mLongitude = longitude;
		extractAllPoints(organisations);
		mSignupClickListener = new WeakReference<OnClickListener>(signupClickListener);
		System.out.println("lat - " + mLatitude + " lon - " + mLongitude);
	}

	private void extractAllPoints(List<Organisation> organisations) {
		for(Organisation organisation : organisations){
			mData.addAll(organisation.getOrganisations());
		}
	}
	
	public void addAll(List<Organisation> autowashes){
		extractAllPoints(autowashes);
		notifyDataSetChanged();
	}
	
	public void clearAll(){
		mData.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return mData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Point getGroup(int groupPosition) {
		return mData.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mData.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflateParentView(parent);
		}
		
		Point autowash = getGroup(groupPosition); 
		ViewHolderParent holder = (ViewHolderParent) convertView.getTag();
		
		holder.mAutowashTitle.setText(autowash.getName());
		holder.mAutowashAddress.setText(autowash.getAddress());
		holder.mAutowashWorkTime.setText(autowash.getWorkStart() + " - " + autowash.getWorkEnd());
		if(mLatitude > 0.01 && mLongitude > 0.01){
			double distance = DistanceCalc.getDistanceKm(mLatitude, mLongitude, autowash.getLatitude(), autowash.getLongitude());
			holder.mAutowashDistance.setText(parent.getResources().getString(R.string.template_distance, distance));
		} else {
			holder.mAutowashDistance.setText(R.string.message_no_distance_info);
		}
		
		int paddingLeft = holder.mLayoutContent.getPaddingLeft();
		int paddingTop = holder.mLayoutContent.getPaddingTop();
		int paddingRight = holder.mLayoutContent.getPaddingRight();
		int paddingBottom = holder.mLayoutContent.getPaddingBottom();
		
		if(isExpanded){
			holder.mLayoutContent.setBackgroundResource(R.drawable.blue_stroke_top);
			holder.mImageArrow.setImageResource(R.drawable.arrow_top);
		} else {
			holder.mLayoutContent.setBackgroundResource(R.drawable.shape_white_stroke_dark);
			holder.mImageArrow.setImageResource(R.drawable.arrow_down);
		}
		
		holder.mLayoutContent.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		
		return convertView;
	}
	
	private View inflateParentView(ViewGroup parent){
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View parentView = inflater.inflate(R.layout.list_item_autowash, parent, false);
		ViewHolderParent holder = new ViewHolderParent(parentView);
		parentView.setTag(holder);
		return parentView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflateChildView(parent);
		}
		
		Point autowash = getGroup(groupPosition);
		List<AdditionalServiceDescription> servicesDescription = autowash.getAdditionalServices();
		
		List<AdditionalService> services = new ArrayList<AdditionalService>();
		for(AdditionalServiceDescription serviceDescription : servicesDescription){
			services.add(AdditionalService.getServiceByServerCode(serviceDescription.getId()));
		}
		
		ViewHolderChild holder = (ViewHolderChild) convertView.getTag();
		holder.mImageRestRoom.setVisibility(services.contains(AdditionalService.REST_ROOM) ? View.VISIBLE : View.GONE);
		holder.mImagePayVisa.setVisibility(services.contains(AdditionalService.PAY_VISA) ? View.VISIBLE : View.GONE);
		holder.mImageFuel.setVisibility(services.contains(AdditionalService.FUEL) ? View.VISIBLE : View.GONE);
		holder.mImageTechSupport.setVisibility(services.contains(AdditionalService.TECH_SUPPORT) ? View.VISIBLE : View.GONE);
		holder.mImageWifi.setVisibility(services.contains(AdditionalService.WIFI) ? View.VISIBLE : View.GONE);
		holder.mImageFood.setVisibility(services.contains(AdditionalService.FOOD) ? View.VISIBLE : View.GONE);
		holder.mButtonSignup.setOnClickListener(mSignupClickListener.get());
		holder.mButtonSignup.setTag(autowash);
		
		return convertView;
	}

	private View inflateChildView(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View childView = inflater.inflate(R.layout.list_item_child_autowash, parent, false);
		ViewHolderChild holder = new ViewHolderChild(childView);
		childView.setTag(holder);
		return childView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	// =================================================================================
	// inner classes
	// =================================================================================
	
	public static class ViewHolderParent {
		@InjectView(R.id.textAutowashTitle) public TextView mAutowashTitle;
		@InjectView(R.id.textAutowashAddress) public TextView mAutowashAddress;
		@InjectView(R.id.textAutowashWorkTime) public TextView mAutowashWorkTime;
		@InjectView(R.id.textAutowashDistance) public TextView mAutowashDistance;
		
		@InjectView(R.id.icon) public ImageView mImageArrow;
		@InjectView(R.id.layoutContent) public ViewGroup mLayoutContent;

		public ViewHolderParent(View view) {
			ButterKnife.inject(this, view);
		}
	}

	class ViewHolderChild {
		@InjectView(R.id.layoutAdditionalServices) LinearLayout mLayoutServicesIcons;
		@InjectView(R.id.imageRestRoom) ImageView mImageRestRoom;
		@InjectView(R.id.imagePayVisa) ImageView mImagePayVisa;
		@InjectView(R.id.imageFuel) ImageView mImageFuel;
		@InjectView(R.id.imageTechSupport) ImageView mImageTechSupport;
		@InjectView(R.id.imageWifi) ImageView mImageWifi;
		@InjectView(R.id.imageFood) ImageView mImageFood;
		@InjectView(R.id.buttonSignUpForWash) Button mButtonSignup;

		public ViewHolderChild(View view) {
			ButterKnife.inject(this, view);
		}
	}

}
