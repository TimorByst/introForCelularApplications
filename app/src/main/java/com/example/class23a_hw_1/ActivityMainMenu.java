package com.example.class23a_hw_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

public class ActivityMainMenu extends AppCompatActivity {

    private AppCompatImageView menu_IMG_highway;
    private MaterialButton menu_BTN_sensors;
    private MaterialButton menu_BTN_arrows_fast;
    private MaterialButton menu_BTN_arrows_slow;
    private MaterialButton menu_BTN_records;
    private  FragmentRecords fragmentRecords;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        fragmentRecords = new FragmentRecords();

        findViews();
        loadImages(R.drawable.three_lane_highway, menu_IMG_highway);
        initViews();
    }
    private void openGamePage(boolean playWithButtons, boolean fast){
        Intent intent = new Intent(this, ActivityGame.class);
        intent.putExtra("PlayMode", playWithButtons);
        intent.putExtra("GameSpeed", fast);
        startActivity(intent);
    }

    private void openRecordPage(){
        Intent intent = new Intent(this, ActivityRecords.class);
        startActivity(intent);
    }

    private void initViews() {
        menu_BTN_arrows_fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGamePage(true, true);
            }
        });

        menu_BTN_arrows_slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGamePage(true, false);
            }
        });

        menu_BTN_sensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGamePage(false, false);
            }
        });

        menu_BTN_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecordPage();
            }
        });
    }

    private void findViews() {
        menu_IMG_highway = findViewById(R.id.menu_IMG_highway);
        menu_BTN_arrows_fast = findViewById(R.id.menu_BTN_arrows_fast);
        menu_BTN_arrows_slow = findViewById(R.id.menu_BTN_arrows_slow);
        menu_BTN_sensors = findViewById(R.id.menu_BTN_sensors);
        menu_BTN_records = findViewById(R.id.menu_BTN_records);
    }

    private void loadImages(int imageResource, AppCompatImageView imgView){
        Glide.with(this).load(imageResource).into(imgView);
    }


}
