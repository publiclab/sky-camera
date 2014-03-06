package com.publabs.skycam.objects;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import com.publabs.skycam.tasks.AsyncSaveExifTask;
import com.publabs.skycam.tasks.AsyncSavePicTask;
import com.publabs.skycam.utils.GPSTracker;
import com.publabs.skycam.utils.SensorTracker;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, PictureCallback {

	private static final String TAG = "CAMERA_PREVIEW";

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Context mContext;
	
	private GPSTracker mGPSTracker;
	private SensorTracker mSensorTracker;
	private SharedPreferences prefs;
	
	public CameraPreview(Context context) {
		super(context);

		mContext = context;
		// Shared preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// GPS
		mGPSTracker = new GPSTracker(context);
		// Accelerometer
		mSensorTracker = new SensorTracker(context);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(holder);
			Log.v(TAG, "Surface Created");
			
			Camera.Parameters para = mCamera.getParameters();
			
			// if phone is in portrait mode, rotate the camera preview
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				para.set("orientation", "portrait");
			    mCamera.stopPreview();
				mCamera.setDisplayOrientation(90);
				para.setRotation(90);
			}
			
			// setting max picture size
			Size maxPictureSize = getMaxPictureSize(para.getSupportedPictureSizes());
			para.setPictureSize(maxPictureSize.width, maxPictureSize.height);
	     	para.setFocusMode("auto");
			mCamera.setParameters(para);
			mCamera.startPreview();
			
		} catch (IOException exception) {
			mCamera.release();
			Log.d(TAG, "Error setting camera preview: " + exception.getMessage());
		} 
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(TAG, "Surface Changed");
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(TAG, "Surface Destroyed");
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	
	private Size getMaxPictureSize(List<Size> supportedSizes) {
		Iterator<Size> it = supportedSizes.iterator();
		
		ArrayList<Integer> widthSizes = new ArrayList<Integer>();
		ArrayList<Integer> heightSizes = new ArrayList<Integer>();
		
		while(it.hasNext()) {
			Size size = it.next();
			widthSizes.add(size.width);
			heightSizes.add(size.height);
		}
		
		int maxWidth = Collections.max(widthSizes);
		int maxheight = Collections.max(heightSizes);
		
		return mCamera.new Size(maxWidth, maxheight);
	}
	
	@SuppressWarnings("deprecation")
	public void startPreview() {
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// register sensor listener
		mSensorTracker.registerSensorTracker();
	}
	
	public void stopPreview() {
		mHolder.removeCallback(this);
		mSensorTracker.unregisterSensorTracker();
		// stop camera preview
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		mGPSTracker.stopUsingGPS();
	}
	
	public void takePicture() {
		if(mSensorTracker.isDeviceStable()) {
			// get GPS location
			mGPSTracker.getLocation();
	       	Toast.makeText(mContext, "Your Location is - \nLat: " + mGPSTracker.getLatitude() + "\nLong: " + mGPSTracker.getLongitude(), Toast.LENGTH_SHORT).show();
			// shoot
	       	mCamera.takePicture(null, null, this);
		} else {
			Toast.makeText(mContext, "Device is shaky. Photo is not taken", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// generate pic data
		PicData picData = generatePicData(data);
		// save picture
	    new AsyncSavePicTask().execute(picData);
	    // save exif
		new AsyncSaveExifTask().execute(picData);
		camera.startPreview();
	}
	
	private PicData generatePicData(byte[] data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yymmddhhmmss", Locale.getDefault());
	    String date = dateFormat.format(new Date());
	    String photoFile = "Picture_" + date + ".jpg";

	    String mailID = prefs.getString("mail", "");
	    PicData picData = new PicData(data, photoFile, mGPSTracker.getLatitude(), mGPSTracker.getLongitude(), mailID);
	    return picData;
	}
	
	public Camera.Parameters getCameraParameters() {
		return mCamera.getParameters();
	}
	
	public boolean isExposureCompensationSupported() {
		Camera.Parameters paras = getCameraParameters();
		return (paras.getMinExposureCompensation() != 0 && paras.getMaxExposureCompensation() != 0);
	}
	
	public void setExposureCompensation(int exposure){
		Camera.Parameters p = getCameraParameters();
	    mCamera.stopPreview();
		p.setExposureCompensation(exposure);
	    mCamera.setParameters(p);
		mCamera.startPreview();
	}

}
