package com.example.class23a_hw_1;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ActivityRecords extends AppCompatActivity {

    FragmentMap fragmentMap;
    FragmentRecords fragmentRecords;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        fragmentRecords = new FragmentRecords();
        fragmentMap = new FragmentMap();
        fragmentRecords.setCallback_Map(callback_map);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_LAY_records, fragmentRecords).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_LAY_maps, fragmentMap).commit();
    }

    Callback_Map callback_map = new Callback_Map() {
        @Override
        public void zoomOnRecord(String title, double longitude, double latitude) {
            fragmentMap.showLocation(latitude, longitude, title);
        }
    };

}
