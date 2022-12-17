package com.example.class23a_hw_1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyAccelerometerDetector {

    public interface Callback_moveCar {
        void moveCar(float startingPos, float currentPos, float lastPos);

        void changeSpeed(float startingValue, float currentValue);
    }

    private Callback_moveCar callback_moveCar;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private float[] startingOrientation = new float[3];
    private float[] lastOrientation = new float[3];
    private float[] currentOrientation = new float[3];
    private boolean firstReading = true;

    private final int X = 0;
    private final int Y = 1;
    private final int Z = 2;


    public Callback_moveCar getCallback_moveCar() {
        return callback_moveCar;
    }

    public MyAccelerometerDetector(Context context, Callback_moveCar callback_moveCar) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.callback_moveCar = callback_moveCar;
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (firstReading) {
                // get the initial values to use them as a pivot
                startingOrientation[X] = event.values[X]; // X
                startingOrientation[Y] = event.values[Y]; // Y
                startingOrientation[Z] = event.values[Z]; // Z

                currentOrientation[X] = event.values[X]; // X
                currentOrientation[Z] = event.values[Z]; // Y

                firstReading = false;
            } else {

                float x = event.values[X];
                float z = event.values[Z];


                if (currentOrientation[X] != x) {
                    // Handle tilt
                    lastOrientation[X] = currentOrientation[X];
                    currentOrientation[X] = x;
                    callback_moveCar.moveCar(startingOrientation[X], currentOrientation[X], lastOrientation[X]);
                }
                if (currentOrientation[Z] != z) {
                    // Handle yaw
                    currentOrientation[Z] = z;
                    callback_moveCar.changeSpeed(startingOrientation[Z], currentOrientation[Z]);
                }
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void start() {
        sensorManager.registerListener(
                sensorEventListener,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(sensorEventListener);
    }
}


//import android.hardware.Sensor;
//        import android.hardware.SensorEvent;
//        import android.hardware.SensorEventListener;
//        import android.hardware.SensorManager;
//
//public class TiltPanDetector implements SensorEventListener {
//    private static final int SENSITIVITY = 2;
//
//    private SensorManager sensorManager;
//    private Sensor accelerometer;
//
//    private float[] startingOrientation = new float[3];
//    private boolean firstUpdate = true;
//
//    public TiltPanDetector(SensorManager sensorManager) {
//        this.sensorManager = sensorManager;
//        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//    }
//
//    public void start() {
//        // Get the starting orientation of the phone
//        SensorManager.getOrientation(startingOrientation);
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    public void stop() {
//        sensorManager.unregisterListener(this);
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (firstUpdate) {
//            // Skip the first update to avoid false tilt or pan
//            firstUpdate = false;
//            return;
//        }
//
//        float x = event.values[0];
//        float y = event.values[1];
//
//        if (Math.abs(x) > SENSITIVITY || Math.abs(y) > SENSITIVITY) {
//            // Handle tilt or pan
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Not used in this example
//    }
//}
