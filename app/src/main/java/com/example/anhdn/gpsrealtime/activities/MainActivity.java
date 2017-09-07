package com.example.anhdn.gpsrealtime.activities;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.anhdn.gpsrealtime.R;
import com.example.anhdn.gpsrealtime.interfaces.PluginCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {


	public static final String TAG = MainActivity.class.getSimpleName();
	public static final String REQUESTING_LOCATION_UPDATES = "REQUESTING_LOCATION_UPDATES";
	private FusedLocationProviderClient fusedLocationProviderClient;
	private boolean requestingLocationUpdates;
	private LocationCallback locationCallback;
	private PluginCallback pluginCallback;
	private float currentLat;
	private float currentLong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				for (Location location : locationResult.getLocations()) {
					if (pluginCallback != null){
						pluginCallback.onLocationUpdate(location.getLatitude(), location.getLongitude());
					}
				}
			}
		};
		if (savedInstanceState != null && savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES)) {
			requestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (requestingLocationUpdates) {
//			startLocationUpdates();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		stopLocationUpdates();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(REQUESTING_LOCATION_UPDATES, requestingLocationUpdates);
		super.onSaveInstanceState(outState);
	}




	@SuppressWarnings("MissingPermission")
	private void getLastLocation() {
		fusedLocationProviderClient.getLastLocation()
				.addOnSuccessListener(this, new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location location) {
						if (location != null) {
//							lastLocation = location;
							if (pluginCallback != null){
								pluginCallback.onLocationUpdate(location.getLatitude(), location.getLongitude());
							}
						}
					}
				});
	}

//	@SuppressWarnings("MissingPermission")
//	private void startLocationUpdates() {
//		fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper*/);
//	}
//
//	private void stopLocationUpdates() {
//		fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//	}
//
//	public void updateLocation(PluginCallback pluginCallback){
//		this.pluginCallback = pluginCallback;
//	}
}
