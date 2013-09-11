package com.org.tlapscam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import java.io.OutputStream;




public class SavePic extends AsyncTask<PicData, Void, String> {
    
	
	
	protected String doInBackground(PicData... params)
	{
		if(params != null && params.length > 0)
		{
			PicData picData = params[0];
			byte[] data = picData.getData();
			String sdPath = Environment.getExternalStorageDirectory().getPath() + "/TLapseFolder/";
			File saveDir = new File(sdPath);
			File savebit=new File(sdPath);
			
			if (!saveDir.exists())
				saveDir.mkdirs();
			
			String fileName = picData.getName();
			String savefile = sdPath + fileName;
		
			OutputStream fos = null;
			FileOutputStream fOut;
			
            
			
			try
			{
				fos = new FileOutputStream(savefile);
				fos.write(data);
			    fos.close();
}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
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
	
	public static int calculateInSampleSize(
	        BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
