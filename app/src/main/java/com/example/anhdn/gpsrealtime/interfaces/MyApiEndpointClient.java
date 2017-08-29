package com.example.anhdn.gpsrealtime.interfaces;

import com.example.anhdn.gpsrealtime.beans.DirectionObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by AnhDN on 8/28/2017.
 */

public interface MyApiEndpointClient {

	@GET("/maps/api/directions/json?sensor=false&mode=walking&alternatives=false")
	Call<DirectionObject> getDirections(
			@Query("origin") String origin,
			@Query("destination") String destination,
			@Query("key") String key
	);
}
