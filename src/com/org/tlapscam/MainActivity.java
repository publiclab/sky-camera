package com.org.tlapscam;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



public class MainActivity extends Activity implements OnClickListener,
	SurfaceHolder.Callback, Camera.PictureCallback, OnSharedPreferenceChangeListener, SensorEventListener {
	Button startStopButton;
	TextView countdownTextView;
	Handler timeUpdateHandler;
	Handler recd;
	boolean tlapseRunning = false;
	boolean firstshot = false;
	boolean minacc1 = false;
	int currTime = 0;
	int expindx ;
	String isomode = "auto";
	int isomode1;
	double latitude;
	double longitude;
	int Time_Period; 	// in seconds
	int w; 
	int h ;
	String time="";
	String time1="";
	String mailid = "";
	String mailid1 = "";
	String threshold1 = "";
	String threshold2 = "";
	String photoFile = "";
	//private static final String TAG = "Cam View";
	private static final SurfaceHolder SurfaceHolder = null;
	SurfaceView camView;
	SurfaceHolder surfaceHolder;
	Camera cam;
	GPSTracker gps;
	ArrayList<Integer> arrayList1 = new ArrayList<Integer>();
	ArrayList<Integer> arrayList2 = new ArrayList<Integer>();
	SharedPreferences prefs;
	SensorManager mSensorManager;
	Sensor mAccelerometer;
	LinkedList<Float> accdata;
	float[] values;
	int count;
	float threshold;
	PicData picnew;
	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		camView = (SurfaceView) this.findViewById(R.id.CameraView);
		surfaceHolder = camView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
       
		countdownTextView = (TextView) findViewById(R.id.CountDownTextView);
		prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		prefs.registerOnSharedPreferenceChangeListener(this);	
		//Log.i("Neetu", "ISO: " +prefs.getString("time", "300s"));
		time = prefs.getString("time", "25");
		time1 = time;
		mailid = prefs.getString("mail", "");
		mailid1 = mailid;
		threshold1 = prefs.getString("thres", "2.0");
		threshold2 = threshold1;
		double k = Double.parseDouble(threshold1);
		threshold = (float) k;
		//Log.i("starting", " threshold = "+ threshold);
		Time_Period = separateDigitsAndAlphabets(time);
		countdownTextView.setText(""+Time_Period);
		startStopButton = (Button) findViewById(R.id.CountDownButton);
		startStopButton.setOnClickListener(this);
		timeUpdateHandler = new Handler();
		gps = new GPSTracker(MainActivity.this);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		accdata = new LinkedList();
		count = 0;
		recd = new Handler();
		
	
	}
	
	public void onSensorChanged(SensorEvent event) {
	     // can be safely ignored
		getacceleration(event);
		
		}
		
		@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	     // can be safely ignored
	    }

	public float[] getacceleration(SensorEvent event){
		values = event.values;
     //   Log.i("Mohit", "ISO: " + values[0] +" "+ values[1] +" "+values[2] + " "+ findmax(values) +" "+System.currentTimeMillis());
        return values;
	}
	
	public float findmax (float[] a){
		float x = Math.abs(a[0]);
		for (int j = 0; j<a.length-1; j++){
			
			if(x<Math.abs(a[j+1])){
			  x = Math.abs(a[j+1]);
			}
		}
		return x;
	//	accdata.add(x);
	}
	
	public float finndmax (LinkedList<Float> b){
		float l = b.get(0);
		for (int j = 0; j<b.size()-1; j++){
			if(l < b.get(j+1)){
			  l = b.get(j+1);
			}
		}
		return l;
	}
	
	public boolean minacc(LinkedList<Float> b){
		Log.i("minacc", " Size = "+ accdata.size());
		float f1 = finndmax(b)- threshold; 
		if((finndmax(b) < threshold) || f1 <= 0.05){
			b.removeAll(b);
			return minacc1 = true;
		}
		else 
			b.removeAll(b);
			return minacc1 = false;
		
	}
	
	
	
	
	public void gatherdata(){
		recd.postDelayed(sTask, 0);
	 
	}
	
	private Runnable sTask = new Runnable() {
		
		public void run() {
        
    	if(5> count){
    		accdata.add(findmax(values));
        	count++;
        	Log.i("Mohit", " Size = "+ accdata.size() +" "+ findmax(values) +" "+count);
        	recd.postDelayed(sTask, 150);
    	}
    	else{
    		count = 0;
    		Log.i("Neetu", " Size = "+ accdata.size());
    	    recd.removeCallbacks(sTask); 
    		}
    
		
		
		
		
		}
   };
	
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
	    time = prefs.getString("time", "25");
	    mailid = prefs.getString("mail", "");
	    threshold1 = prefs.getString("threshold", "");
	    if(!(time.equalsIgnoreCase(time1))){
	        super.recreate();    	
	    }
	    
	    else if(!(mailid.equalsIgnoreCase(mailid1))){
	    		super.recreate();
	    	}
	    	
	    else if(!(threshold1.equalsIgnoreCase(threshold2))){
    		super.recreate();
    	} 
	
	 //   Log.i("Patil", "ISOO1: " +time);
	}

    public static int separateDigitsAndAlphabets(String str) {
        String number = "";
        String letter = "";
        int k = 0;
        for (int i = 0; i < str.length(); i++) {
               char a = str.charAt(i);
               if (Character.isDigit(a)) {
                     number = number + a;

               } else {
                     letter = letter + a;

               }
        }
       
        
        
        k = Integer.parseInt(number);
        
        try{
        if(letter.equals("m")){
        //	Log.i("Patil", "ISO1: " +letter);
           	    k = k*60;
        }
        if(letter.equals("h")){
          	    k = k*3600;
        }}
        catch (Exception e){
        	
        }
        
        
        return k;
 }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		timeUpdateHandler.removeCallbacks(timeUpdateTask);
		startStopButton.setText("Start Timer");
		tlapseRunning = false;
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		  
		    case R.id.exposure:
		    	aboutExposureItem();
	        	break; 
		
	        case R.id.settings:
	        	startActivity(new Intent(this, PrefsActivity.class));
	        	break;
	        
	        case R.id.about:
	        	aboutMenuItem();
	        	break;
	          
		}
	    return true;
	}
	
	
	private void aboutMenuItem(){
		 new AlertDialog.Builder(this)
		  .setTitle("About")
		  .setMessage("This is Google Summer of Code 2013 application")
		  .setNeutralButton("OK", new DialogInterface.OnClickListener(){
			  
			  @Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		  }).show();
		}

	
	private void aboutExposureItem(){
		
	    Camera.Parameters p = cam.getParameters();
	   // Log.i("Neetu", "ISO: " +p.flatten());
	    int max = p.getMaxExposureCompensation();
	    int min = p.getMinExposureCompensation();
	    

	    if (max == 0 && min == 0) {
	       	Toast.makeText(getApplicationContext(), "Exposure Compensation is not supported by your Smartphone", Toast.LENGTH_LONG).show();	
            return;
        }
        
	    
        float step = p.getExposureCompensationStep();
        int maxValue = (int) FloatMath.floor(max*step);
        int minValue = (int) FloatMath.ceil(min*step);
        final int procal = maxValue;
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.activity_dialog, (ViewGroup) findViewById(R.id.layout_dialog));       
        final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1); // txtItem1
        final TextView item2 = (TextView)Viewlayout.findViewById(R.id.txtItem2); // txtItem2
		popDialog.setIcon(android.R.drawable.btn_star_big_on);
		popDialog.setTitle("Select Exposure Value between " + minValue +" to "+maxValue);
		
		popDialog.setView(Viewlayout);
		
		//  seekBar1
		SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
		seek1.setProgress(p.getExposureCompensation()+procal);
		item1.setText("Exposure Index: " + p.getExposureCompensation());
	//	Log.i("Mohit", "camindx: " +p.getExposureCompensation());
		seek1.setAlpha(1);
		seek1.setMax(2*maxValue);
		seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
		        	progress = progress - procal;
		        	item1.setText("Exposure Index: " + progress);
		        	expomod(progress);
		        	
		        }

				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					
				}

				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
		    });

		// Button OK
		popDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}

				});
		
		if(p.get("iso") == null){
	    	Toast.makeText(getApplicationContext(), "ISO Modes are not supported by your Smartphone", Toast.LENGTH_LONG).show();	
            return;
	    }
		
		
		//  seekBar2
			SeekBar seek2 = (SeekBar) Viewlayout.findViewById(R.id.seekBar2);
			seek2.setMax(4);
			seek2.setProgress(isomode1);
			item2.setText("ISO Mode: " + isomode);
			seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
			            if(progress == 0){
			            	isomode = "auto";
			            	isomode1 = 0;
			            	item2.setText("ISO Mode: " + isomode);
				        	isomod(isomode);
                         }
			            
			            if (progress == 1){
			            	isomode = "100";
			            	isomode1 = 1;
			            	item2.setText("ISO Mode: " + isomode);
				        	isomod(isomode);
			            }
			        	
			            if (progress == 2){
			            	isomode = "200";
			            	isomode1 = 2;
			            	item2.setText("ISO Mode: " + isomode);
				        	isomod(isomode);
			            }
			            
			            if (progress == 3){
			            	isomode = "400";
			            	isomode1 = 3;
			            	item2.setText("ISO Mode: " + isomode);
				        	isomod(isomode);
			            }
			        	
			            if (progress == 4){
			            	isomode = "800";
			            	isomode1 = 4;
			            	item2.setText("ISO Mode: " + isomode);
				        	isomod(isomode);
			            }
			        	
			        }

					public void onStartTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub
						
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
			    });
			 

			// Button OK
			popDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});


		popDialog.create();
		popDialog.show();
 
		}
	
   private void isomod (String k){
	   
	   Camera.Parameters p = cam.getParameters();
       cam.stopPreview();
	   try {
       p.set("iso", k);
       cam.setParameters(p);
	   }
	   
	   catch (Exception e) {
       	Toast.makeText(getApplicationContext(), "This ISO Mode is not supported by your Smartphone.", Toast.LENGTH_LONG).show();	
       	isomode = "400";
       	isomode1 = 3;
       	p.set("iso", "300");
        cam.setParameters(p);
	   }
	   cam.startPreview();
   }
	
	
		 
   private void expomod(int x){
		Camera.Parameters p = cam.getParameters();
        cam.stopPreview();
		p.setExposureCompensation(x);
        cam.setParameters(p);
		cam.startPreview();
//		Log.i("Neetu", "ISO: " +p.flatten());
   }
		
	
   public class SendEmailAsyncTask extends AsyncTask<PicData, Void, String> {
		
		Mail m = new Mail("gsocpublabs@gmail.com", "publabs1234");
				
		    
		    @Override
		    protected String doInBackground(PicData... params) {
		
		    	if(params != null && params.length > 0)
				{
					PicData picData = params[0];
					String sdPath = Environment.getExternalStorageDirectory().getPath() + "/TLapseFolder/";
					String sdPath1 = Environment.getExternalStorageDirectory().getPath() + "/TLapseFolder/resize.png";
					String fileName = picData.getName();
					String savefile = sdPath + fileName;
		    	    String sendemail =picData.getemail();
		    	
		    	String[] toArr = {""+sendemail}; 
		        m.setTo(toArr); 
		        m.setFrom("gsocpublabs@gmail.com"); 
		        m.setSubject("GSoC 2013 Email"); 
		        m.setBody("Latitude: " + picData.getlats() + " Longitude " + picData.getlons() );	
		    	

		        try { 
		           
		        	m.addAttachment(""+sdPath1); 
		          
		          if(m.send()) { 
		        	  runOnUiThread(new Runnable() 
		        	  {
		        	     public void run() 
		        	     {
		        	    	 Toast.makeText(MainActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();    
		        	     }
		        	  }); 
		        	  
		        	  
		        	 // Log.i("hindilit", " Mail Sent " + "True");
		          } else { 
		        	  runOnUiThread(new Runnable() 
		        	  {
		        	     public void run() 
		        	     {
		        	    	 Toast.makeText(MainActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();    
		        	     }
		        	  }); 
		        	  
		        	  //  Log.i("hindilit", " Mail Sent " + "False");
		          } 
		        } catch(Exception e) { 
		        	runOnUiThread(new Runnable() 
		        	  {
		        	     public void run() 
		        	     {
		        	    	 Toast.makeText(MainActivity.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();    
		        	     }
		        	  }); 
		        	  
		        	
		        	 
		         // Log.e("Mail", "Could not send email", e); 
		        } 
				
				}
		    	return null;
		    }
			
	}
   
   
	public void onClick(View v) {
		if (!tlapseRunning) {
			
			if(gps.canGetLocation()){
				startStopButton.setText("Stop Timer");
				tlapseRunning = true;
				gps.getLocation();
	        	latitude  = gps.getLatitude();
	        	longitude = gps.getLongitude();
	        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();	
	        	
	        	timeUpdateHandler.post(timeUpdateTask);
	        //	recd.post(sTask);

			}else{
	        	gps.showSettingsAlert();
	        	
	        }
			
		} else {
			startStopButton.setText("Start Timer");
			tlapseRunning = false;
			timeUpdateHandler.removeCallbacks(timeUpdateTask);
		//	recd.removeCallbacks(sTask);
		}
	}

	private Runnable timeUpdateTask = new Runnable() {
		public void run() {
			if (currTime < Time_Period) {
				if(currTime == Time_Period - 1){
			        count = 0;
			        Log.i("patil", "Entered into last second");
			        gatherdata();
		        //	Log.i("Mohit1", " Size = "+ accdata.size() +" "+ findmax(values) +" "+count);
					currTime++;
					timeUpdateHandler.postDelayed(timeUpdateTask, 1000);
					countdownTextView.setText("" + currTime);
				}else if(currTime == 1 && firstshot == true){
					//Log.i("Neetu", "Count = " +count);
					count = 0;
					Log.i("vaibhav", "Entered into first second");
					gatherdata();
					currTime++;
					timeUpdateHandler.postDelayed(timeUpdateTask, 1000);
					countdownTextView.setText("" + currTime);
				}else if(currTime == 2 && firstshot == true){
					 minacc(accdata);
					 Log.i("ALS", "ACCleration = " +minacc1);
					 if(!(minacc1)){
						 File file = new File(Environment.getExternalStorageDirectory().getPath() + "/TLapseFolder/" +photoFile);
						 boolean deleted = file.delete();
						 Log.i("rahul meena", "File Deleted: "+deleted + " "+Environment.getExternalStorageDirectory().getPath() + "/TLapseFolder/" +photoFile);
					 }
					 else{
						 (new SendEmailAsyncTask()).execute(picnew); 
					 }
					 currTime++;
					 timeUpdateHandler.postDelayed(timeUpdateTask, 1000);
				     countdownTextView.setText("" + currTime);
				}
				
				else{
					currTime++;
					timeUpdateHandler.postDelayed(timeUpdateTask, 1000);
					countdownTextView.setText("" + currTime);
				}
				
			} else {
				count = 0;
				Log.i("patty", "Entered into camera second");
				gatherdata();
				firstshot = true;
	        //	Log.i("Mohit2", " Size = "+ accdata.size() +" "+ findmax(values) +" "+count);
				gps.getLocation();
		       	latitude  = gps.getLatitude();
		       	longitude = gps.getLongitude();
		       	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();	
		        cam.takePicture(null, null, null, MainActivity.this);
				currTime = 0;
				timeUpdateHandler.postDelayed(timeUpdateTask, 1000);
				countdownTextView.setText("" + currTime);
			}

/*			timeUpdateHandler.postDelayed(timeUpdateTask, 1000);
			countdownTextView.setText("" + currTime);*/
		}
	};

	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		
		cam.startPreview();
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			
			cam.setPreviewDisplay(holder);
			Camera.Parameters p = cam.getParameters();
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				
				p.set("orientation", "portrait");
			    cam.stopPreview();
				cam.setDisplayOrientation(90);
				p.setRotation(90);

			}
			
			List<Size> sizes = p.getSupportedPictureSizes();
			for (int i=0;i<sizes.size();i++){
				arrayList1.add(sizes.get(i).width);
				arrayList2.add(sizes.get(i).height);
	     //       Log.i("PictureSize", "Supported Size: " +sizes.get(i).width);         
	        }
			
			w = Collections.max(arrayList1);
			h = Collections.max(arrayList2);
			p.setPictureSize(w, h);
	    	p.setFocusMode("infinity");
			cam.setParameters(p);
			cam.startPreview();
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
	    photoFile = "Picture_" + date + ".jpg";
	    picnew = new PicData(data, photoFile, latitude, longitude, mailid);
	    (new SavePic()).execute(new PicData[]{new PicData(data, photoFile, latitude, longitude, mailid)});
		(new SaveExif()).execute(new PicData[]{new PicData(data, photoFile, latitude, longitude, mailid)});
		//(new SendEmailAsyncTask()).execute(new PicData[]{new PicData(data, photoFile, latitude, longitude, mailid)});
		camera.startPreview();
	}
	

	
	public void onPause() {
		super.onPause(); // onPause method in the parent class
		
		surfaceHolder.removeCallback(this);
		timeUpdateHandler.removeCallbacks(timeUpdateTask);
        expindx = cam.getParameters().getExposureCompensation();
        isomode = cam.getParameters().get("iso");
        cam.stopPreview();
		cam.release();
		cam=null;
		tlapseRunning = false;
		//tlapseRunning = true;
		startStopButton.setText("Start Timer");
		gps.stopUsingGPS();
        mSensorManager.unregisterListener(this);
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	public void onResume() {
		super.onResume(); // onResume method in the parent class
//		startStopButton.setText("Start2 Timer");
	    camView = (SurfaceView) findViewById(R.id.CameraView);
	    surfaceHolder = camView.getHolder();
	    surfaceHolder.addCallback(this);
	    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		gps.startUsingGPS();
		mSensorManager.registerListener(this, mAccelerometer, 50000);

		if (cam == null) {
	        cam = Camera.open();
	        cam.setDisplayOrientation(90);
			Camera.Parameters p = cam.getParameters();
            p.setExposureCompensation(expindx);
           // Log.i("Neetu", "ISO: " +p.get("iso"));
            
            if(!(p.get("iso") == null)){
                p.set("iso", isomode);
    	    }
            
            cam.setParameters(p);
	        cam.startPreview();
			/*try {
				cam.setPreviewDisplay(surfaceHolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}
	}
	
}
