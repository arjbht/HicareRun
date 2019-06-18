package com.ab.hicarerun.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivityHomeBinding;
import com.ab.hicarerun.fragments.FaceRecognizationFragment;
import com.ab.hicarerun.fragments.HomeFragment;
import com.ab.hicarerun.fragments.NotificationFragment;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.HandShakeModel.HandShake;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.LogoutResponse;
import com.ab.hicarerun.service.LocationManager;
import com.ab.hicarerun.service.ServiceLocationSend;
import com.ab.hicarerun.service.listner.LocationManagerListner;
import com.ab.hicarerun.utils.SharedPreferencesUtility;

import java.util.Calendar;
import java.util.List;

import io.realm.RealmResults;

public class HomeActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener, LocationManagerListner {
    ActivityHomeBinding mActivityHomeBinding;

    private static final int LOGOUT_REQ = 1000;
    private Location mLocation;
    private LocationManagerListner mListner;
    public static final String ARG_HANDSHAKE = "ARG_HANDSHAKE";
    public static final String ARG_EVENT = "ARG_EVENT";
    public static final String ARG_USER = "ARG_USER";
    RealmResults<LoginResponse> LoginRealmModels = null;
    List<HandShake> items = null;
    boolean isClicked = false;
    String userName = "";
    String userId = "";
    String profilePic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHomeBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_home);
        setSupportActionBar(mActivityHomeBinding.toolbar);

        try {
            isClicked = getIntent().getExtras().getBoolean(ARG_EVENT, false);

            LoginRealmModels =
                    BaseApplication.getRealm().where(LoginResponse.class).findAll();
            if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                LocationManager.Builder builder = new LocationManager.Builder(this);
                builder.setLocationListner(this);
                builder.build();
                initNavigationDrawer();
                userId = LoginRealmModels.get(0).getUserID();
                SharedPreferencesUtility.savePrefString(HomeActivity.this, SharedPreferencesUtility.PREF_USERID, userId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        getServiceCalled();
        addFragment(HomeFragment.newInstance(), "HomeActivity - HomeFragment");

    }

    private void getServiceCalled() {

        if (isClicked) {
            userName = getIntent().getStringExtra(ARG_USER);
            SharedPreferencesUtility.savePrefString(HomeActivity.this, SharedPreferencesUtility.PREF_USERNAME, userName);
            items = (List<HandShake>) getIntent().getSerializableExtra(ARG_HANDSHAKE);
            String time = items.get(1).getValue();
            SharedPreferencesUtility.savePrefString(HomeActivity.this, SharedPreferencesUtility.PREF_TIME, time);
            if (items.get(0).getText().equals("EnableTrace")) {
                if (items.get(0).getValue().equals("true")) {
                    Intent itAlarm = new Intent(this, ServiceLocationSend.class);
                    PendingIntent pendingIntent = PendingIntent.getService(this, 0, itAlarm, 0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 3);
                    AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarme.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            1000 * 60 * Integer.parseInt(items.get(1).getValue()),
                            pendingIntent);
//                    alarme.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 1000 * 60 * Integer.parseInt(items.get(1).getValue()), pendingIntent);
                }
            }
        } else {
            items = (List<HandShake>) getIntent().getSerializableExtra(ARG_HANDSHAKE);
            String time = SharedPreferencesUtility.getPrefString(HomeActivity.this, SharedPreferencesUtility.PREF_TIME);
            Intent itAlarm = new Intent(this, ServiceLocationSend.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, itAlarm, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);
            AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    1000 * 60 * Integer.parseInt(time),
                    pendingIntent);
//            alarme.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 1000 * 60 * Integer.parseInt(items.get(1).getValue()), pendingIntent);
        }
    }


    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        LoginRealmModels =
                BaseApplication.getRealm().where(LoginResponse.class).findAll();
        if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
            String Uname = LoginRealmModels.get(0).getUserName();

            View header = navigationView.getHeaderView(0);
            TextView name = (TextView) header.findViewById(R.id.drawer_name);
            name.setText("Hi, " + Uname);


        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {

                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(mActivityHomeBinding.container.getId(), HomeFragment.newInstance()).addToBackStack(null).commit();
                        mActivityHomeBinding.drawer.closeDrawers();
                        break;


                    case R.id.nav_notifications:
                        getSupportFragmentManager().beginTransaction().replace(mActivityHomeBinding.container.getId(), NotificationFragment.newInstance()).addToBackStack(null).commit();

                        mActivityHomeBinding.drawer.closeDrawers();
                        break;

                    case R.id.nav_settings:
                        mActivityHomeBinding.drawer.closeDrawers();
                        break;

                    case R.id.nav_logout:
                        mActivityHomeBinding.drawer.closeDrawers();

                        final AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
//                        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
                        dialog.setTitle("Logout");
                        dialog.setMessage("Do you want to logout?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NetworkCallController controller = new NetworkCallController();
                                String UserId = SharedPreferencesUtility.getPrefString(HomeActivity.this, SharedPreferencesUtility.PREF_USERID);

                                controller.setListner(new NetworkResponseListner() {
                                    @Override
                                    public void onResponse(int requestCode, Object response) {
                                        LogoutResponse logres = (LogoutResponse) response;

                                        if (logres.getSuccess() == true) {
                                            SharedPreferencesUtility.savePrefBoolean(getApplicationContext(), SharedPreferencesUtility.IS_USER_LOGIN,
                                                    false);
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });

                                controller.getLogout(LOGOUT_REQ, UserId);

                            }
                        });
                        dialog.setNegativeButton("No", null);
                        dialog.show();

                        break;

                }
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mActivityHomeBinding.drawer, mActivityHomeBinding.toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        mActivityHomeBinding.drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            showExitAlert();
        } else {
            super.onBackPressed();
        }
    }

    private void showExitAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit");
        dialog.setMessage("Do you want to exit?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton("No", null);
        dialog.show();
    }

    @Override
    public void onBackStackChanged() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider) {
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
