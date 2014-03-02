package com.publabs.skycam;

import com.publabs.skycam.objects.CameraPreview;
import com.publabs.skycam.utils.Timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private TextView tvCountDownTimer;
	private Button bCountDownTimer;
	
    private CameraPreview mPreview;
    private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// initialization
		tvCountDownTimer = (TextView) findViewById(R.id.tvCountDown);
		bCountDownTimer = (Button) findViewById(R.id.bCountDown);
		
		// create preview view and set it as the content of the activity.
        mPreview = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.layoutCameraView);
        preview.addView(mPreview);

        // on click listener
        bCountDownTimer.setOnClickListener(this);
        
        mTimer = new Timer(this, tvCountDownTimer);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {

		    case R.id.exposure:
		    	//aboutExposureItem();
	        	break; 

	        case R.id.settings:
	        	startActivity(new Intent(this, PrefsActivity.class));
	        	break;

	        case R.id.about:
	        	//aboutMenuItem();
	        	break;

		}
	    return true;
	}
	
	public void onTimerFinished() {
		mPreview.takePicture();
	}
	
	private void handleStartTimer() {
		mTimer.startTimer();
		bCountDownTimer.setText("Stop Timer");
	}
	
	private void handleStopTimer() {
		mTimer.stopTimer();
		bCountDownTimer.setText("Start Timer");
	}

	@Override
	public void onClick(View v) {
		if(mTimer.isStarted()) {
			// stop the timer
			handleStopTimer();
		} else {
			// start the timer
			handleStartTimer();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPreview.startPreview();
		Log.v("Main", "OnResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mPreview.stopPreview();
		if(mTimer.isStarted())	mTimer.stopTimer();
		Log.v("Main", "OnPause");
	}
	
}
