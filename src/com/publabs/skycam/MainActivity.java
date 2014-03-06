package com.publabs.skycam;

import com.publabs.skycam.objects.CameraPreview;
import com.publabs.skycam.utils.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
		    	showExposureSettingsDialog();
	        	break; 

	        case R.id.settings:
	        	startActivity(new Intent(this, PrefsActivity.class));
	        	break;

	        case R.id.about:
	        	showAboutDialog();
	        	break;

		}
	    return true;
	}

	private void showAboutDialog() {
		AlertDialog.Builder alertAbout = new AlertDialog.Builder(this);
		alertAbout.setTitle("About");
		alertAbout.setMessage("This app by Public Lab, will take periodic photographs, and is intended to operate a cheap Android phone while attached to a balloon or kite, for aerial photography. It emails small previews of photos and the latitude and longitude to the given email address, while in flight. " +
	  		"Be sure to share your work with the rest of the Public Lab community at PublicLab.org!");
		alertAbout.setNeutralButton("OK", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertAbout.show();
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
	
	private void showExposureSettingsDialog() {
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.dialog_exposure_settings, (ViewGroup) findViewById(R.id.layout_dialog_exposure_settings));      
        
        final TextView tvExposureTip = (TextView) viewLayout.findViewById(R.id.tvExposureSettingsCompensationTip);
        final TextView tvExposure = (TextView) viewLayout.findViewById(R.id.tvExposureSettingsCompensation);
        final TextView tvISO = (TextView) viewLayout.findViewById(R.id.tvExposureSettingsISOMode);
        final TextView tvISOTip = (TextView) viewLayout.findViewById(R.id.tvExposureSettingsISOModeTip);
        final SeekBar sbExposureCompensation = (SeekBar) viewLayout.findViewById(R.id.sbExposureSettingsCompensation);
        final SeekBar sbISOMode = (SeekBar) viewLayout.findViewById(R.id.sbExposureSettingsISOMode);
        
		popDialog.setIcon(android.R.drawable.btn_star_big_on);
		popDialog.setTitle("EXPOSURE SETTINGS");
		popDialog.setView(viewLayout);
		
		// check for exposure compensation
		if(mPreview.isExposureCompensationSupported()) {
			// calculate values
			Camera.Parameters paras = mPreview.getCameraParameters();
			int maxExposure = paras.getMaxExposureCompensation();
			int minExposure = paras.getMinExposureCompensation();
	        final int halfRange = maxExposure;
	        
	        tvExposureTip.setText("Tip: select exposure value between " + minExposure +" and " + maxExposure);
	        sbExposureCompensation.setProgress(paras.getExposureCompensation() + halfRange);
			tvExposure.setText("Exposure Index: " + paras.getExposureCompensation());
			sbExposureCompensation.setAlpha(1);
			sbExposureCompensation.setMax(2*halfRange);
			
			sbExposureCompensation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					progress = progress - halfRange;
					tvExposure.setText("Exposure Index: " + progress);
					mPreview.setExposureCompensation(progress);
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
		    });

	        
		} else {
			Toast.makeText(getApplicationContext(), "Exposure Compensation is not supported by your Smartphone", Toast.LENGTH_LONG).show();	
			tvExposureTip.setText("Tip: exposure compensation is not supported");
		}
		
		// check for ISO modes
		if(mPreview.isISOModeSupported()) {
			// calculate values for iso
			Camera.Parameters paras = mPreview.getCameraParameters();
			String currentISO = paras.get("iso");
			String values = paras.get("iso-values");
			final String[] isoValues = values.split(",");
			
			tvISOTip.setText("Supported ISO values : " + values);
			sbISOMode.setAlpha(1);
			sbISOMode.setMax(isoValues.length-1);
			
			// set current iso progress
			tvISO.setText("ISO value: " + currentISO);
			int currentISOPosition = 0;
			for(int i = 0; i < isoValues.length; i++) {
				if(isoValues[i].equals(currentISO)) {
					currentISOPosition = i;
					break;
				}
			}
			sbISOMode.setProgress(currentISOPosition);
			
			sbISOMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					String selectedMode = isoValues[progress];
					tvISO.setText("ISO value: " + selectedMode);
					mPreview.setISOMode(selectedMode);
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
		    });
			
		} else {
			Toast.makeText(getApplicationContext(), "ISO Modes are not supported by your Smartphone", Toast.LENGTH_LONG).show();	
			tvISOTip.setText("Tip: ISO modes are not supported");
		}
		
		popDialog.create();
		popDialog.show();
	}
	
}
