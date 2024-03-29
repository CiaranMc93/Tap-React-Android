package com.ciacavus.tapreact;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static java.lang.Math.sqrt;

/**
 * Created by ciaran on 12/06/2016.
 */

public class ShakeDetector implements SensorEventListener {

    /*
     * The gForce that is necessary to register as shake.
     * Must be greater than 1G (one earth gravity unit).
     * You can install "G-Force", by Blake La Pierre
     * from the Google Play Store and run it to see how
     *  many G's it takes to register a shake
     */
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    //get game logic variable to update the logic
    GameLogic gameLogic = new GameLogic();

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        void onShake(int count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor mAccelerometer = event.sensor;

        if(mAccelerometer.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            //check if the phone is upright or turned
            //if the values are between 2 certain points, then the device is upright

        }

        //make sure the event is called
        if (mListener != null) {
            //get the values of the event
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            //set the values relative to the GRAVITY_EARTH values
            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float)sqrt(gX * gX + gY * gY + gZ * gZ);

            //check if the gForce created by the shake is larger than the threshold
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                //update the shake count
                mShakeCount++;

                mListener.onShake(mShakeCount);
            }
        }
    }
}
