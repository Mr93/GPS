package com.example.anhdn.gpsrealtime;

import com.example.anhdn.gpsrealtime.interfaces.PluginCallback;

/**
 * Created by AnhDN on 8/31/2017.
 */

public class UnityBridge {
	public void updateDirections(PluginCallback pluginCallback){
//		this.pluginCallback = pluginCallback;
//		if (tempEndAddress != null && !"".equalsIgnoreCase(tempEndAddress)){
//		}
		pluginCallback.onWaypointFetched("hello");
	}
}
