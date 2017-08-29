package com.example.anhdn.gpsrealtime.interfaces;

import org.json.JSONObject;

/**
 * Created by AnhDN on 8/29/2017.
 */

public interface PluginCallback {

	void onWaypointFetched(String directions);

	void onLocationUpdate(double latitude, double longitude);

}
