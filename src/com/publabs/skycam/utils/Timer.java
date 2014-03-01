package com.publabs.skycam.utils;

import com.publabs.skycam.MainActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

public class Timer {

	private SharedPreferences prefs;
	private CountDownTimer timer;
	
	private MainActivity activity;
	private TextView tvTimerStatus;
	
	private boolean isStarted;
	
	public Timer(MainActivity activity, TextView tvTimerStatus) {
		this.activity = activity;
		this.tvTimerStatus = tvTimerStatus;
		isStarted = false;
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public void startTimer() {
		try {
			int timePeriod = getTimePeriod();
			timer = new CountDownTimer(timePeriod * 1000, 1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					long secondsRemaining = Math.round(millisUntilFinished / 1000.0);
					Log.v("On Tick", "Remaing : " + secondsRemaining + " s");
					tvTimerStatus.setText(Long.toString(secondsRemaining));
				}
				
				@Override
				public void onFinish() {
					Log.v("Finish", "Timer Finished");
					activity.onTimerFinished();
					startTimer();
				}
			};
			
			timer.start();
			isStarted = true;
		} catch(NumberFormatException ex) {
			ex.printStackTrace();
			stopTimer();
		}
	}
	
	public void stopTimer() {
		isStarted = false;
		timer.cancel();
		tvTimerStatus.setText("");
	}
	
	private int getTimePeriod() throws NumberFormatException {
		String timePeriod = prefs.getString("time", Constants.DEFAULT_TIME_PERIOD);
		int ret = Integer.parseInt(timePeriod);
		return ret;
	}
	
}
