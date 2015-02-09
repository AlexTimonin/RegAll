package com.regall.old.network.geocode;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.regall.R;
import com.regall.old.network.geocode.model.Bounds;

public class RouteCreator {

	private enum routeSpeedTypes {

		SLOW(2, R.color.route_speed_slow_color),

		NORMAL(5, R.color.route_speed_normal_color),

		NORMAL2(9, R.color.route_speed_normal2_color),

		FAST(15, R.color.route_speed_fast_color);

		private int speed;

		private int colorID;

		private routeSpeedTypes(int speed, int colorID) {

			this.speed = speed;

			this.colorID = colorID;

		}

		public int getSpeed() {

			return speed;

		}

		public int getColorID() {

			return colorID;

		}

	};

	public static PolylineOptions createRoutePolyline(ArrayList<LatLng> points) {

		PolylineOptions polyOptions = new PolylineOptions()

		.addAll(points)

		.width(15)

		.color(Color.argb(255, 113, 131, 77))

		.geodesic(true);

		return polyOptions;

	}

	public static ArrayList<PolylineOptions> createRoutePolyline(Context ctx, ArrayList<LatLng> points) {

		int slowColor = ctx.getResources().getColor(routeSpeedTypes.SLOW.getColorID());

		int normalColor = ctx.getResources().getColor(routeSpeedTypes.NORMAL.getColorID());

		int normalColor2 = ctx.getResources().getColor(routeSpeedTypes.NORMAL2.getColorID());

		int fastColor = ctx.getResources().getColor(routeSpeedTypes.FAST.getColorID());

		Random rand = new Random();

		ArrayList<PolylineOptions> polysOptions = new ArrayList<PolylineOptions>();

		for (int i = 0, len = points.size() - 2; i < len; i++) {

			PolylineOptions polyOptions = new PolylineOptions()

			.add(points.get(i))

			.add(points.get(i + 1))

			.width(15)

			/*
			 * .color(getSegmentColor(rand.nextInt(routeSpeedTypes.FAST.getSpeed(
			 * )), slowColor, normalColor, normalColor2, fastColor))
			 */

			.geodesic(true);

			polysOptions.add(polyOptions);

		}

		return polysOptions;

	}

	// @SuppressLint("NewApi")
	// private static int getSegmentColor(int speed, int slowColor, int
	// normalColor, int normalColor2, int fastColor) {
	//
	// if (speed <= routeSpeedTypes.SLOW.getSpeed()) {
	//
	// ArgbEvaluator ev = new ArgbEvaluator();
	//
	// return (int) ev.evaluate((float) speed / (float)
	// routeSpeedTypes.SLOW.getSpeed(), slowColor, normalColor);
	//
	// }
	//
	// else if (speed <= routeSpeedTypes.NORMAL.getSpeed()) {
	//
	// ArgbEvaluator ev = new ArgbEvaluator();
	//
	// return (int) ev.evaluate((float) (speed - (float)
	// routeSpeedTypes.SLOW.getSpeed()) / (float)
	// routeSpeedTypes.NORMAL.getSpeed(), normalColor, normalColor2);
	//
	// }
	//
	// else {
	//
	// ArgbEvaluator ev = new ArgbEvaluator();
	//
	// return (int) ev.evaluate((float) (speed -
	// routeSpeedTypes.NORMAL.getSpeed()) / (float) speed, normalColor2,
	// fastColor);
	//
	// }
	//
	// }

	public static MarkerOptions createRouteMarker(LatLng point, String title) {

		MarkerOptions markerOptions = new MarkerOptions()

		.position(point)

		.title(title);

		return markerOptions;

	}

	public static LatLngBounds createRouteBounds(Bounds bounds) {

		LatLngBounds geoBounds = new LatLngBounds(new LatLng(bounds.getSouthwest().getLat(), bounds.getSouthwest().getLng()),

		new LatLng(bounds.getNortheast().getLat(), bounds.getNortheast().getLng()));

		return geoBounds;

	}

}