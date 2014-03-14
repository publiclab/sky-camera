package com.publabs.skycam.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.publabs.skycam.objects.Mail;
import com.publabs.skycam.objects.PicData;
import com.publabs.skycam.utils.Constants;

public class AsyncSendMailTask extends AsyncTask<PicData, Void, String> {

	private Context context;
	private Mail email;

	public AsyncSendMailTask(Context context, String username, String password) {
		email = new Mail(username, password);
	}
	
	@Override
	protected String doInBackground(PicData... params) {
		if (params != null && params.length > 0) {
			PicData picData = params[0];
			
			String fileName = picData.getName();
			String savefile = Constants.APPLICATION_PATH + fileName;
			String sendemail = picData.getEmail();

			String[] toArr = { "" + sendemail };
			email.setTo(toArr);
			email.setFrom("gsocpublabs@gmail.com");
			email.setSubject("GSoC 2013 Email");
			email.setBody("Latitude: " + picData.getlats() + " Longitude "+ picData.getlons());

			try {
				email.addAttachment(savefile);
				if (email.send()) {
					return "Email was sent successfully";
				} else {
					return "Email was not sent";
				}
			} catch (Exception e) {
				return "There was a problem sending the email";
			}

		} 
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(result == null) {
			Toast.makeText(context, "Email was sent successfully", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
		}
	}
	
}
