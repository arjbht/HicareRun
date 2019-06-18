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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.ab.hicarerun.network.models.ExotelModel.ExotelResponse;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.TaskModel.TaskListResponse;
import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.ab.hicarerun.utils.AppUtils;

import java.util.List;

import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements NetworkResponseListner<TaskListResponse>, OnCallListItemClickHandler {
    FragmentHomeBinding mFragmentHomeBinding;
    RecyclerView.LayoutManager layoutManager;
    TaskListAdapter mAdapter;
    private static final int TASKS_REQ = 1000;
    private static final int EXOTEL_REQ = 2000;
    private static final int CALL_REQUEST = 3000;
    private static final int CAM_REQUEST = 4000;
    private boolean isSkip = false;
    private Integer pageNumber = 1;
    private String UserId = "", IMEI = "", UserName = "";


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

        return mFragmentHomeBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllTasks();
        AppUtils.getDataClean();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mFragmentHomeBinding.swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getAllTasks();
                    }
                });
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        mFragmentHomeBinding.recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
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

        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager)
                getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION
                    && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                return true;
            }
        }
        return false;
    }

    private void getAllTasks() {
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
    }

    @Override
    public void onResponse(int requestCode, TaskListResponse data) {
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
                mFragmentHomeBinding.emptyTask.setVisibility(View.GONE);
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

        txt_head.setText("Welcome " + UserName + ", please mark your attendance with the biometric authentication.");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                replaceFragment(FaceRecognizationFragment.newInstance(false,""), "HomeFragment-FaceRecognizationFragment");
            }


        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                isSkip = true;
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
            String techNumber = mAdapter.getItem(position).getTechMobile();
            if (techNumber == null) {
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
            String techNumber = mAdapter.getItem(position).getTechMobile();
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
            String techNumber = mAdapter.getItem(position).getTechMobile();
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
        if ((HomeActivity) getActivity() != null) {
            if (((HomeActivity) getActivity()).getmLocation() != null) {
                if (mAdapter.getItem(position).getAccountLat() != null && !mAdapter.getItem(position).getAccountLat().equals("")) {
                    double latitude = Double.parseDouble(mAdapter.getItem(position).getCustomerLat());
                    double longitude = Double.parseDouble(mAdapter.getItem(position).getCustomerLong());
                    String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + ((HomeActivity) getActivity()).getmLocation().getLatitude() + "," + ((HomeActivity) getActivity()).getmLocation().getLongitude() + "&daddr=" + latitude + "," + longitude;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(Intent.createChooser(intent, "Hicare Run"));
                }
            }
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
        }
    }
}
