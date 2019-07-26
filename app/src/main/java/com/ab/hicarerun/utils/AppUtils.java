package com.ab.hicarerun.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.AttendanceModel.AttendanceRequest;
import com.ab.hicarerun.network.models.GeneralModel.GeneralData;
import com.ab.hicarerun.network.models.GeneralModel.GeneralPaymentMode;
import com.ab.hicarerun.network.models.GeneralModel.GeneralTaskStatus;
import com.ab.hicarerun.network.models.HandShakeModel.HandShake;
import com.ab.hicarerun.network.models.LoggerModel.ErrorLog;
import com.ab.hicarerun.network.models.LoggerModel.ErrorLoggerModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;


public class AppUtils {

    private static final int CAM_REQ = 1000;
    private static final int HANDSHAKE_REQUEST = 2000;
    private static final int ERROR_REQUEST = 3000;


    public class LocationConstants {
        public static final int SUCCESS_RESULT = 0;

        public static final int FAILURE_RESULT = 1;


        public static final String PACKAGE_NAME = "com.sample.sishin.maplocation";

        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

        public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

        public static final String LOCATION_DATA_AREA = PACKAGE_NAME + ".LOCATION_DATA_AREA";
        public static final String LOCATION_DATA_CITY = PACKAGE_NAME + ".LOCATION_DATA_CITY";
        public static final String LOCATION_DATA_STREET = PACKAGE_NAME + ".LOCATION_DATA_STREET";


    }


    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            // Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public static void showOkActionAlertBox(Context context, String mStrMessage, DialogInterface.OnClickListener mClickListener) {
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(context);

