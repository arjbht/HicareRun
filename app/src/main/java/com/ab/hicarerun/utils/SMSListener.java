package com.ab.hicarerun.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.ab.hicarerun.handler.Common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arjun Bhatt on 6/24/2019.
 */
public class SMSListener extends BroadcastReceiver {
    private static Common.OTPListener mListener; // this listener will do the magic of throwing the extracted OTP to all the bound views.

    @Override
    public void onReceive(Context context, Intent intent) {

        // this function is trigged when each time a new SMS is received on device.

        Bundle data = intent.getExtras();

        Object[] pdus = new Object[0];
        if (data != null) {
            pdus = (Object[]) data.get("pdus"); // the pdus key will contain the newly received SMS
        }

        if (pdus != null) {
            Pattern p = Pattern.compile("(|^)\\d{6}");

            for (Object pdu : pdus) { // loop through and pick up the SMS of interest
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String messageBody = smsMessage.getMessageBody();
                // your custom logic to filter and extract the OTP from relevant SMS - with regex or any other way.
//                Matcher m = p.matcher(messageBody);
//                if (m.find()) {
//                     otp = messageBody.replace("is your HiCare verification code", "");
                String otp = messageBody.substring(0,6);
//                }
                if (mListener!=null)
                mListener.onOTPReceived(otp);
                break;
            }
        }
    }

    public static void bindListener(Common.OTPListener listener) {
        mListener = listener;
    }

    public static void unbindListener() {
        mListener = null;
    }
}
