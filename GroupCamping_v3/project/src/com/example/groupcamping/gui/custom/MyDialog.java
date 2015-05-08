package com.example.groupcamping.gui.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.groupcamping.R;
import com.example.groupcamping.utis.MyLog;

public class MyDialog {

	private static boolean isDialogShowing = false;
	private static Dialog mDialog;

	private static String mTitle;
	private static String mMessage;
	private static boolean mCancelAble;
	private static boolean mShowCancelButton;
	private static String mOkString;
	private static String mCancelString;
	private static Runnable mOkRunnable;
	private static Runnable mCancelRunnable;

	/**
	 * show default alert dialog with title and message
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param okRunnable
	 */
	public static void showDialog(Context context, String title, String message, boolean cancelAble,
			final Runnable okRunnable) {

		// if (isDialogShowing)
		// return;
		//
		// mDialog = createDialog(context, title, message, cancelAble);
		//
		// // ok button
		// Button btnOk = (Button) mDialog.findViewById(R.id.btn_dialog_ok);
		// btnOk.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (okRunnable != null)
		// okRunnable.run();
		// mDialog.dismiss();
		// }
		// });
		//
		// // hide middle line
		// mDialog.findViewById(R.id.view_middle_line).setVisibility(View.GONE);
		//
		// // hide cancel button
		// mDialog.findViewById(R.id.btn_dialog_cancel).setVisibility(View.GONE);
		//
		// mDialog.show();
		showDialog(context, title, message, cancelAble, null, null, okRunnable, null, false);
	}

	/**
	 * Show custom dialog with title & message (OK & Cancel button)
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param okRunnable
	 * @param cancelRunnable
	 * @param cancelAble
	 */
	public static void showDialog(Context context, String title, String message, boolean cancelAble, String okString,
			String cancelString, final Runnable okRunnable, final Runnable cancelRunnable, boolean showCancelButton) {

		if (isDialogShowing) {
			try {
				mDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				MyLog.eGeneral("Hide dialog error: " + e);
			}
		}

		mOkString = okString;
		mCancelString = cancelString;
		mOkRunnable = okRunnable;
		mCancelRunnable = cancelRunnable;
		mShowCancelButton = showCancelButton;

		mDialog = createDialog(context, title, message, cancelAble);

		// ok button
		Button btnOk = (Button) mDialog.findViewById(R.id.btn_dialog_ok);
		if (okString != null)
			btnOk.setText(okString);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				if (okRunnable != null)
					okRunnable.run();
			}
		});

		// cancel button
		if (!showCancelButton) {
			// hide middle line
			mDialog.findViewById(R.id.view_middle_line).setVisibility(View.GONE);
			// hide cancel button
			mDialog.findViewById(R.id.btn_dialog_cancel).setVisibility(View.GONE);
		} else {
			Button btnCancel = (Button) mDialog.findViewById(R.id.btn_dialog_cancel);
			if (cancelString != null)
				btnCancel.setText(cancelString);
			btnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDialog.dismiss();
					if (cancelRunnable != null)
						cancelRunnable.run();
				}
			});
		}

		mDialog.show();
	}

	/**
	 * Show custom dialog with title & message (OK & Cancel button)
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param okRunnable
	 * @param cancelRunnable
	 * @param cancelAble
	 */
	public static void showDialog(Context context, String title, String message, boolean cancelAble,
			final Runnable okRunnable, final Runnable cancelRunnable) {

		showDialog(context, title, message, cancelAble, null, null, okRunnable, cancelRunnable, true);
	}

	/**
	 * Create dialog with title, message, cancelAble
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param cancelAble
	 * @return
	 */
	private static Dialog createDialog(final Context context, String title, String message, boolean cancelAble) {

		//
		mTitle = title;
		mMessage = message;
		mCancelAble = cancelAble;

		mDialog = new Dialog(context, R.style.my_dialog_theme);
		mDialog.setCancelable(cancelAble);
		mDialog.setContentView(R.layout.my_dialog);

		mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);

		// resize dialog
		resizeDialog(context);

		// set title & message
		TextView tvMessage = (TextView) mDialog.findViewById(R.id.tv_dialog_message);
		tvMessage.setText(message);
		TextView tvTitle = (TextView) mDialog.findViewById(R.id.tv_dialog_title);
		tvTitle.setText(title);

		mDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				isDialogShowing = false;
			}
		});
		mDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				isDialogShowing = true;
			}
		});

		return mDialog;
	}

	/**
	 * get window screen size in pixel
	 * 
	 * @param context
	 * @return
	 */
	public static Point getWindowSizeInPixel(Context context) {

		Point size = new Point();
		WindowManager w = ((Activity) context).getWindowManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			w.getDefaultDisplay().getSize(size);
		} else {
			Display d = w.getDefaultDisplay();
			size.x = d.getWidth();
			size.y = d.getHeight();
		}
		return size;
	}

	/**
	 * 
	 * @param context
	 * @param dialog
	 */
	public static void resizeDialog(final Context context) {
		try {
			if (mDialog == null)
				return;
			Point screenSize = getWindowSizeInPixel(context);

			WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			Window window = mDialog.getWindow();
			params.copyFrom(window.getAttributes());

			// set width of dialog is 80% screen width in portrait or 70% in landscape
			params.width = (int) (screenSize.x <= screenSize.y ? screenSize.x * 0.8f : screenSize.x * 0.7f);
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			window.setAttributes(params);
		} catch (Exception e) {
			// TODO: handle exception
			MyLog.eGeneral("MyDialog - resizeDialog error: " + e);
		}
	}

	/**
	 * 
	 */
	public static void reShowDialog(final Context context) {
		if (isDialogShowing) {
			isDialogShowing = false;
			showDialog(context, mTitle, mMessage, mCancelAble, mOkString, mCancelString, mOkRunnable, mCancelRunnable,
					mShowCancelButton);
		}
	}

}
