package com.bmw.bmwdemo.services;

import com.bmw.bmwdemo.model.Location;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Suman
 */

public interface RestfulAPI {

    @GET("Locations")
    Call<List<Location>> getFeed();
}
