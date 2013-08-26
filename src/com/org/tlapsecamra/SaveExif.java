package com.org.tlapsecamra;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.AsyncTask;
import android.os.Environment;
import android.media.ExifInterface;
import java.util.Date;




public class SaveExif extends AsyncTask<PicData, Void, String> {
    
	
	
	protected String doInBackground(PicData... params)
	{
		if(params != null && params.length > 0)
		{
			PicData picData = params[0];
			String sdPath = Environment.getExternalStorageDirectory().getPath() + "/TLapseFolder/";
			String fileName = picData.getName();
			String savefile = sdPath + fileName;
			
			try
			{
			    ExifInterface exif = new ExifInterface(savefile);
		    	double lat = picData.getlat()/1000000.0;
		    	
		    	if (lat < 0) {
		            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
		            lat = -lat;
		        } else {
		            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
		        }
		    	exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
		        		picData.getlats());

		        double lon = picData.getlon()/1000000.0;
		        if (lon < 0) {
		            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
		            lon = -lon;
		        } else {
		            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
		        }
		        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
		        		picData.getlons());
			    
		        exif.saveAttributes();
		        exif.setAttribute(ExifInterface.TAG_DATETIME, (new Date(System.currentTimeMillis())).toString());    
			
			}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
					
		}
		return null;
	}
	
	
}
