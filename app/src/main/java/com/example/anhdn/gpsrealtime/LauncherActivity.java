package com.example.anhdn.gpsrealtime;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.anhdn.gpsrealtime.beans.DirectionObject;
import com.example.anhdn.gpsrealtime.interfaces.MyApiEndpointClient;
import com.example.anhdn.gpsrealtime.interfaces.PluginCallback;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class LauncherActivity extends AppCompatActivity {

	private static final String BASE_URL = "https://maps.googleapis.com";
	public static final String TAG = LauncherActivity.class.getSimpleName();
	private static final int MY_PERMISSION_LOCATION_REQUEST = 1;

	private Retrofit retrofit;
	private String sourceLat = "21.017147";
	private String sourceLong = "105.784515";
	private String destLat = "21.005418";
	private String destLong = "105.823655";
	private String origin = sourceLat + "," + sourceLong;
	private String destination = destLat + "," + destLong;
	private String tempEndAddress;
	private PluginCallback pluginCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_laucher);
		checkLocationPermission();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSION_LOCATION_REQUEST:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					createRetrofitInstance();
				} else {
					System.exit(0);
				}
		}
	}


	private void checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
				.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				new AlertDialog.Builder(this)
						.setTitle(getResources().getString(R.string.title_permission_location_dialog))
						.setMessage(getResources().getString(R.string.message_permission_location_dialog))
						.show();
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSION_LOCATION_REQUEST);
			}
		} else {
			createRetrofitInstance();
		}
	}

	private void createRetrofitInstance() {
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		Retrofit.Builder builder = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create());
		Retrofit retrofit = builder.client(httpClient.build()).build();
		MyApiEndpointClient client = retrofit.create(MyApiEndpointClient.class);
		retrofit2.Call<DirectionObject> responseBodyCall = client.getDirections(origin, destination,
				getResources().getString(R.string.api_key));
		responseBodyCall.enqueue(new Callback<DirectionObject>() {
			@Override
			public void onResponse(retrofit2.Call<DirectionObject> call, Response<DirectionObject> response) {
				tempEndAddress = response.body().getRoutes().get(0).getLegs().get(0).getEndAddress();
				Log.d(TAG, "onResponse: " + tempEndAddress);
				if (pluginCallback != null){
					pluginCallback.onWaypointFetched(tempEndAddress);
				}
			}

			@Override
			public void onFailure(retrofit2.Call<DirectionObject> call, Throwable t) {
				Log.e(TAG, "onFailure: " + call.toString(), t);
			}
		});
	}
}
