package com.regall.controllers;

import android.content.Context;
import android.widget.ExpandableListView;
import com.regall.adapters.DetailsExpandableListAdapter;
import com.regall.old.network.response.ResponseGetOrganizations;

/**
 * Created by Alex on 12.03.2015.
 */
public class DetailsExpandableListController implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener {

    private Context context;
    private ExpandableListView expandableListView;
    private DetailsExpandableListAdapter adapter;
    private ResponseGetOrganizations.Point point;

    public DetailsExpandableListController(Context context, ExpandableListView expandableListView, ResponseGetOrganizations.Point point) {
        this.context = context;
        this.expandableListView = expandableListView;
        this.point = point;
    }

    public void init() {
        expandableListView.setAdapter(adapter = new DetailsExpandableListAdapter(context, point));
        expandableListView.setOnGroupExpandListener(this);
        expandableListView.setOnGroupCollapseListener(this);
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        adapter.getGroup(groupPosition).expanded = true;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        adapter.getGroup(groupPosition).expanded = false;
        adapter.notifyDataSetChanged();
    }
}
