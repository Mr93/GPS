package com.example.anhdn.gpsrealtime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;

import com.example.anhdn.gpsrealtime.beans.DirectionObject;
import com.example.anhdn.gpsrealtime.interfaces.MyApiEndpointClient;
import com.example.anhdn.gpsrealtime.interfaces.PluginCallback;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LauncherActivity extends AppCompatActivity {

	private static final String BASE_URL = "https://maps.googleapis.com";
	public static final String TAG = LauncherActivity.class.getSimpleName();

	private Retrofit retrofit;
	private String sourceLat = "21.017147";
	private String sourceLong = "105.784515";
	private String destLat = "21.005418";
	private String destLong = "105.823655";
	private String origin = sourceLat + "," + sourceLong;
	private String destination = destLat + "," + destLong;
	private String temp;
	private PluginCallback pluginCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_laucher);
		createRetrofitInstance();
	}

	public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString
				.append(Double.toString(sourcelog));
		urlString.append("&destination=");// to
		urlString
				.append(Double.toString(destlat));
		urlString.append(",");
		urlString.append(Double.toString(destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		urlString.append("&key=" + getResources().getString(R.string.api_key));
		return urlString.toString();
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
				temp = response.body().getRoutes().get(0).getLegs().get(0).getEndAddress();
				if (pluginCallback != null){
					pluginCallback.onWaypointFetched(temp);
				}
			}

			@Override
			public void onFailure(retrofit2.Call<DirectionObject> call, Throwable t) {
				Log.e(TAG, "onFailure: " + call.toString(), t);
			}
		});
	}

	public void updateDirections(PluginCallback pluginCallback){
		this.pluginCallback = pluginCallback;
		if (temp != null && !"".equalsIgnoreCase(temp)){
			pluginCallback.onWaypointFetched(temp);
		}
	}
}
