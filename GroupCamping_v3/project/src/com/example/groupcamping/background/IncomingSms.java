package com.example.groupcamping.background;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser.BookmarkColumns;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.groupcamping.dropbox.Defines;
import com.example.groupcamping.utis.MyLog;
import com.example.groupcamping.utis.MyToast;

public class IncomingSms extends BroadcastReceiver {

	// Get the object of SmsManager
	final SmsManager sms = SmsManager.getDefault();

	public void onReceive(Context context, Intent intent) {

		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {

					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();

					String senderNum = phoneNumber;
					String message = currentMessage.getDisplayMessageBody();

					if (message.contains(Defines.DROPBOX_MESSAGE)) {
						try {
							String[] arr = message.split("\n");
							String url = arr[1];
							url.replace(Defines.SHARE_URL, Defines.DIRECT_DOWNLOAD_URL);
							String fileName = arr[2];

							Log.i("SmsReceiver", "url: " + url + "; fileName: " + fileName);
							saveFileFromDropbox(context, url, fileName);
						} catch (Exception e) {
							e.printStackTrace();
							MyLog.eGeneral("Save file error: " + e);
						}
					}

					Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

					// // Show Alert
					// int duration = Toast.LENGTH_LONG;
					// Toast toast = Toast
					// .makeText(context, "senderNum: " + senderNum + ", message: " + message, duration);
					// toast.show();

				} // end for loop
			} // bundle is null

		} catch (Exception e) {
			Log.e("SmsReceiver", "Exception smsReceiver" + e);

		}
	}

	public void saveFileFromDropbox(final Context context, final String url, final String name) throws Exception {

		new AsyncTask<Object, Object, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				try {
					URL wallpaperURL = new URL(url);
					// URLConnection connection = wallpaperURL.openConnection();
					InputStream inputStream = new BufferedInputStream(wallpaperURL.openStream(), 10240);
					File dropboxDir = new File(context.getFilesDir().getAbsolutePath() + "/dropbox_download");
					if (!dropboxDir.exists())
						dropboxDir.mkdir();
					File jsonFile = new File(dropboxDir, name);
					FileOutputStream outputStream = new FileOutputStream(jsonFile);

					byte buffer[] = new byte[1024];
					int dataSize;
					while ((dataSize = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, dataSize);
					}
					outputStream.close();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					MyLog.eGeneral("Save file error: " + e);
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					Toast toast = Toast.makeText(context, "New camping group has been added to your device.",
							Toast.LENGTH_SHORT);
					toast.show();
				}
				super.onPostExecute(result);
			}

		}.execute();

	}
}