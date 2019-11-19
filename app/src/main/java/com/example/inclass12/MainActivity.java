package com.example.inclass12;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    ArrayList<LocList> locations = null;
    private Marker mStart;
    private Marker mEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String json = null;
        try {
            InputStream is = getAssets().open("trip.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Gson gson = new Gson();
        Location loc = gson.fromJson(json, Location.class);

        locations = loc.getListPoints();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        final ArrayList<LatLng> latLngArrayList = new ArrayList<>();

        for (LocList mylocation : locations) {
            LatLng my_val = new LatLng(Double.parseDouble(mylocation.getLatitude()), Double.parseDouble(mylocation.getLongitude()));
            latLngArrayList.add(my_val);
        }

        Polyline line = gMap.addPolyline(new PolylineOptions()
                .addAll(latLngArrayList)
                .width(5)
                .color(Color.RED));

        gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mStart = gMap.addMarker(new MarkerOptions().position(latLngArrayList.get(0)).title("Start Location"));
                mStart.showInfoWindow();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < latLngArrayList.size(); i++) {
                    builder.include(latLngArrayList.get(i));
                    Log.d("demo", "Value " + i + ": " + latLngArrayList.get(i).toString());
                }

                Log.d("demo2", "Size: " + latLngArrayList.size());
                LatLngBounds bo = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bo, 50);
                mEnd = gMap.addMarker(new MarkerOptions().position(latLngArrayList.get(latLngArrayList.size() - 1)).title("End Location"));
                mEnd.showInfoWindow();

                gMap.moveCamera(cu);
            }
        });
    }

}
