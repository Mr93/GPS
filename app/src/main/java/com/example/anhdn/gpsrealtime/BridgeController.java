package com.example.anhdn.gpsrealtime;

import android.app.Activity;
import android.content.Intent;

import com.example.anhdn.gpsrealtime.activities.LauncherActivity;
import com.example.anhdn.gpsrealtime.interfaces.PluginCallback;

/**
 * Created by AnhDN on 9/5/2017.
 */

public class BridgeController extends Activity {

	private PluginCallback pluginCallback;
	private static final int REQUEST_WAYPOINT = 1;

	public void getDirections(PluginCallback pluginCallback){
		this.pluginCallback = pluginCallback;
		startActivityForResult(LauncherActivity.getStartIntent(this), REQUEST_WAYPOINT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
			case REQUEST_WAYPOINT:
				if (resultCode == RESULT_OK && data != null){

				}
		}
	}

	/*public void updateDirections(PluginCallback pluginCallback) {
		if (temp != null && !"".equalsIgnoreCase(temp)) {
			pluginCallback.onWaypointFetched(temp);
		}
	}*/

}
