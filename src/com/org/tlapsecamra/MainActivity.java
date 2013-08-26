package com.org.tlapsecamra;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements OnClickListener,
	SurfaceHolder.Callback, Camera.PictureCallback {
	Button startStopButton;
	TextView countdownTextView;
	Handler timeUpdateHandler;
	boolean tlapseRunning = false;
	int currTime = 0;
	double latitude;
	double longitude;
	public static final int Time_Period = 25; 
	// in seconds
	private static final String TAG = "Cam View";
	private static final SurfaceHolder SurfaceHolder = null;
	SurfaceView camView;
	SurfaceHolder surfaceHolder;
	Camera cam;
	GPSTracker gps;
    
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		camView = (SurfaceView) this.findViewById(R.id.CameraView);
		surfaceHolder = camView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);

		countdownTextView = (TextView) findViewById(R.id.CountDownTextView);
		startStopButton = (Button) findViewById(R.id.CountDownButton);
		startStopButton.setOnClickListener(this);
		timeUpdateHandler = new Handler();
		gps = new GPSTracker(MainActivity.this);
	}
	

	public void onClick(View v) {
		if (!tlapseRunning) {
			startStopButton.setText("Stop Timer");
			tlapseRunning = true;
			
			if(gps.canGetLocation()){
	        	gps.getLocation();
	        	latitude  = gps.getLatitude();
	        	longitude = gps.getLongitude();
	        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
	        }else{
	        	gps.showSettingsAlert();
	        	
	        }
			
			timeUpdateHandler.post(timeUpdateTask);
		} else {
			startStopButton.setText("Start Timer");
			tlapseRunning = false;
			timeUpdateHandler.removeCallbacks(timeUpdateTask);
		}
	}

	private Runnable timeUpdateTask = new Runnable() {
		public void run() {
			if (currTime < Time_Period) {
				currTime++;
			} else {
				
			  	gps.getLocation();
		       	latitude  = gps.getLatitude();
		       	longitude = gps.getLongitude();
		       	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
		        cam.takePicture(null, null, null, MainActivity.this);
				currTime = 0;
			}

			timeUpdateHandler.postDelayed(timeUpdateTask, 1000);
			countdownTextView.setText("" + currTime);
		}
	};

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Camera.Parameters p = cam.getParameters();
        List<Size> sizes = p.getSupportedPictureSizes();
        for (int i=0;i<sizes.size();i++){
            Log.i("PictureSize", "Supported Size: " +sizes.get(i).width);         
        }
        Size size = sizes.get(sizes.size()-1);
        p.setPictureSize(size.width, size.height);
        p.setPreviewSize(w, h);
        p.setFocusMode("infinity");
        cam.setParameters(p);
        try {
        	 cam.setPreviewDisplay(holder);
        	 cam.startPreview();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			cam.setPreviewDisplay(holder);
			Camera.Parameters parameters = cam.getParameters();
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				
				parameters.set("orientation", "portrait");
			    cam.stopPreview();
				cam.setDisplayOrientation(90);
				parameters.setRotation(90);
			    cam.startPreview();
			}
			
			cam.setParameters(parameters);
		} catch (IOException exception) {
			cam.release();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		cam.stopPreview();
		cam.release();
	}

		
	public void onPictureTaken(byte[] data, Camera camera) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yymmddhhmmss");
	    String date = dateFormat.format(new Date());
	    String photoFile = "Picture_" + date + ".jpg";
		(new SavePic()).execute(new PicData[]{new PicData(data, photoFile, latitude, longitude)});
		(new SaveExif()).execute(new PicData[]{new PicData(data, photoFile, latitude, longitude)});
		camera.startPreview();
		Toast t = Toast.makeText(this, "Saved JPEG!", Toast.LENGTH_SHORT);
		t.show();
	}
	

	
	public void onPause() {
		super.onPause(); // onPause method in the parent class
		
		surfaceHolder.removeCallback(this);
		timeUpdateHandler.removeCallbacks(timeUpdateTask);
        cam.stopPreview();
		cam.release();
		cam=null;
		tlapseRunning = false;
		startStopButton.setText("Start Timer");
		gps.stopUsingGPS();
	}
	
	public void onResume() {
		super.onResume(); // onResume method in the parent class

	    camView = (SurfaceView) findViewById(R.id.CameraView);
	    surfaceHolder = camView.getHolder();
	    surfaceHolder.addCallback(this);
	    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		gps.startUsingGPS();
		if (cam == null) {
	        cam = Camera.open();
	        cam.setDisplayOrientation(90);
			cam.startPreview();
			try {
				cam.setPreviewDisplay(surfaceHolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
