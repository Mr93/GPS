package com.example.anhdn.gpsrealtime;

import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.anhdn.gpsrealtime.interfaces.PluginCallback;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class RealTimeGpsActivity extends AppCompatActivity {

	public static final int INTERVAL_REQUEST = 1000;
	public static final int FASTEST_INTERVAL_REQUEST = 5000;
	public static final String TAG = RealTimeGpsActivity.class.getSimpleName();
	public static final int REQUEST_CHECK_SETTINGS = 2;
	public static final String REQUESTING_LOCATION_UPDATES = "REQUESTING_LOCATION_UPDATES";
	private FusedLocationProviderClient fusedLocationProviderClient;
	private Location lastLocation;
	private LocationRequest locationRequest;
	private boolean requestingLocationUpdates;
	private LocationCallback locationCallback;
	private PluginCallback pluginCallback;
	private float currentLat;
	private float currentLong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		createLocationRequest();
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
			startLocationUpdates();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocationUpdates();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(REQUESTING_LOCATION_UPDATES, requestingLocationUpdates);
		super.onSaveInstanceState(outState);
	}




	private void createLocationRequest() {
		locationRequest = new LocationRequest();
		locationRequest.setInterval(INTERVAL_REQUEST);
		locationRequest.setFastestInterval(FASTEST_INTERVAL_REQUEST);
		locationRequest.setSmallestDisplacement(1);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(locationRequest);
		SettingsClient settingsClient = LocationServices.getSettingsClient(this);
		Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
		task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
			@Override
			public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
				Log.d(TAG, "onSuccess: ");
				getLastLocation();
				requestingLocationUpdates = true;
			}
		}).addOnFailureListener(this, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				int statusCode = ((ApiException) e).getStatusCode();
				switch (statusCode) {
					case CommonStatusCodes.RESOLUTION_REQUIRED:
						try {
							ResolvableApiException resolvableApiException = (ResolvableApiException) e;
							resolvableApiException.startResolutionForResult(RealTimeGpsActivity.this, REQUEST_CHECK_SETTINGS);
						} catch (IntentSender.SendIntentException e1) {
							Log.e(TAG, "onFailure: ", e);
						}
						break;
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						System.exit(0);
						break;
				}
			}
		});
	}

	@SuppressWarnings("MissingPermission")
	private void getLastLocation() {
		fusedLocationProviderClient.getLastLocation()
				.addOnSuccessListener(this, new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location location) {
						if (location != null) {
							lastLocation = location;
							if (pluginCallback != null){
								pluginCallback.onLocationUpdate(location.getLatitude(), location.getLongitude());
							}
						}
					}
				});
	}

	@SuppressWarnings("MissingPermission")
	private void startLocationUpdates() {
		fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper*/);
	}

	private void stopLocationUpdates() {
		fusedLocationProviderClient.removeLocationUpdates(locationCallback);
	}

	public void updateLocation(PluginCallback pluginCallback){
		this.pluginCallback = pluginCallback;
	}
}
