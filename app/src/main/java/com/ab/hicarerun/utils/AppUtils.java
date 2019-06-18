package com.ab.hicarerun.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.GeneralModel.GeneralData;
import com.ab.hicarerun.network.models.GeneralModel.GeneralPaymentMode;
import com.ab.hicarerun.network.models.GeneralModel.GeneralTaskStatus;
import com.ab.hicarerun.network.models.HandShakeModel.HandShake;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;


public class AppUtils {


    private static final int HANDSHAKE_REQUEST = 2000;

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

    public static void getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
        String formattedDate = dateFormat.format(date);
        System.out.println("Current time of the day using Calendar - 24 hour format: " + formattedDate);
    }

    public static String CompareTime(String strTimeToCompare)

    {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        int dtHour;

        int dtMin;

        int iAMPM;

        String strAMorPM = null;

        Date dtCurrentDate;


        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());


        try {
            Date TimeToCompare = sdf.parse(strTimeToCompare);

            dtMin = cal.get(Calendar.MINUTE);

            dtHour = cal.get(Calendar.HOUR);

            iAMPM = cal.get(Calendar.AM_PM);

            if (iAMPM == 1)

            {
                strAMorPM = "PM";
            }

            if (iAMPM == 0) {
                strAMorPM = "AM";
            }

            dtCurrentDate = sdf.parse(dtHour + ":" + dtMin + " " + strAMorPM);
            if (dtCurrentDate.after(TimeToCompare))

            {
                return "1";
            }
            if (dtCurrentDate.before(TimeToCompare))

            {
                return "-1";
            }
            dtCurrentDate = sdf.parse(dtHour + ":" + (dtMin + 15) + " " + strAMorPM);
            if (dtCurrentDate.equals(TimeToCompare))

            {
                return "0";
            }
        } catch (ParseException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();
        }
        return "4";

    }

    public static String compareDates(String d1, String d2) {


        String date_result = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

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
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().where(GeneralData.class).findAll().deleteAllFromRealm();
        Realm.getDefaultInstance().where(GeneralTaskStatus.class).findAll().deleteAllFromRealm();
        Realm.getDefaultInstance().where(GeneralPaymentMode.class).findAll().deleteAllFromRealm();
        Realm.getDefaultInstance().where(com.ab.hicarerun.network.models.GeneralModel.IncompleteReason.class).findAll().deleteAllFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }

    public static String reFormatDateTime(String dateIn, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = simpleDateFormat.parse(dateIn);
        simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static void getHandShakeCall(final String username, final Activity context) {
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

    }


}