        mBuilder.setMessage(mStrMessage);
        mBuilder.setPositiveButton("ok", mClickListener);
        mBuilder.setCancelable(false);
        mBuilder.create().show();
    }


    public static void showDownloadActionAlertBox(Context context,String title, String mStrMessage, DialogInterface.OnClickListener mClickListener) {
        android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        mBuilder.setIcon(R.mipmap.logo);
        mBuilder.setTitle(title);
        mBuilder.setMessage(mStrMessage);
        mBuilder.setPositiveButton("UPDATE", mClickListener);
        mBuilder.setCancelable(false);
        mBuilder.create().show();
    }

    public static void getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
        String formattedDate = dateFormat.format(date);
        System.out.println("Current time of the day using Calendar - 24 hour format: " + formattedDate);
    }


    public static String compareDates(String d1, String d2) {


        String date_result = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

//            Calendar cal = Calendar.getInstance();
//            cal.setTime(date2);
//            cal.add(Calendar.MINUTE, 15);
//            Date dateAfter = cal.getTime();

            System.out.println("Date1" + sdf.format(date1));
            System.out.println("Date2" + sdf.format(date2));
            System.out.println();

            if (date1.after(date2)) {


                    date_result = "afterdate";
                    System.out.println("Date1 is after Date2");


            }
            if (date1.before(date2)) {

                date_result = "beforedate";
                System.out.println("Date1 is before Date2");

            }

            if (date1.equals(date2)) {

                date_result = "equalsdate";
                System.out.println("Date1 is equal Date2");

            }
            System.out.println(date_result);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date_result;
    }

    public static String currentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();
        return dateFormat.format(date1);
    }

    public static void getDataClean() {
        try {
            Realm.getDefaultInstance().beginTransaction();
            Realm.getDefaultInstance().where(GeneralData.class).findAll().deleteAllFromRealm();
            Realm.getDefaultInstance().where(GeneralTaskStatus.class).findAll().deleteAllFromRealm();
            Realm.getDefaultInstance().where(GeneralPaymentMode.class).findAll().deleteAllFromRealm();
            Realm.getDefaultInstance().where(com.ab.hicarerun.network.models.GeneralModel.IncompleteReason.class).findAll().deleteAllFromRealm();
            Realm.getDefaultInstance().commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String reFormatDateTime(String dateIn, String format) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = simpleDateFormat.parse(dateIn);
        simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String millisTOHr(Long millis) throws ParseException {

        String hms = String.format("%02d hr %02d min", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(hms);
        return hms;
    }

    public static String reFormatTime(String time, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        Date date = simpleDateFormat.parse(time);
        simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static void getHandShakeCall(final String username, final Activity context) {
        try {
            NetworkCallController controller = new NetworkCallController();
            controller.setListner(new NetworkResponseListner() {
                @Override
                public void onResponse(int requestCode, Object data) {
                    SharedPreferencesUtility.savePrefBoolean(context,
                            SharedPreferencesUtility.IS_USER_LOGIN, true);
                    List<HandShake> items = (List<HandShake>) data;
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.putExtra(HomeActivity.ARG_HANDSHAKE, (Serializable) items);
                    intent.putExtra(HomeActivity.ARG_EVENT, true);
                    intent.putExtra(HomeActivity.ARG_USER, username);
                    context.startActivity(intent);
                    context.finish();
                }

                @Override
                public void onFailure(int requestCode) {

                }
            });
            controller.getHandShake(HANDSHAKE_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static AttendanceRequest getDeviceInfo(final Activity context, String encodedImage, String batteryStatistics, boolean isSkip) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");

        String userId = SharedPreferencesUtility.getPrefString(context, SharedPreferencesUtility.PREF_USERID);
        String strUsername = SharedPreferencesUtility.getPrefString(context, SharedPreferencesUtility.PREF_USERNAME);
        String DeviceIMEINumber = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        try {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
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

        String DeviceTime = dateformat.format(c.getTime());
        String PhoneMake = Build.VERSION.SDK_INT + "," + Build.MODEL + "," + Build.PRODUCT + "," + Build.MANUFACTURER + "," + version;
        String DeviceName = Build.DEVICE;
        AttendanceRequest request = new AttendanceRequest();
        request.setDeviceIMEINumber(DeviceIMEINumber);
        request.setDeviceName(DeviceName);
        request.setDeviceTime(DeviceTime);
        request.setGPSConnected(true);
        request.setLatitude(String.valueOf(((HomeActivity) context).getmLocation().getLatitude()));
        request.setLongitude(String.valueOf(((HomeActivity) context).getmLocation().getLongitude()));
        request.setUserName(strUsername);
        request.setUserId(userId);
        request.setTechId(userId);
        request.setLoggedIn(true);
        request.setPhoneMake(PhoneMake);
        request.setBatteryStatistics(batteryStatistics);
        request.setResourceImage(encodedImage);
        request.setSkipAttendance(isSkip);

        return request;
    }

    public static int getMyBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryIntent.getIntExtra("level", -1);
    }

    public static void statusCheck(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context);
        }
    }

    private static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        try {
                            Intent mIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(mIntent);
                            dialog.dismiss();
                            Activity activity = (Activity) context;
                            activity.finishAffinity();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Activity activity = (Activity) context;
                        activity.finishAffinity();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void sendErrorLogs(final Activity context, String error, String activityName, String methodName, String lineNo) {
        try {
            Date currentTime = Calendar.getInstance().getTime();
            String DT = String.valueOf(currentTime);
            ErrorLoggerModel request = new ErrorLoggerModel();
            ErrorLog data = new ErrorLog();
            data.setApplicationName("HicareRun Mobile");
            data.setApplicationType("Mobile");
            data.setLevel("Error");
            data.setLogMessage(error);
            data.setSource(lineNo);
            data.setType("Mobile");
            data.setUserId(0);
            data.setMethodName(methodName);
            request.setApplicationName("HicareRun Mobile");
            request.setApplicationType("Mobile");
            request.setSource("HiCareRun");
            request.setData(data.toString());
            NetworkCallController controller = new NetworkCallController();
            controller.setListner(new NetworkResponseListner() {
                @Override
                public void onResponse(int requestCode, Object data) {

                }

                @Override
                public void onFailure(int requestCode) {

                }
            });
            controller.sendErrorLog(ERROR_REQUEST, request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void checkCurrentVersion(final String apkurl, String version) {
//        PackageInfo pInfo = null;
//        try {
//            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        String mobileVersion = pInfo.versionName;
//
//        if (Float.parseFloat(mobileVersion) < Float.parseFloat(version)) {
//            String messageAlert = "<html><body><p>Please update your app to new version.<br><br>Current app version: " + mobileVersion + "<br><br>New version: " + version + "</p></body></html>";
//            AppUtils.showDownloadActionAlertBox(HomeActivity.this, String.valueOf(Html.fromHtml(messageAlert)), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if (AppUtils.checkConnection(HomeActivity.this)) {
//                        ProgressDialog progress = new ProgressDialog(HomeActivity.this);
//                        DownloadApk downloadAndInstall = new DownloadApk();
//                        progress.setCancelable(false);
//                        progress.setMessage("Downloading...");
//                        downloadAndInstall.setContext(getApplicationContext(), progress);
//                        downloadAndInstall.execute(apkurl);
//                    } else {
//                        AppUtils.showOkActionAlertBox(HomeActivity.this, "No Internet Found.", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                finish();
//                            }
//                        });
//                    }
//                }
//            });
//
//        }
//    }

    public static String getCurrentTimeStamp(){
        String s = "";
        try {
            DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            s = dateFormatter.format(today);

        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }


}
