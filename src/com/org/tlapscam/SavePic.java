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
			
			
			Bitmap b= BitmapFactory.decodeFile(savefile);
            Bitmap out = Bitmap.createScaledBitmap(b, 320, 480, false);

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
	
	
}
