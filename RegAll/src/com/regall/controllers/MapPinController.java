package com.regall.controllers;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.regall.R;
import com.regall.adapters.MapMarkerInfoWindowAdapter;
import com.regall.old.network.response.ResponseGetOrganizations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 09.02.2015.
 */
public class MapPinController {

    private Map<Marker, ResponseGetOrganizations.Point> markerMap = new HashMap<Marker, ResponseGetOrganizations.Point>();
    private GoogleMap map;

    public MapPinController(Context context, GoogleMap map) {
        this.map = map;
        map.setInfoWindowAdapter(new MapMarkerInfoWindowAdapter(context, markerMap));
        map.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    public void swap(List<ResponseGetOrganizations.Organisation> organizations) {
        for (Marker marker : markerMap.keySet()) {
            for (ResponseGetOrganizations.Organisation organization : organizations) {
                if (!organization.getOrganisations().contains(markerMap.get(marker))) {
                    marker.remove();
                    markerMap.remove(marker);
                }
            }
        }
        for (ResponseGetOrganizations.Organisation organization : organizations) {
            for (ResponseGetOrganizations.Point point : organization.getOrganisations()) {
                if (!markerMap.containsValue(point)) {
                    int markerIconResId = organization.isPremium() ? R.drawable.new_pin_green : R.drawable.new_pin_gray;
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(point.getLatitude(), point.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(markerIconResId))
                            .anchor(1f, 0.33f)
                            .title(point.getName())
                            .draggable(false));
                    markerMap.put(marker, point);
                }
            }
        }
    }

    public void clear() {
        for (Marker marker : markerMap.keySet()) {
            marker.remove();
            markerMap.remove(marker);
        }
    }

    private GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            //TODO
        }
    };

}
