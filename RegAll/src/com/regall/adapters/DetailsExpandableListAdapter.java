package com.regall.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import com.regall.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 12.03.2015.
 */
public class DetailsExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<DetailsItem> values;

    public DetailsExpandableListAdapter(Context context) {
        this.context = context;
        values = new ArrayList<DetailsItem>(4);
        values.add(new DetailsItem(0, "Информация о мойке", R.layout.new_details_exp_item_info));
        values.add(new DetailsItem(1, "Услуги", R.layout.new_details_exp_item_services));
        values.add(new DetailsItem(2, "Акции", R.layout.new_details_exp_item_special_offers)); //TODO hide if no offers
        values.add(new DetailsItem(3, "Оценить", R.layout.new_details_exp_item_rate));
    }

    @Override
    public int getGroupCount() {
        return values.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
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

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(getChild(groupPosition, childPosition), null);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class DetailsItem {
        public int id;
        public String label;
        public int contentResId;

        public DetailsItem(int id, String label, int contentResId) {
            this.id = id;
            this.label = label;
            this.contentResId = contentResId;
        }
    }

}
