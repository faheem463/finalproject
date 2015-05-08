package com.example.groupcamping.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.provider.Telephony.Sms;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.example.groupcamping.utis.*;

/**
 * Here we show uploading a file in a background thread, trying to show typical exception handling and flow of control
 * for an app that uploads a file from Dropbox.
 */
public class UploadFile extends AsyncTask<Void, Long, Boolean> {

	private DropboxAPI<?> mApi;
	private String mPath;
	private File mFile;

	private long mFileLen;
	private UploadRequest mRequest;
	private Context mContext;
	private final ProgressDialog mDialog;

	private String mErrorMsg;
	private ArrayList<String> mPhoneNumber;

	public UploadFile(Context context, DropboxAPI<?> api, String dropboxPath, File file, ArrayList<String> phoneNumbers) {
		// We set the context this way so we don't accidentally leak activities
		mContext = context.getApplicationContext();

		mFileLen = file.length();
		mApi = api;
		mPath = dropboxPath;
		mFile = file;
		this.mPhoneNumber = phoneNumbers;

		mDialog = new ProgressDialog(context);
		mDialog.setMax(100);
		mDialog.setCancelable(false);
		mDialog.setMessage("Uploading " + file.getName());
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setProgress(0);
		mDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// This will cancel the putFile operation
				mRequest.abort();
			}
		});
		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			// By creating a request, we get a handle to the putFile operation,
			// so we can cancel it later if we want to
			FileInputStream fis = new FileInputStream(mFile);
			String path = mPath + mFile.getName();
			mRequest = mApi.putFileOverwriteRequest(path, fis, mFile.length(), new ProgressListener() {
				@Override
				public long progressInterval() {
					// Update the progress bar every half-second or so
					return 500;
				}

				@Override
				public void onProgress(long bytes, long total) {
					publishProgress(bytes);
				}
			});

			// Get the metadata for a directory
			Entry dirent;
			try {
				dirent = mApi.metadata(Defines.DROPBOX_DIR, 1000, null, true, null);

				for (Entry ent : dirent.contents) {

					String shareAddress = null;
					if (!ent.isDir) {
						if (ent.fileName().equals(mFile.getName())) {

							DropboxLink shareLink = mApi.share(ent.path);
							shareAddress = getShareURL(shareLink.url).replaceFirst("https://www", "https://dl");
							// shareAddress = shareLink.url;
							MyLog.iGeneral("dropbox share link " + shareAddress);
							try {

								String message = shareAddress.substring(Defines.SHARE_URL.length(),
										shareAddress.lastIndexOf("/"));
								message = Defines.DROPBOX_MESSAGE + "\n" + shareAddress + "\n" + mFile.getName();
								for (String phoneNumber : mPhoneNumber) {
									try {
										SmsHelper.sendSms(phoneNumber, message);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (mRequest != null) {
				mRequest.upload();
				return true;
			}
		} catch (DropboxUnlinkedException e) {
			// This session wasn't authenticated properly or user unlinked
			mErrorMsg = "This app wasn't authenticated properly.";
		} catch (DropboxFileSizeException e) {
			// File size too big to upload via the API
			mErrorMsg = "This file is too big to upload";
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = "Upload canceled";
		} catch (DropboxServerException e) {
			// Server-side exception. These are examples of what could happen,
			// but we don't do anything special with them here.
			if (e.error == DropboxServerException._401_UNAUTHORIZED) {
				// Unauthorized, so we should unlink them. You may want to
				// automatically log the user out in this case.
			} else if (e.error == DropboxServerException._403_FORBIDDEN) {
				// Not allowed to access this
			} else if (e.error == DropboxServerException._404_NOT_FOUND) {
				// path not found (or if it was the thumbnail, can't be
				// thumbnailed)
			} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
				// user is over quota
			} else {
				// Something else
			}
			// This gets the Dropbox error, translated into the user's language
			mErrorMsg = e.body.userError;
			if (mErrorMsg == null) {
				mErrorMsg = e.body.error;
			}
		} catch (DropboxIOException e) {
			// Happens all the time, probably want to retry automatically.
			mErrorMsg = "Network error.  Try again.";
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			mErrorMsg = "Dropbox error.  Try again.";
		} catch (DropboxException e) {
			// Unknown error
			mErrorMsg = "Unknown error.  Try again.";
		} catch (FileNotFoundException e) {
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
		mDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (result) {
			showToast("Group successfully shared");
		} else {
			showToast(mErrorMsg);
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}

	String getShareURL(String strURL) {
		URLConnection conn = null;
		String redirectedUrl = null;
		try {
			URL inputURL = new URL(strURL);
			conn = inputURL.openConnection();
			conn.connect();

			InputStream is = conn.getInputStream();
			System.out.println("Redirected URL: " + conn.getURL());
			redirectedUrl = conn.getURL().toString();
			is.close();

		} catch (MalformedURLException e) {
			MyLog.eGeneral("Please input a valid URL");
		} catch (IOException ioe) {
			MyLog.eGeneral("Can not connect to the URL");
		}

		return redirectedUrl;
	}
}
