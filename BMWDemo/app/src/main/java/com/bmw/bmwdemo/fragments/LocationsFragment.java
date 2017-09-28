package com.bmw.bmwdemo.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bmw.bmwdemo.MainActivity;
import com.bmw.bmwdemo.MapsActivity;
import com.bmw.bmwdemo.R;
import com.bmw.bmwdemo.adapter.LocationAdapter;
import com.bmw.bmwdemo.model.Location;
import com.bmw.bmwdemo.model.SampleLocation;
import com.bmw.bmwdemo.services.RestfulAPI;
import com.bmw.bmwdemo.utilities.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Suman
 */

public class LocationsFragment extends Fragment {

    MainActivity mainActivity;
    @BindView(R.id.locationRecView)
    RecyclerView locationRecView;
    List<Location> locationList;
    Context ctx;
    String sortParam;

    @BindView(R.id.progressBar)
    ProgressBar pb;

    @BindView(R.id.button)
    Button mainMapBtn;

    @BindView(R.id.sortspinner)
    Spinner sortSpinner;

    public String BASE_URL = "http://localsearch.azurewebsites.net/api/";
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_location,null);
        unbinder = ButterKnife.bind(this, mainView);

        pb.setVisibility(View.VISIBLE);

        setUpSpinner(sortSpinner);
        sortParam = sortSpinner.getSelectedItem().toString();

        locationRecView.setClickable(true);
        locationRecView.setFocusable(true);

        getLocations();
        sortLocations();

        mainMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMap();
            }
        });
        return mainView;
    }
    private void setUpSpinner(Spinner sortSpinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx,
                R.array.sorting_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                pb.setVisibility(View.VISIBLE);
                sortParam = parentView.getSelectedItem().toString();
                sortLocations();
                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }



    private void sortLocations() {
        Log.e("Spinner", sortParam);
        if(locationList!=null) {
            if (sortParam.contains("Name")) {
                Collections.sort(locationList, new NameComparator());
            }
            if (sortParam.contains("Time")) {
                Collections.sort(locationList, new ArivTimeComparator());
            }
            if (sortParam.contains("Distance")) {
                Collections.sort(locationList, new DistanceComparator());
            }
            if (locationList != null) {
                LocationAdapter adapter = new LocationAdapter(locationList, this);
                locationRecView.setAdapter(adapter);
            }
        }
    }

    private void getLocations() {



        RestfulAPI api = Utils.getClient(mainActivity,BASE_URL).create(RestfulAPI.class);

        Call<List<Location>> call = api.getFeed();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                locationList = response.body();
                locationRecView.setLayoutManager(new LinearLayoutManager(ctx));
                LocationAdapter adapter = new LocationAdapter(locationList, LocationsFragment.this);
                locationRecView.setAdapter(adapter);

                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {

                Log.d("FAILURE0","::"+t.getMessage());
            }
        });
    }

    public class NameComparator implements Comparator<Location> {
        @Override
        public int compare(Location l1, Location l2) {
            return l1.getName().compareTo(l2.getName());
        }
    }
    public class ArivTimeComparator implements Comparator<Location> {
        @Override
        public int compare(Location l1, Location l2) {
            return l1.getArrivalTime().compareTo(l2.getArrivalTime());
        }
    }
    public class DistanceComparator implements Comparator<Location> {
        @Override
        public int compare(Location l1, Location l2) {
            return String.valueOf(l1.getDistance()).compareTo(String.valueOf(l2.getDistance()));
        }
    }

    public void launchMap() {
        ArrayList<SampleLocation> simpleLocations = new ArrayList<>();
        for (Location location: locationList
                ){
            SampleLocation simpleLocation = new SampleLocation(location.getID(),location.getName(),location.getLatitude(),
                    location.getLongitude(),location.getAddress(),location.getArrivalTime());
            simpleLocations.add(simpleLocation);

        }

        Intent intent = new Intent(ctx, MapsActivity.class);
        intent.putParcelableArrayListExtra("locations", simpleLocations);
        ctx.startActivity(intent);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ctx = context;

    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
