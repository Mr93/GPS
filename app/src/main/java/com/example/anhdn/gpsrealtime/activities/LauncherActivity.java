package com.example.anhdn.gpsrealtime.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.anhdn.gpsrealtime.R;
import com.example.anhdn.gpsrealtime.beans.DirectionObject;
import com.example.anhdn.gpsrealtime.interfaces.MyApiEndpointClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LauncherActivity extends AppCompatActivity {

	private static final String BASE_URL = "https://maps.googleapis.com";
	public static final String TAG = LauncherActivity.class.getSimpleName();
	private static final int MY_PERMISSION_LOCATION_REQUEST = 1;
	public static final int INTERVAL_REQUEST = 1000;
	public static final int FASTEST_INTERVAL_REQUEST = 5000;
	public static final int REQUEST_CHECK_SETTINGS = 2;

	private Retrofit retrofit;
	private String sourceLat = "21.017147";
	private String sourceLong = "105.784515";
	private String destLat = "21.005418";
	private String destLong = "105.823655";
	private String origin = sourceLat + "," + sourceLong;
	private String destination = destLat + "," + destLong;
	private LocationRequest locationRequest;

	public static Intent getStartIntent(Context context) {
		Intent intent = new Intent(context, LauncherActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_laucher);
		checkLocationPermission();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSION_LOCATION_REQUEST:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
						PackageManager.PERMISSION_GRANTED) {
					createLocationRequest();
				} else {
					new AlertDialog.Builder(this)
							.setTitle(getResources().getString(R.string.title_permission_location_dialog))
							.setMessage(getResources().getString(R.string.message_permission_dialog))
							.setOnDismissListener(new DialogInterface.OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface dialogInterface) {
									LauncherActivity.this.finish();
								}
							})
							.show();
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_CHECK_SETTINGS:
				if (resultCode == RESULT_CANCELED) {
					this.finish();
				}
				break;
		}
	}

	private void checkLocationPermission() {
		if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !hasPermission(Manifest.permission.CAMERA)) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
					|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
				new AlertDialog.Builder(this)
						.setTitle(getResources().getString(R.string.title_permission_location_dialog))
						.setMessage(getResources().getString(R.string.message_permission_dialog))
						.setOnDismissListener(new DialogInterface.OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialogInterface) {
								ActivityCompat.requestPermissions(LauncherActivity.this, new String[]{Manifest.permission
										.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, MY_PERMISSION_LOCATION_REQUEST);
							}
						})
						.show();
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
						.CAMERA}, MY_PERMISSION_LOCATION_REQUEST);
			}
		} else {
			createLocationRequest();
		}
	}

	private boolean hasPermission(String permission) {
		return ContextCompat.checkSelfPermission(this, permission) == PackageManager
				.PERMISSION_GRANTED;
	}

	private void createRetrofitInstance() {
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		Retrofit.Builder builder = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create());
		retrofit = builder.client(httpClient.build()).build();
		MyApiEndpointClient client = retrofit.create(MyApiEndpointClient.class);
		retrofit2.Call<DirectionObject> responseBodyCall = client.getDirections(origin, destination,
				getResources().getString(R.string.api_key));
		responseBodyCall.enqueue(new Callback<DirectionObject>() {
			@Override
			public void onResponse(retrofit2.Call<DirectionObject> call, Response<DirectionObject> response) {
				Log.d(TAG, "onResponse: " + response.body());
			}

			@Override
			public void onFailure(retrofit2.Call<DirectionObject> call, Throwable t) {
				Log.e(TAG, "onFailure: " + call.toString(), t);
			}
		});
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
			}
		}).addOnFailureListener(this, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				handleFailureLocationSettingResponse(e);
			}
		});
	}

	private void handleFailureLocationSettingResponse(@NonNull Exception e) {
		int statusCode = ((ApiException) e).getStatusCode();
		switch (statusCode) {
			case CommonStatusCodes.RESOLUTION_REQUIRED:
				try {
					ResolvableApiException resolvableApiException = (ResolvableApiException) e;
					resolvableApiException.startResolutionForResult(LauncherActivity.this, REQUEST_CHECK_SETTINGS);
				} catch (IntentSender.SendIntentException e1) {
					Log.e(TAG, "onFailure: ", e);
				}
				break;
			case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
				this.finish();
				break;
		}
	}
}
