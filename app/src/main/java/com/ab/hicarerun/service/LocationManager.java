package com.ab.hicarerun.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.ab.hicarerun.service.listner.LocationManagerListner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, LocationListener

{
    private static final String TAG = LocationManager.class.getSimpleName();

    private static final int MINUTE = 1000 * 60;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // default value is false but user can change it
    private String mLastLocationUpdateTime;
    // fetched location time
    private String locationProvider;
    // source of fetched location

    private Location mLastLocationFetched;
    // location fetched
    private Location mLocationFetched;
    // location fetched
    private Location networkLocation;
    private Location gpsLocation;

    private int mLocationPiority;
    private long mLocationFetchInterval;
    private long mFastestLocationFetchInterval;

    private Activity mActivity;
    // activity context
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationManagerListner mLocationManagerListner;

    private android.location.LocationManager locationManager;
    private android.location.LocationListener locationListener;

    public boolean isGPSEnabled;
    public boolean isNetworkEnabled;

    private PROVIDER mProviderType;
    private RESTRICTION mForceNetworkProviders;

    // send location
    private LocationManager(Activity activity, PROVIDER provider, int locationPriority,
                            long locationFetchInterval, long fastestLocationFetchInterval, LocationManagerListner listner,
                            RESTRICTION restriction) {
        this.mActivity = activity;
        this.mProviderType = provider;
        this.mLocationFetchInterval = locationFetchInterval;
        this.mFastestLocationFetchInterval = fastestLocationFetchInterval;
        this.mLocationManagerListner = listner;
        this.mLocationPiority = locationPriority;
        this.mForceNetworkProviders = restriction;
        initSmartLocationManager();
    }

    public LocationManager(Activity activity){
        this.mActivity = activity;
        initSmartLocationManager();
    }


    private void initSmartLocationManager() {

        // 1) ask for permission for Android 6 above to avoid crash
        // 2) check if gps is available
        // 3) get location using awesome strategy

        askLocationPermission();                            // for android version 6 above
        checkNetworkProviderEnable();                       //

        if (isGooglePlayServicesAvailable())                // if googleplay services available
        {
            initLocationObjts();                            // init obj for google play service and start fetching location
        } else {
            getLocationUsingAndroidAPI();                   // otherwise get location using Android API
        }
    }

    private void initLocationObjts() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(mLocationPiority)
                .setInterval(mLocationFetchInterval)                    // 10 seconds, in milliseconds
                .setFastestInterval(mFastestLocationFetchInterval);     // 1 second, in milliseconds

        if (mGoogleApiClient == null) {
            mGoogleApiClient =
                    new GoogleApiClient.Builder(mActivity.getApplicationContext()).addConnectionCallbacks(
                            this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        startLocationFetching();                                        // connect google play services to fetch location
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                    this);
            getLocationUsingAndroidAPI();
        } else {
            setNewLocation(getBetterLocation(location, mLocationFetched), mLocationFetched);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            getLastKnownLocation();
        } else {
            setNewLocation(getBetterLocation(location, mLocationFetched), mLocationFetched);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mActivity,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST); // Start an Activity that tries to resolve the error
                getLocationUsingAndroidAPI();                                                                // try to get location using Android API locationManager
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG,
                    "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void setNewLocation(Location location, Location oldLocation) {
        if (location != null) {
            mLastLocationFetched = oldLocation;
            mLocationFetched = location;
            mLastLocationUpdateTime = DateFormat.getTimeInstance().format(new Date());
            locationProvider = location.getProvider();
            mLocationManagerListner.locationFetched(location, mLastLocationFetched,
                    mLastLocationUpdateTime, location.getProvider());
        }
    }

    private void getLocationUsingAndroidAPI() {
        // Acquire a reference to the system Location Manager
        locationManager =
                (android.location.LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

        setLocationListner();
        captureLocation();
    }

    public void captureLocation() {
        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            if (mProviderType == PROVIDER.GPS_PROVIDER) {
                locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0,
                        locationListener);
            } else if (mProviderType == PROVIDER.NETWORK_PROVIDER) {
                locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 0,
                        0, locationListener);
            } else {
                locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 0,
                        0, locationListener);
                locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0,
                        locationListener);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setLocationListner() {
        // Define a listener that responds to location updates
        locationListener = new android.location.LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (location == null) {
                    getLastKnownLocation();
                } else {
                    setNewLocation(getBetterLocation(location, mLocationFetched), mLocationFetched);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    public Location getAccurateLocation() {
        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try {
            gpsLocation =
                    locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
            networkLocation =
                    locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
            Location newLocalGPS, newLocalNetwork;
            if (gpsLocation != null || networkLocation != null) {
                newLocalGPS = getBetterLocation(mLocationFetched, gpsLocation);
                newLocalNetwork = getBetterLocation(mLocationFetched, networkLocation);
                setNewLocation(getBetterLocation(newLocalGPS, newLocalNetwork), mLocationFetched);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return mLocationFetched;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                this);
    }

    public void startLocationFetching() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }

    public void pauseLocationFetching() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    public void abortLocationFetching() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();

            // Remove the listener you previously added
            if (locationManager != null && locationListener != null) {
                if (Build.VERSION.SDK_INT >= 23
                        && ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    locationManager.removeUpdates(locationListener);
                    locationManager = null;
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
    }

    public void resetLocation() {
        mLocationFetched = null;
        mLastLocationFetched = null;
        networkLocation = null;
        gpsLocation = null;
    }

    //  Android M Permission check
    public void askLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(
                            "Please allow all permissions in App Settings for additional functionality.")
                            .setCancelable(false)
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                                    @SuppressWarnings("unused") final int id) {
                                    Toast.makeText(mActivity, "Welcome", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog,
                                                    @SuppressWarnings("unused") final int id) {
                                    // Permission denied
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                    }, PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    public void checkNetworkProviderEnable() {
        locationManager =
                (android.location.LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        isNetworkEnabled =
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            buildAlertMessageTurnOnLocationProviders(
                    "Your location providers seems to be disabled, please enable it", "OK", "Cancel");
        } else if (!isGPSEnabled && mForceNetworkProviders == RESTRICTION.ONLY_GPS_LOCATION) {
            buildAlertMessageTurnOnLocationProviders("Your GPS seems to be disabled, please enable it",
                    "OK", "Cancel");
        } else if (!isNetworkEnabled && mForceNetworkProviders == RESTRICTION.ONLY_NETWORK_LOCATION) {
            buildAlertMessageTurnOnLocationProviders(
                    "Your Network location provider seems to be disabled, please enable it", "OK", "Cancel");
        }
    }

    private void buildAlertMessageTurnOnLocationProviders(final String message, String positiveButtonText,
                                                          String negativeButtonText) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        Intent mIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(mIntent);
                        Activity activity = (Activity) mActivity;
                        activity.finishAffinity();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        // Location Service are not turned off
                        dialog.cancel();
                        Activity activity = (Activity) mActivity;
                        activity.finishAffinity();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    public Location getLastKnownLocation() {
        locationProvider = android.location.LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = null;
        // Or use LocationManager.GPS_PROVIDER
        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return lastKnownLocation;
        }
        try {
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            return lastKnownLocation;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return lastKnownLocation;
    }

    public boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);

        if (status == ConnectionResult.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected Location getBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return location;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > (2 * MINUTE);
        boolean isSignificantlyOlder = timeDelta < -(2 * MINUTE);
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return location;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider =
                isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return location;
        } else if (isNewer && !isLessAccurate) {
            return location;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return location;
        }
        return currentBestLocation;
    }

    /**
     * Checks whether two providers are the same
     */

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public boolean isLocationAccurate(Location location) {
        if (location.hasAccuracy()) {
            return true;
        } else {
            return false;
        }
    }

    public enum PROVIDER {
        ALL_PROVIDER, NETWORK_PROVIDER, GPS_PROVIDER
    }

    public enum RESTRICTION {
        NONE, ONLY_GPS_LOCATION, ONLY_NETWORK_LOCATION
    }

    public static class Builder {

        private Activity mActivity;
        private PROVIDER mProviderType;
        private int mLocationPiority;
        private RESTRICTION mForceNetworkProviders;
        private int mLocationFetchInterval;
        private int mFastestLocationFetchInterval;
        private LocationManagerListner mLocationManagerListner;

        public Builder(Activity activity) {
            this.mActivity = activity;
            this.mProviderType = PROVIDER.ALL_PROVIDER;
            this.mLocationPiority = LocationRequest.PRIORITY_HIGH_ACCURACY;
            this.mForceNetworkProviders = RESTRICTION.NONE;
            this.mLocationFetchInterval = 2 * MINUTE;
            this.mFastestLocationFetchInterval = MINUTE;
            this.mLocationManagerListner = null;
        }

        public void setProvider(PROVIDER provider) {
            this.mProviderType = provider;
        }

        public void setLocationPriority(int priority) {
            this.mLocationPiority = priority;
        }

        public void setRestriction(RESTRICTION restriction) {
            this.mForceNetworkProviders = restriction;
        }

        public void setLocationListner(LocationManagerListner listner) {
            this.mLocationManagerListner = listner;
        }

        public LocationManager build() {
            return new LocationManager(mActivity, mProviderType, mLocationPiority, mLocationFetchInterval,
                    mFastestLocationFetchInterval, mLocationManagerListner, mForceNetworkProviders);
        }
    }


}


