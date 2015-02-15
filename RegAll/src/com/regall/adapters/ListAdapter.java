package com.regall.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.regall.R;
import com.regall.old.network.response.ResponseGetOrganizations;

import java.util.List;

/**
 * Created by Alex on 14.02.2015.
 */
public class ListAdapter extends BaseAdapter {

    private Context context;
    private List<ResponseGetOrganizations.Organisation> data;

    public ListAdapter(Context context, List<ResponseGetOrganizations.Organisation> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (data != null) {
            for (ResponseGetOrganizations.Organisation organisation : data) {
                if (organisation.getOrganisations() != null) {
                    count += organisation.getOrganisations().size();
                }
            }
        }
        return count;
    }

    @Override
    public ResponseGetOrganizations.Point getItem(int position) {
        int orgIndex = 0;
        while (position >= data.get(orgIndex).getOrganisations().size()) {
            position -= data.get(orgIndex).getOrganisations().size();
            orgIndex++;
        }
        return data.get(orgIndex).getOrganisations().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.new_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        ResponseGetOrganizations.Point point = getItem(position);
        viewHolder.image.setImageResource(R.drawable.new_reg_screen_logo);
        viewHolder.name.setText(point.getName());
        viewHolder.address.setText(point.getAddress());
        viewHolder.workTime.setText(context.getString(R.string.from_to, point.getWorkStart(), point.getWorkEnd()));
        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        TextView address;
        TextView workTime;

        public ViewHolder(View root) {
            image = (ImageView) root.findViewById(R.id.listItemImageView);
            name = (TextView) root.findViewById(R.id.listItemTitleTextView);
            address = (TextView) root.findViewById(R.id.listItemAddressTextView);
            workTime = (TextView) root.findViewById(R.id.listItemWorkTimeTextView);
        }
    }

}
