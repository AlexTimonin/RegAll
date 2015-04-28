package com.regall.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.regall.R;
import com.regall.old.network.response.ResponseGetOrganizations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 12.03.2015.
 */
public class DetailsExpandableListAdapter extends BaseExpandableListAdapter {

    public static final int ITEM_ID_INFO = 0;
    public static final int ITEM_ID_SERVICES = 1;
    public static final int ITEM_ID_OFFERS = 2;
    public static final int ITEM_ID_RATE = 3;

    private Context context;
    private List<DetailsItem> values;
    private ResponseGetOrganizations.Point point;

    public DetailsExpandableListAdapter(Context context, ResponseGetOrganizations.Point point) {
        this.context = context;
        this.point = point;
        values = new ArrayList<DetailsItem>(4);
        values.add(new DetailsItem(ITEM_ID_INFO, "Информация о мойке", R.drawable.new_ic_details_info, R.layout.new_details_exp_item_info));
        values.add(new DetailsItem(ITEM_ID_SERVICES, "Услуги", R.drawable.ic_new_details_services, R.layout.new_details_exp_item_services));
        values.add(new DetailsItem(ITEM_ID_OFFERS, "Акции", R.drawable.ic_new_details_offers, R.layout.new_details_exp_item_special_offers)); //TODO hide if no offers
        values.add(new DetailsItem(ITEM_ID_RATE, "Оценить", R.drawable.ic_new_details_rate, R.layout.new_details_exp_item_rate));
    }

    @Override
    public int getGroupCount() {
        return values.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (getGroup(groupPosition).id == ITEM_ID_SERVICES) {
            if (point.getServices() == null) {
                return 0;
            }
            return point.getServices().size();
        }
        return 1;
    }

    @Override
    public DetailsItem getGroup(int groupPosition) {
        return values.get(groupPosition);
    }

    @Override
    public Integer getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).contentResId;
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.new_details_exp_header, null);
        }
        ((ImageView) convertView.findViewById(R.id.detailsHeaderIcon)).setImageResource(getGroup(groupPosition).iconResId);
        ((TextView) convertView.findViewById(R.id.detailsHeaderText)).setText(getGroup(groupPosition).label);
        ((ImageView) convertView.findViewById(R.id.detailsHeaderExpandIndicator)).setImageResource(getGroup(groupPosition).expanded ? R.drawable.ic_new_details_arrow_up : R.drawable.ic_new_details_arrow_down);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(getChild(groupPosition, childPosition), null);
        switch (getGroup(groupPosition).id) {
            case ITEM_ID_INFO:
                ((TextView) convertView.findViewById(R.id.detailsInfoAddress)).setText(point.getAddress());
                ((TextView) convertView.findViewById(R.id.detailsInfoWorkTime)).setText(context.getString(R.string.from_to, point.getWorkStart(), point.getWorkEnd()));
                ((TextView) convertView.findViewById(R.id.detailsInfoPhone)).setText(point.getPhone());
                break;
            case ITEM_ID_SERVICES:
                ((TextView) convertView.findViewById(R.id.detailsServicesKey)).setText(point.getServices().get(childPosition).getTitle());
                ((TextView) convertView.findViewById(R.id.detailsServicesValue)).setText(point.getServices().get(childPosition).getDescription());
                break;
            case ITEM_ID_OFFERS:
                //TODO
                break;
            case ITEM_ID_RATE:
                break;
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public static class DetailsItem {
        public int id;
        public String label;
        public int iconResId;
        public int contentResId;
        public boolean expanded = false;

        public DetailsItem(int id, String label, int iconResId, int contentResId) {
            this.id = id;
            this.label = label;
            this.iconResId = iconResId;
            this.contentResId = contentResId;
        }
    }

}
