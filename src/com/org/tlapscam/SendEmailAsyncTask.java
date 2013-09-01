package com.org.tlapscam;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


public class SendEmailAsyncTask extends AsyncTask<PicData, Void, String> {
	
	Mail m = new Mail("gsocpublabs@gmail.com", "publabs1234");

    
	    @Override
	    protected String doInBackground(PicData... params) {
	
	    	if(params != null && params.length > 0)
			{
				PicData picData = params[0];
				String sdPath = Environment.getExternalStorageDirectory().getPath() + "/TLapseFolder/";
				String fileName = picData.getName();
				String savefile = sdPath + fileName;
	    	    String sendemail =picData.getemail();
	    	
	    	String[] toArr = {""+sendemail}; 
	        m.setTo(toArr); 
	        m.setFrom("gsocpublabs@gmail.com"); 
	        m.setSubject("GSoC 2013 Email"); 
	        m.setBody("Latitude: " + picData.getlats() + " Longitude " + picData.getlons() );	
	    	

	        try { 
	            m.addAttachment(""+savefile); 
	   
	          if(m.send()) { 
	        	  Log.i("hindilit", " Mail Sent " + "True");
	          } else { 
	        	  Log.i("hindilit", " Mail Sent " + "False");
	          } 
	        } catch(Exception e) { 
	          //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show(); 
	          Log.e("Mail", "Could not send email", e); 
	        } 
			
			}
	    	return null;
	    }
		
}
