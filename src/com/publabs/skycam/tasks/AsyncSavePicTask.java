package com.publabs.skycam.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.OutputStream;

import com.publabs.skycam.objects.PicData;
import com.publabs.skycam.utils.Constants;

public class AsyncSavePicTask extends AsyncTask<PicData, Void, String> {
    
	protected String doInBackground(PicData... params) {
		if(params != null && params.length > 0) {
			PicData picData = params[0];
			byte[] data = picData.getData();
			File saveDir = new File(Constants.APPLICATION_PATH);
			File savebit=new File(Constants.APPLICATION_PATH);
			
			if (!saveDir.exists())	saveDir.mkdirs();
			
			String fileName = picData.getName();
			String savefile = Constants.APPLICATION_PATH + fileName;
		
			OutputStream fos = null;
			FileOutputStream fOut;
			try {
				fos = new FileOutputStream(savefile);
				fos.write(data);
			    fos.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			// run garbage collector
			System.gc();
			
			BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(savefile, options);

		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, 640, 480);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    Bitmap b = BitmapFactory.decodeFile(savefile, options);
			
			Bitmap out = Bitmap.createScaledBitmap(b, 640, 480, false);

            File file = new File(savebit, "resize.png");
            try {
                fOut = new FileOutputStream(file);
                out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                b.recycle();
                out.recycle();
            } catch (Exception e) { // TODO
            	e.printStackTrace();
            }
            
					
		}
		
		return null;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 8;

	    if (height > reqHeight || width > reqWidth) {

	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    
	    return inSampleSize;
	}
	
}
