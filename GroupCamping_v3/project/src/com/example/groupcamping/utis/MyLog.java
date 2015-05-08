package com.example.groupcamping.utis;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class MyLog {

	private static final String GENERAL_TAG = "general_TAG";

	/**
	 * log infor tag
	 * 
	 * @param msg
	 *            message
	 */
	public static void iGeneral(String msg) {
		Log.i(GENERAL_TAG, "" + msg);
	}

	/**
	 * log error tag
	 * 
	 * @param msg
	 *            message
	 */
	public static void eGeneral(String msg) {
		Log.e(GENERAL_TAG, "" + msg);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static void logDeviceInfo(Context context) {

		int Measuredwidth = 0;
		int Measuredheight = 0;
		Point size = new Point();
		WindowManager w = ((Activity) context).getWindowManager();
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			w.getDefaultDisplay().getSize(size);
			Measuredwidth = size.x;
			Measuredheight = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			Measuredwidth = d.getWidth();
			Measuredheight = d.getHeight();
		}

		iGeneral("DPI: " + displayMetrics.density);
		iGeneral("Width: " + Measuredwidth / displayMetrics.density + "dp - " + Measuredwidth + " px");
		iGeneral("Height: " + Measuredheight / displayMetrics.density + "dp - " + Measuredheight + " px");

	}

	/**
	 * 
	 */
	public static void logToFile(Exception ex, String response, String jsonData) {

		try {
			// create log content
			String stringToWrite = "";
			if (ex != null)
				stringToWrite += "Error:\n" + ex + "\n\n";
			if (jsonData != null)
				stringToWrite += "JsonData:\n" + jsonData + "\n\n";
			if (response != null)
				stringToWrite += "API Response:\n" + response;

			// create app dir
			File appDir = new File(Environment.getExternalStorageDirectory() + "/sfm30");
			if (!appDir.exists()) {
				appDir.mkdir();
			}
			// log dir
			File logDir = new File(appDir, "logs");
			if (!logDir.exists()) {
				logDir.mkdir();
			}

			// log file
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS", Locale.getDefault());
			String fileName = "log-" + sdf.format(new Date()) + ".txt";
			String filePath = logDir.getAbsolutePath() + "/" + fileName;
			FileWriter fWriter = new FileWriter(filePath, true);
			fWriter.write(stringToWrite);
			fWriter.flush();
			fWriter.close();
		} catch (Exception e) {
		}
	}

}
