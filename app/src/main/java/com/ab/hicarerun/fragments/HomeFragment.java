package com.ab.hicarerun.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.activities.TaskDetailsActivity;
import com.ab.hicarerun.adapter.TaskListAdapter;
import com.ab.hicarerun.databinding.FragmentHomeBinding;
import com.ab.hicarerun.handler.OnCallListItemClickHandler;
import com.ab.hicarerun.handler.OnListItemClickHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.AttendanceModel.AttendanceRequest;
import com.ab.hicarerun.network.models.ExotelModel.ExotelResponse;
import com.ab.hicarerun.network.models.HandShakeModel.ContinueHandShakeResponse;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.TaskModel.TaskListResponse;
import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.SharedPreferencesUtility;
import com.google.android.gms.tasks.Task;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements NetworkResponseListner<TaskListResponse>, OnCallListItemClickHandler {
    FragmentHomeBinding mFragmentHomeBinding;
    RecyclerView.LayoutManager layoutManager;
    TaskListAdapter mAdapter;
    final Handler timerHandler = new Handler();
    Runnable timerRunnable;
    private static final int TASKS_REQ = 1000;
    private static final int EXOTEL_REQ = 2000;
    private static final int CALL_REQUEST = 3000;
    private static final int CAM_REQUEST = 4000;
    private boolean isBack = false;
    private boolean isSkip = false;
    private Integer pageNumber = 1;
    private String UserId = "", IMEI = "", UserName = "";
    private String activityName = "";
    private String methodName = "";
    private NavigationView navigationView = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentHomeBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        navigationView = getActivity().findViewById(R.id.navigation_view);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        activityName = getActivity().getClass().getSimpleName();
        apply();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                timerHandler.postDelayed(this, 60000); //run every minute
            }
        };
        return mFragmentHomeBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        timerHandler.postDelayed(timerRunnable, 500);
        isBack = SharedPreferencesUtility.getPrefBoolean(getActivity(), SharedPreferencesUtility.PREF_REFRESH);
        if (isBack) {
            getAllTasks();
            AppUtils.getDataClean();
            SharedPreferencesUtility.savePrefBoolean(getActivity(), SharedPreferencesUtility.PREF_REFRESH, false);
        } else {
            AppUtils.getDataClean();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentHomeBinding.recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());

        mFragmentHomeBinding.swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getAllTasks();
                    }
                });

        mFragmentHomeBinding.recycleView.setLayoutManager(layoutManager);


        mFragmentHomeBinding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_dark, android.R.color.holo_blue_light,
                android.R.color.holo_red_dark, android.R.color.holo_red_light,
                android.R.color.holo_green_dark, android.R.color.holo_green_light,
                android.R.color.holo_red_dark, android.R.color.holo_red_light);

        // specify an adapter (see also next example)
        mAdapter = new TaskListAdapter(getActivity());
        mAdapter.setOnCallClickHandler(this);
        mFragmentHomeBinding.recycleView.setAdapter(mAdapter);

        getAllTasks();
        mFragmentHomeBinding.swipeRefreshLayout.setRefreshing(true);
        try {
            PhoneCallListener phoneListener = new PhoneCallListener();
            TelephonyManager telephonyManager = (TelephonyManager)
                    getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.toString();
            methodName = "PhoneCallListener";
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(getActivity(), error, activityName, methodName, lineNo);
        }

    }


    private void getAllTasks() {
        try {
            SharedPreferencesUtility.savePrefBoolean(getActivity(), SharedPreferencesUtility.PREF_REFRESH, false);
            if ((HomeActivity) getActivity() != null) {
                RealmResults<LoginResponse> LoginRealmModels =
                        BaseApplication.getRealm().where(LoginResponse.class).findAll();
                if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                    UserName = LoginRealmModels.get(0).getUserName();
                    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    IMEI = telephonyManager.getDeviceId();
                    UserId = LoginRealmModels.get(0).getUserID();
                    NetworkCallController controller = new NetworkCallController(this);
                    controller.setListner(this);
                    controller.getTasksList(TASKS_REQ, UserId, IMEI);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.toString();
            methodName = "getAllTasks";
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(getActivity(), error, activityName, methodName, lineNo);
        }

    }

    @Override
    public void onResponse(int requestCode, TaskListResponse data) {
        AppUtils.getDataClean();
        if (data.getErrorMessage().equals("Absent") && !isSkip) {
            mFragmentHomeBinding.swipeRefreshLayout.setRefreshing(false);
            getAttendanceDialog();

        } else {
            mFragmentHomeBinding.swipeRefreshLayout.setRefreshing(false);
            final List<Tasks> items = data.getData();
            if (items != null) {
                if (pageNumber == 1 && items.size() > 0) {
                    mAdapter.setData(items);
                    mAdapter.notifyDataSetChanged();
                    mFragmentHomeBinding.emptyTask.setVisibility(View.GONE);
                } else if (items.size() > 0) {
                    mAdapter.addData(items);
                    mAdapter.notifyDataSetChanged();
                    mFragmentHomeBinding.emptyTask.setVisibility(View.GONE);
                } else {
                    mFragmentHomeBinding.emptyTask.setVisibility(View.VISIBLE);
                }
            } else {
                mFragmentHomeBinding.emptyTask.setVisibility(View.VISIBLE);
            }


            mAdapter.setOnItemClickHandler(new OnListItemClickHandler() {
                @Override
                public void onItemClick(int positon) {
                    Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
                    intent.putExtra(TaskDetailsActivity.ARGS_TASKS, items.get(positon));
                    startActivity(intent);

                }
            });

        }


    }


    private void getAttendanceDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());

        View promptsView = li.inflate(R.layout.dialog_mark_attendance, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setTitle("Mark Attendance");

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        final AppCompatTextView txt_head =
                (AppCompatTextView) promptsView.findViewById(R.id.txt_head);
        final AppCompatButton btn_send =
                (AppCompatButton) promptsView.findViewById(R.id.btn_send);
        final AppCompatButton btn_cancel =
                (AppCompatButton) promptsView.findViewById(R.id.btn_cancel);

        txt_head.setText("Welcome " + UserName + ", please mark your attendance with the face recognization.");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                replaceFragment(FaceRecognizationFragment.newInstance(true, ""), "HomeFragment-FaceRecognizationFragment");
                alertDialog.dismiss();
            }


        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                isSkip = true;
                try {
                    if ((HomeActivity) getActivity() != null) {
                        RealmResults<LoginResponse> LoginRealmModels =
                                BaseApplication.getRealm().where(LoginResponse.class).findAll();
                        if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                            String BatteryStatistics = String.valueOf(AppUtils.getMyBatteryLevel(getActivity()));
                            AttendanceRequest request = AppUtils.getDeviceInfo(getActivity(), "", BatteryStatistics, true);
                            NetworkCallController controller = new NetworkCallController();
                            controller.setListner(new NetworkResponseListner() {
                                @Override
                                public void onResponse(int requestCode, Object data) {
                                    ContinueHandShakeResponse response = (ContinueHandShakeResponse) data;
                                    if (response.getSuccess()) {
                                        Toasty.success(getActivity(),"Attendance marked successfully.",Toast.LENGTH_SHORT).show();
                                        replaceFragment(HomeFragment.newInstance(), "FaceRecognizationFragment-HomeFragment");
                                    } else {
                                        Toast.makeText(getActivity(), "Attendance Failed, please try again.", Toast.LENGTH_SHORT).show();
                                        getAttendanceDialog();
                                        getAllTasks();
                                    }
                                }

                                @Override
                                public void onFailure(int requestCode) {

                                }
                            });
                            controller.getTechAttendance(CAM_REQUEST, request);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String error = e.toString();
                    methodName = "getAttendanceDialog";
                    String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
                    AppUtils.sendErrorLogs(getActivity(), error, activityName, methodName, lineNo);
                }


                getAllTasks();
            }
        });
        alertDialog.setIcon(R.mipmap.logo);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    @Override
    public void onFailure(int requestCode) {
        mFragmentHomeBinding.swipeRefreshLayout.setRefreshing(false);
        mFragmentHomeBinding.emptyTask.setVisibility(View.GONE);
    }

    @Override
    public void onPrimaryMobileClicked(int position) {
        if (AppUtils.checkConnection(getActivity()) == true) {
            String primaryNumber = mAdapter.getItem(position).getPrimaryMobile();
            String techNumber = "";

            if ((HomeActivity) getActivity() != null) {
                RealmResults<LoginResponse> LoginRealmModels =
                        BaseApplication.getRealm().where(LoginResponse.class).findAll();
                if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                    techNumber = LoginRealmModels.get(0).getPhoneNumber();
                }
            }

            if (techNumber == null || techNumber.length() == 0) {
                AppUtils.showOkActionAlertBox(getActivity(), "Technician number is unavaible, please contact to Administrator.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
            } else if (primaryNumber == null || primaryNumber.trim().length() == 0) {
                AppUtils.showOkActionAlertBox(getActivity(), "Customer mobile number is unavaible.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
            } else {
                getExotelCalled(primaryNumber, techNumber);
            }
        } else {
            AppUtils.showOkActionAlertBox(getActivity(), "No Internet Connection.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }


    @Override
    public void onAlternateMobileClicked(int position) {
        if (AppUtils.checkConnection(getActivity()) == true) {
            String secondaryNumber = mAdapter.getItem(position).getAltMobile();
            String techNumber = "";

            if ((HomeActivity) getActivity() != null) {
                RealmResults<LoginResponse> LoginRealmModels =
                        BaseApplication.getRealm().where(LoginResponse.class).findAll();
                if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                    techNumber = LoginRealmModels.get(0).getPhoneNumber();
                }
            }
            if (techNumber == null || techNumber.trim().length() == 0) {

                AppUtils.showOkActionAlertBox(getActivity(), "Technician number is unavaible, please contact to Administrator.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
            } else if (secondaryNumber == null || secondaryNumber.trim().length() == 0) {
                AppUtils.showOkActionAlertBox(getActivity(), "Customer alt. mobile number is unavaible.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
            } else {
                getExotelCalled(secondaryNumber, techNumber);
            }
        } else {

            AppUtils.showOkActionAlertBox(getActivity(), "No Internet Connection.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    @Override
    public void onTelePhoneClicked(int position) {

        if (AppUtils.checkConnection(getActivity()) == true) {
            String secondaryNumber = mAdapter.getItem(position).getAltMobile();
            String techNumber = "";

            if ((HomeActivity) getActivity() != null) {
                RealmResults<LoginResponse> LoginRealmModels =
                        BaseApplication.getRealm().where(LoginResponse.class).findAll();
                if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                    techNumber = LoginRealmModels.get(0).getPhoneNumber();
                }
            }
            if (techNumber == null || techNumber.trim().length() == 0) {

                AppUtils.showOkActionAlertBox(getActivity(), "Technician number is unavaible, please contact to Administrator.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
            } else if (secondaryNumber == null || secondaryNumber.trim().length() == 0) {
                AppUtils.showOkActionAlertBox(getActivity(), "Customer phone number is unavaible.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
            } else {
                getExotelCalled(secondaryNumber, techNumber);
            }
        } else {

            AppUtils.showOkActionAlertBox(getActivity(), "No Internet Connection.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    @Override
    public void onTrackLocationIconClicked(int position) {
        try {
            if ((HomeActivity) getActivity() != null) {
//            if (((HomeActivity) getActivity()).getmLocation() != null) {
                if (mAdapter.getItem(position).getCustomerLat() != null && !mAdapter.getItem(position).getCustomerLat().equals("")) {
                    double latitude = Double.parseDouble(mAdapter.getItem(position).getCustomerLat());
                    double longitude = Double.parseDouble(mAdapter.getItem(position).getCustomerLong());
                    String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + ((HomeActivity) getActivity()).getmLocation().getLatitude() + "," + ((HomeActivity) getActivity()).getmLocation().getLongitude() + "&daddr=" + latitude + "," + longitude;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(Intent.createChooser(intent, "Hicare Run"));
                }
//            }else {
//                Toast.makeText(getActivity(), "location not found", Toast.LENGTH_SHORT).show();
//            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.toString();
            methodName = "onTrackLocationIconClicked";
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(getActivity(), error, activityName, methodName, lineNo);
        }

    }

    @Override
    public void onItemClick(int positon) {

    }

    private void getExotelCalled(String customerNumber, String techNumber) {
        NetworkCallController controller = new NetworkCallController();
        controller.setListner(new NetworkResponseListner() {
            @Override
            public void onResponse(int requestCode, Object data) {
                ExotelResponse exotelResponse = (ExotelResponse) data;

                if (exotelResponse.getSuccess() == true) {
                    try {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel:", exotelResponse.getResponseMessage() + "," + exotelResponse.getData(), null));
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
                                return;
                            }

                            getActivity().startActivity(intent);
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + exotelResponse.getResponseMessage() + "," + exotelResponse.getData()));
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling

                                ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                                return;
                            }
                            getActivity().startActivity(callIntent);

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        String error = ex.toString();
                        methodName = "getExotelCalled()";

                        System.out.println("The line number is " + new Exception().getStackTrace()[0].getLineNumber());
                        String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
                        AppUtils.sendErrorLogs(getActivity(), error, activityName, methodName, lineNo);
                    }

                } else {
                    Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int requestCode) {

            }
        });
        controller.getExotelCalled(EXOTEL_REQ, customerNumber, techNumber);
    }

    class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "RENEWAL_CALL";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            try {
                if (TelephonyManager.CALL_STATE_RINGING == state) {
                    // phone ringing
                    Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
                }
                if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                    Log.i(LOG_TAG, "OFFHOOK");
                    isPhoneCalling = true;
                }
                if (TelephonyManager.CALL_STATE_IDLE == state) {
                    Log.i(LOG_TAG, "IDLE");
                    if (isPhoneCalling) {
                        Log.i(LOG_TAG, "restart app");
                        Intent i = getActivity().getPackageManager()
                                .getLaunchIntentForPackage(
                                        getActivity().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(i);
                        isPhoneCalling = false;
                    }


                }
            } catch (Exception e) {
                String error = e.toString();
                methodName = "PhoneCallListener";
                String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
                AppUtils.sendErrorLogs(getActivity(), error, activityName, methodName, lineNo);
            }


        }
    }

    @Override
    public void onPause() {
        timerHandler.removeCallbacks(timerRunnable);
        super.onPause();
    }


    private void apply() {
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().getItem(1).setChecked(false);
        navigationView.getMenu().getItem(2).setChecked(false);
        navigationView.getMenu().getItem(3).setChecked(false);
        navigationView.getMenu().getItem(4).setChecked(false);

    }
}
