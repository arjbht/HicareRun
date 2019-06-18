package com.ab.hicarerun.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.HandShakeModel.ContinueHandShakeRequest;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.utils.SharedPreferencesUtility;


import java.security.Provider;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmResults;

public class ServiceLocationSend extends Service implements LocationListener {

    String str_lat, str_lng, strUsername = "", userId = "", DeviceIMEINumber, DeviceTime, BatteryStatistics, PhoneMake, DeviceName;
    String timeIntervalValue, enableTraceValue;
    Boolean IsLoggedIn, IsGPSConnected;
    double lat, lng;
    private static final int CONTINUT_HANDSHAKE_REQUEST = 1000;
    LocationManager locationManager;
    Location location;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.e("Service Started", "Service Started");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                Log.e("Running", "Running call");

                final Location location = getLastKnownLocation();
                if (location != null) {

                    lat = (double) (location.getLatitude());
                    lng = (double) (location.getLongitude());
                    Log.e("lat", "Lattitude:" + lat);
                    Log.e("long", "Longitude:" + lng);
                    onLocationChanged(location);

                } else {

                    Log.e("lat_long ", "null");
                    onLocationChanged(location);
                }

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.e("Called to GPS", "APi Hit ifff");
                    getDeviceDetails(ServiceLocationSend.this, true, false);
                    getContinueHandShake(ServiceLocationSend.this);

                } else {
                    Log.e("Called to GPS", "APi Hit else");
                    getDeviceDetails(ServiceLocationSend.this, true, true);
                    getContinueHandShake(ServiceLocationSend.this);
                }
            }
        });

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.e("onDestroy", "onDestroy Call");

        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

//        Intent itAlarm = new Intent(this, ServiceLocationSend.class);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0, itAlarm, 0);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, 3);
//        AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarme.cancel(pendingIntent);


            Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
            restartServiceIntent.setPackage(getPackageName());

            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 3000,
                    restartServicePendingIntent);

            super.onTaskRemoved(rootIntent);


    }

    void getDeviceDetails(Context context, Boolean isloggedInsuccess, Boolean isGPSEnabled) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");


        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ServiceLocationSend.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        str_lat = String.valueOf(lat);
        str_lng = String.valueOf(lng);

        strUsername = SharedPreferencesUtility.getPrefString(context, SharedPreferencesUtility.PREF_USERNAME);
        userId = SharedPreferencesUtility.getPrefString(context, SharedPreferencesUtility.PREF_USERID);

        try {

            DeviceIMEINumber = telephonyManager.getDeviceId();

        } catch (Exception e) {
            e.printStackTrace();
        }

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        System.out.print(version);

        DeviceTime = dateformat.format(c.getTime());
        BatteryStatistics = String.valueOf(getMyBatteryLevel(ServiceLocationSend.this));
        PhoneMake = Build.VERSION.SDK_INT + "," + Build.MODEL + "," + Build.PRODUCT + "," + Build.MANUFACTURER + "," + version;
        DeviceName = Build.DEVICE;

        if (isloggedInsuccess) {
            IsLoggedIn = true;
        } else {
            IsLoggedIn = false;
        }


        if (isGPSEnabled) {
            IsGPSConnected = true;
        } else {
            IsGPSConnected = false;
        }


        Log.e("TAG", "Lat" + str_lat);
        Log.e("TAG", "Long" + str_lng);
        Log.e("TAG", "str_Username: " + strUsername);
        Log.e("TAG", "IMEI: " + DeviceIMEINumber);
        Log.e("TAG", "Battery: " + BatteryStatistics);
        Log.e("TAG", "Phone Make: " + PhoneMake);
        Log.e("TAG", "Device Name: " + DeviceName);
        Log.e("TAG", "isLogeedinnn: " + IsLoggedIn);
        Log.e("TAG", "GPS: " + IsGPSConnected);

    }


    void getContinueHandShake(Context context) {

        try {

            Log.e("TAG", "Lat" + str_lat);
            Log.e("TAG", "Long" + str_lng);
            Log.e("TAG", "tech_id: " + userId);
            Log.e("TAG", "UserId: " + userId);
            Log.e("TAG", "str_Username: " + strUsername);
            Log.e("TAG", "IMEI: " + DeviceIMEINumber);
            Log.e("TAG", "Battery: " + BatteryStatistics);
            Log.e("TAG", "Phone Make: " + PhoneMake);
            Log.e("TAG", "Device Name: " + DeviceName);
            Log.e("TAG", "Device Time: " + DeviceTime);
            Log.e("TAG", "isLogeedinnn: " + IsLoggedIn);
            Log.e("TAG", "GPS: " + IsGPSConnected);


            NetworkCallController controller = new NetworkCallController();
            ContinueHandShakeRequest request = new ContinueHandShakeRequest();
            request.setLatitude(str_lat);
            request.setLongitude(str_lng);
            request.setTechId(userId);
            request.setUserId(userId);
            request.setUserName(strUsername);
            request.setDeviceIMEINumber(DeviceIMEINumber);
            request.setDeviceTime(DeviceTime);
            request.setBatteryStatistics(BatteryStatistics);
            request.setPhoneMake(PhoneMake);
            request.setDeviceName(DeviceName);
            request.setLoggedIn(IsLoggedIn);
            request.setGPSConnected(IsGPSConnected);
            controller.setListner(new NetworkResponseListner() {
                @Override
                public void onResponse(int requestCode, Object response) {
                    Log.e("response", response.toString());
                }

                @Override
                public void onFailure(int requestCode) {

                }
            });
            controller.getContinueHandShake(CONTINUT_HANDSHAKE_REQUEST, request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getMyBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryIntent.getIntExtra("level", -1);
    }


    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {
            lat = 0.0;
            lng = 0.0;
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public Location getLastKnownLocation() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
