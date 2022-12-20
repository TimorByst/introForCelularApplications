package com.example.class23a_hw_1;

import java.util.Timer;
import java.util.TimerTask;

public class MyTicker {

    private Timer timer = new Timer();
    private ActivityGame.CallbackTimer callbackTimer;
    private boolean isRunning = false;

    public MyTicker(ActivityGame.CallbackTimer callbackTimer){
        this.callbackTimer = callbackTimer;
    }
    public boolean isRunning(){return isRunning;}
    public void start(int delay){
        isRunning = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(callbackTimer != null)
                    callbackTimer.tick();
            }
        }, delay, delay);
    }

    public void stop(){
        timer.cancel();
        isRunning = false;
    }
}
