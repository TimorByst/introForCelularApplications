package com.example.class23a_hw_1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentMap extends Fragment {
    private double longitude;
    private double latitude;
    private String title;
    private SupportMapFragment supportMapFragment;
    private Marker marker = null;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        return view;
    }

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            LatLng location = new LatLng(latitude, longitude);
            if(marker == null) {
                marker = googleMap.addMarker(new MarkerOptions().position(location).title(title));
            }else{
                marker.setPosition(location);
                marker.setTitle(title);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    };

    public void showLocation(double latitude, double longitude, String title){
        this.longitude = longitude;
        this.latitude = latitude;
        this.title = title;
        supportMapFragment.getMapAsync(onMapReadyCallback);
    }
}