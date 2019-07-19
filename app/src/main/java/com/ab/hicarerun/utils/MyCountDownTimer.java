package com.ab.hicarerun.utils;

import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by Arjun Bhatt on 7/5/2019.
 */
public abstract class MyCountDownTimer  extends CountDownTimer {
    private static final long INTERVAL_MS = 1000;
    private long duration;

    protected MyCountDownTimer(long durationMs) {
        super(durationMs, INTERVAL_MS);
        this.duration = durationMs;
    }

    public abstract void onTick(String Hours,String Minutes,String Seconds);

    @Override
    public void onTick(long msUntilFinished) {


        int days = (int) ((msUntilFinished / 1000) / 86400);
                int hours = (int) (((msUntilFinished / 1000) - (days * 86400)) / 3600);
                int minutes = (int) (((msUntilFinished / 1000) - ((days * 86400) + (hours * 3600))) / 60);
                int seconds = (int) ((msUntilFinished / 1000) % 60);
                Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
                an.setInterpolator(new LinearInterpolator());
                an.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                an.setRepeatMode(Animation.INFINITE); //animation will start from end point once ended.
                an.setFillAfter(true);

                String Days = String.format("%02d", days);
                String Hours = String.format("%02d", hours);
                String Minutes = String.format("%02d", minutes);
                String Seconds = String.format("%02d", seconds);


        int second = (int) ((duration - msUntilFinished) / 1000);
        onTick(Hours , Minutes , Seconds);
    }

    @Override
    public void onFinish() {
        onTick(duration / 1000);
    }
}