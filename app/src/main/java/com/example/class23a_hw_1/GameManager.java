package com.example.class23a_hw_1;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class GameManager {

    private int crashes = 0;
    private int life;

    /*Default constructor*/
    public GameManager() {
    }

    public GameManager(int life) {
        this.life = life;
    }

    public int getCrashes() {
        return crashes;
    }
    public void restart(){
        crashes = 0;
    }
    public boolean isLose() {
        return life == crashes;
    }

    public void crashed() {
        crashes++;
    }


}
