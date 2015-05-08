package com.example.groupcamping.utis;

import android.telephony.SmsManager;

public class SmsHelper {
	
	public static void sendSms(String phoneNumber, String message) {
		try {
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(phoneNumber, null, message, null, null);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
