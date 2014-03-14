package com.publabs.skycam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class SensorTracker implements SensorEventListener {

	private static final String TAG = "Sensor Tracker";
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	private SharedPreferences prefs;
	
	private float[] accelValues;	// stores the acceleration values of the sensor
	private boolean isLocked;		// to lock the accelValues when accessing the array
	
	public SensorTracker(Context context) {
		// initialization
		isLocked = false;
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		// Shared preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void registerSensorTracker() {
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unregisterSensorTracker() {
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(!isLocked)	accelValues = event.values;
	}
	
	private float findMaxAcceleration() {
		// lock the accelValues array
		isLocked = true;
		
		float maxAccel = 0;
		for(int i = 0; i < accelValues.length; i++) {
			float tempAccel = Math.abs(accelValues[i]);
			if(maxAccel < tempAccel) {
				maxAccel = tempAccel;
			}
		}
		
		// unlock the array
		isLocked = false;
		
		return maxAccel;
	}
	
	public boolean isDeviceStable() {
		float maxAccel = findMaxAcceleration();
		Log.v(TAG, "Max Accel = " + maxAccel);
		
		String thresholdStr = prefs.getString("thres", "2.0");
		try {
			
			float threshold = Float.parseFloat(thresholdStr);
			return (maxAccel < threshold);
			
		} catch(NumberFormatException ex) {
			ex.printStackTrace();
			return false;
		}

	}

}
