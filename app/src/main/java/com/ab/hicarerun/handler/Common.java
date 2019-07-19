package com.ab.hicarerun.handler;

/**
 * Created by Arjun Bhatt on 6/24/2019.
 */
public interface Common {
        interface OTPListener {
                void onOTPReceived(String otp);
        }
}
