package com.org.tlapsecamra;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
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
	public static final int Time_Period = 10; 
	// in seconds
	
	SurfaceView camView;
	SurfaceHolder surfaceHolder;
	Camera cam;
    
	
	
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
	}
	
	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "'It is ready to take the photograph !!!", Toast.LENGTH_SHORT).show();
		}};

	public void onClick(View v) {
		if (!tlapseRunning) {
			startStopButton.setText("Stop Timer");
			tlapseRunning = true;
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
        p.setFocusMode("continuous-picture");
        cam.setParameters(p);
        try {
            cam.setPreviewDisplay(holder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		cam.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		cam = Camera.open();
		try {
			cam.setPreviewDisplay(holder);
			Camera.Parameters parameters = cam.getParameters();
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				/*parameters.set("orientation", "portrait");*/
				parameters.set("orientation", "portrait");
				cam.setDisplayOrientation(90);
				parameters.setRotation(90);
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
		(new SavePic()).execute(new PicData[]{new PicData(data, photoFile)});
		camera.startPreview();
		Toast t = Toast.makeText(this, "Saved JPEG!", Toast.LENGTH_SHORT);
		t.show();
	}

}
