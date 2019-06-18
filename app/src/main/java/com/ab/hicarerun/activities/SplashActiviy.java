package com.ab.hicarerun.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivitySplashActiviyBinding;
import com.ab.hicarerun.fragments.LoginFragment;
import com.ab.hicarerun.service.LocationManager;
import com.ab.hicarerun.service.listner.LocationManagerListner;
import com.ab.hicarerun.utils.RuntimePermissionsActivity;
import com.ab.hicarerun.utils.SharedPreferencesUtility;
import com.splunk.mint.Mint;
import com.squareup.picasso.Picasso;

public class SplashActiviy extends AppCompatActivity implements LocationManagerListner {
    ActivitySplashActiviyBinding mActivitySplashBinding;
    private Location mLocation;
    private LocationManagerListner mListner;
    private static int SPLASH_TIME_OUT = 3000;
    private android.location.LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySplashBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_splash_activiy);


        LocationManager.Builder builder = new LocationManager.Builder(this);
        builder.setLocationListner(this);
        builder.build();
        Mint.initAndStartSession(this.getApplication(), "5623ed44");
        Picasso.with(getApplicationContext()).load(R.mipmap.splash).into(mActivitySplashBinding.imgSplash);
//        SplashActiviy.super.requestAppPermissions(new String[]{
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.INTERNET,
//                        Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA,
//                }, R.string.runtime_permissions_txt
//                , REQUEST_PERMISSIONS);

        splashScreen();


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash);
        mActivitySplashBinding.imgSplash.startAnimation(animation);
    }

//    @Override
//    public void onPermissionsGranted(int requestCode) {
//    }

    void splashScreen() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                locationManager =
                        (android.location.LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                if (/*locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                        && locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER*/
                        getmLocation() != null) {
                    if (SharedPreferencesUtility.getPrefBoolean(SplashActiviy.this, SharedPreferencesUtility.IS_USER_LOGIN)) {
                        startActivity(new Intent(SplashActiviy.this, HomeActivity.class).putExtra(HomeActivity.ARG_EVENT, "false"));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        finish();
                    } else {
                        startActivity(new Intent(SplashActiviy.this, LoginActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        finish();
                    }
                } else {
                    showLocationPopUp();
                }


            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time,
                                String locationProvider) {
        this.mLocation = mLocation;
        if (mListner != null) {
            mListner.locationFetched(mLocation, oldLocation, time, locationProvider);
        }
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void showLocationPopUp() {

        LocationManager.Builder builder = new LocationManager.Builder(this);
        builder.setLocationListner(this);
        builder.build();
        setLocationListner(this);
    }

    public void setLocationListner(LocationManagerListner locationListner) {
        this.mListner = locationListner;
    }


}
