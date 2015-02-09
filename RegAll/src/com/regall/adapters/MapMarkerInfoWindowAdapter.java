package com.regall.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.regall.R;
import com.regall.old.network.response.ResponseGetOrganizations;

import java.util.Map;

/**
 * Created by Alex on 09.02.2015.
 */
public class MapMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private Map<Marker, ResponseGetOrganizations.Point> markerMap;

    public MapMarkerInfoWindowAdapter(Context context, Map<Marker, ResponseGetOrganizations.Point> markerMap) {
        this.context = context;
        this.markerMap = markerMap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View content = LayoutInflater.from(context).inflate(R.layout.new_map_info_window, null);
        ResponseGetOrganizations.Point point = markerMap.get(marker);
        ((ImageView) content.findViewById(R.id.mapInfoWindowImageView)).setImageResource(R.drawable.new_reg_screen_logo);
        ((TextView) content.findViewById(R.id.mapInfoWindowTitleTextView)).setText(point.getName());
        ((TextView) content.findViewById(R.id.mapInfoWindowAddressTextView)).setText(point.getAddress());
        ((TextView) content.findViewById(R.id.mapInfoWindowWorkTimeTextView)).setText(context.getString(R.string.from_to, point.getWorkStart(), point.getWorkEnd()));
        return content;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
