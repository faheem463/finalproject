package com.example.groupcamping.dropbox;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;

public class DropboxHelper {

	// In the class declaration section:
	// private DropboxAPI<AndroidAuthSession> mDBApi;

	public static boolean updateFile(DropboxAPI<AndroidAuthSession> mDBApi, String dropboxFileName, String content) {
		
		// File file = new File("working-draft.txt");
		InputStream inputStream;
		try {
			// inputStream = new FileInputStream(file);
			inputStream = new ByteArrayInputStream(content.getBytes());
			Entry response = mDBApi.putFile(dropboxFileName, inputStream, content.length(), null, null);
			Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public static void downloadFile(DropboxAPI<AndroidAuthSession> mDBApi, String dropboxFile) {
		try {

			File file = new File("/magnum-opus.txt");
			FileOutputStream outputStream = new FileOutputStream(file);
			DropboxFileInfo info = mDBApi.getFile(dropboxFile, null, outputStream, null);
			Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}