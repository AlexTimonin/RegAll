package com.regall.old.utils;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

public class AccordionGroupClickListener implements OnGroupClickListener {

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		for (int i = 0, max = parent.getCount(); i < max; i++) {
			if (i != groupPosition && parent.isGroupExpanded(i)) {
				parent.collapseGroup(i);
			} else if (i == groupPosition) {
				if (parent.isGroupExpanded(i)) {
					parent.collapseGroup(i);
				} else {
					parent.expandGroup(i);
				}
			}
		}

		return true;
	}

}
