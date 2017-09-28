package com.bmw.bmwdemo.utilities;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Suman
 */

public class Utils {


    public static Retrofit getClient(Context context,String baseUrl) {
        Retrofit retrofit= null;
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
