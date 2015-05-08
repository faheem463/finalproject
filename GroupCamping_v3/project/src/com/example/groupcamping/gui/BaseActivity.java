package com.example.groupcamping.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.example.groupcamping.MyApplication;
import com.example.groupcamping.R;
import com.example.groupcamping.dropbox.Defines;
import com.example.groupcamping.utis.MyToast;

public class BaseActivity extends Activity {

	// You don't need to change these, leave them alone.
	protected static final String ACCOUNT_PREFS_NAME = "prefs";
	protected static final String ACCESS_KEY_NAME = "ACCESS_KEY";
	protected static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	protected Context mContext;
	protected boolean isFinish;
	protected MyToast myToast;

	protected DropboxAPI<AndroidAuthSession> mApi;
	protected boolean mLoggedIn;

	protected boolean isDropbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myToast = new MyToast(this);
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);
		mContext = this;
		if (getIntent() != null) {
			isDropbox = getIntent().getBooleanExtra("GROUP_TYPE", false);
		}
	}

	protected String readFileAsString(String filePath) {
		try {

			StringBuffer fileData = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
			return fileData.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	protected void writeToFile(String data, String filePAth) {
		try {
			File file = new File(filePAth);
			FileOutputStream stream = new FileOutputStream(file);
			try {
			    stream.write(data.getBytes());
			} finally {
			    stream.close();
			}
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.come_from_right, R.anim.out_to_left);
	}

	@Override
	public void finish() {
		isFinish = true;
		super.finish();
	}

	@Override
	protected void onResume() {
		MyApplication.activity = this;
		super.onResume();
	}

	protected AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(Defines.APP_KEY, Defines.APP_SECRET);

		AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
		loadAuth(session);
		return session;
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local store, rather than storing user name
	 * & password, and re-authenticating each time (which is not to be done, ever).
	 */
	protected void loadAuth(AndroidAuthSession session) {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key == null || secret == null || key.length() == 0 || secret.length() == 0)
			return;

		if (key.equals("oauth2:")) {
			// If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
			session.setOAuth2AccessToken(secret);
		} else {
			// Still support using old OAuth 1 tokens.
			session.setAccessTokenPair(new AccessTokenPair(key, secret));
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.come_from_left, R.anim.out_to_right);
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local store, rather than storing user name
	 * & password, and re-authenticating each time (which is not to be done, ever).
	 */
	protected void storeAuth(AndroidAuthSession session) {
		// Store the OAuth 2 access token, if there is one.
		String oauth2AccessToken = session.getOAuth2AccessToken();
		if (oauth2AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
			Editor edit = prefs.edit();
			edit.putString(ACCESS_KEY_NAME, "oauth2:");
			edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
			edit.commit();
			return;
		}
		// Store the OAuth 1 access token, if there is one. This is only necessary if
		// you're still using OAuth 1.
		AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
		if (oauth1AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
			Editor edit = prefs.edit();
			edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
			edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
			edit.commit();
			return;
		}
	}

	protected void logOutDropbox() {
		// Remove credentials from the session
		mApi.getSession().unlink();

		// Clear our stored keys
		clearKeys();
		// Change UI state to display logged out version
		mLoggedIn = false;
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

}
