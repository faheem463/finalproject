package com.example.groupcamping.gui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.example.groupcamping.R;
import com.example.groupcamping.dropbox.Defines;
import com.example.groupcamping.utis.MyLog;
import com.example.groupcamping.utis.MyToast;
import com.example.groupcamping.utis.MyUtils;

public class SplashActivity extends BaseActivity implements OnClickListener {

	MyToast myToast;
	Button btnAuth;
	ProgressBar progressBar;
	private long startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startTime = System.currentTimeMillis();
		setContentView(R.layout.activity_splash);
		initView();

		authenDropbox();

		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {

				MyUtils.readItemsDataFromRaw(mContext);
				return null;
			}

			protected void onPostExecute(Object result) {

				setLoggedIn(mApi.getSession().isLinked());
				if (mLoggedIn) {
					startCampingMain();
				} else {

				}
			};
		}.execute();
	}

	private void initView() {

		myToast = new MyToast(this);
		//
		btnAuth = (Button) findViewById(R.id.btn_auth);
		btnAuth.setOnClickListener(this);
		//
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
	}

	private void checkAppKeySetup() {

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + Defines.APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			myToast.showToast("URL scheme in your app's " + "manifest is not set up correctly. You should have a "
					+ "com.dropbox.client2.android.AuthActivity with the " + "scheme: " + scheme);
			finish();
		}
	}

	private void authenDropbox() {
		// We create a new AuthSession so that we can use the Dropbox API.
		checkAppKeySetup();
	}

	private void startCampingMain() {

		if (mLoggedIn) {
			long delay = 500 - (System.currentTimeMillis() - startTime);
			if (delay < 0)
				delay = 0;
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent i = new Intent(mContext, MainActivity.class);
					startActivity(i);
					finish();
				}
			}, delay);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				storeAuth(session);
				setLoggedIn(true);
				startCampingMain();
			} catch (IllegalStateException e) {
				myToast.showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
				MyLog.eGeneral("Error authenticating: " + e);
			}
		}
	}

	/**
	 * Convenience function to change UI state based on being logged in
	 */
	private void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
		if (loggedIn) {
			btnAuth.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			// myToast.showToast("Authen dropbox error.");
			MyLog.iGeneral("Do not authen dropbox");
			btnAuth.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.btn_auth :
				// Start the remote authentication
				mApi.getSession().startOAuth2Authentication(mContext);
				break;

			default :
				break;
		}
	}

}