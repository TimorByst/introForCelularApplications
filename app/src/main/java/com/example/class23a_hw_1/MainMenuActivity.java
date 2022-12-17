package com.example.class23a_hw_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainMenuActivity extends AppCompatActivity {

    private MaterialButton menu_BTN_sensors;
    private MaterialButton menu_BTN_arrows_fast;
    private MaterialButton menu_BTN_arrows_slow;
    private MaterialButton menu_BTN_records;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViews();
        initViews();
    }
    private void openGamePage(boolean playWithButtons, boolean fast){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("PlayMode", playWithButtons);
        intent.putExtra("GameSpeed", fast);
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

        //menu_BTN_records
    }

    private void findViews() {
        menu_BTN_arrows_fast = findViewById(R.id.menu_BTN_arrows_fast);
        menu_BTN_arrows_slow = findViewById(R.id.menu_BTN_arrows_slow);
        menu_BTN_sensors = findViewById(R.id.menu_BTN_sensors);
        menu_BTN_records = findViewById(R.id.menu_BTN_records);
    }


}
