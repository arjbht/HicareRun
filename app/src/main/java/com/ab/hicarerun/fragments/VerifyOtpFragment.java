package com.ab.hicarerun.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.VerifyOtpActivity;
import com.ab.hicarerun.databinding.FragmentOtpLoginBinding;
import com.ab.hicarerun.databinding.FragmentVerifyOtpBinding;
import com.ab.hicarerun.handler.Common;
import com.ab.hicarerun.handler.UserVerifyOtpClickHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.OtpModel.SendOtpResponse;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.SMSListener;
import com.ab.hicarerun.utils.notifications.OneSIgnalHelper;
import com.ab.hicarerun.viewmodel.UserLoginViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyOtpFragment extends BaseFragment implements UserVerifyOtpClickHandler {
    FragmentVerifyOtpBinding mFragmentVerifyOtpBinding;
    private static final String ARG_MOBILE = "ARG_MOBILE";
    private static final String ARG_OTP = "ARG_OTP";
    private static final String ARG_USER = "ARG_USER";
    private static final int LOGIN_REQUEST = 2000;
    private String mobile = "", otp = "", user = "";
    private static final int OTP_REQUEST = 1000;
    private Boolean isGetInside = false;
    private LoginFragment.UserLoginTask mAuthTask = null;
    private String profilePic = "";
    private SMSListener reciever;

    public VerifyOtpFragment() {
        // Required empty public constructor
    }

    public static VerifyOtpFragment newInstance(String mobile, String otp, String user) {
        Bundle args = new Bundle();
        args.putString(ARG_MOBILE, mobile);
        args.putString(ARG_OTP, otp);
        args.putString(ARG_USER, user);
        VerifyOtpFragment fragment = new VerifyOtpFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mobile = getArguments().getString(ARG_MOBILE);
            user = getArguments().getString(ARG_USER);
            otp = getArguments().getString(ARG_OTP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFragmentVerifyOtpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_otp, container, false);
        mFragmentVerifyOtpBinding.setHandler(this);

        // Add this listener after your views have been inflated

        reciever = new SMSListener();

        getActivity().setTitle("");
        return mFragmentVerifyOtpBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String text = "<font color=#000000>Didn't get the OTP?</font> <font color=#2bb77a> RESEND</font>";
        mFragmentVerifyOtpBinding.txtResend.setText(Html.fromHtml(text));
        mFragmentVerifyOtpBinding.txtNumber.setText("OTP sent to " + mobile);


        SMSListener.bindListener(new Common.OTPListener() {
            @Override
            public void onOTPReceived(String otp) {
                Log.d("Text", otp);
                mFragmentVerifyOtpBinding.otpView.setText(otp);
                if (mFragmentVerifyOtpBinding.otpView.length() == 6) {
                    attemptLogin();
                }

            }

        });


        mFragmentVerifyOtpBinding.otpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    mFragmentVerifyOtpBinding.btnVerify.setEnabled(true);
                    mFragmentVerifyOtpBinding.btnVerify.setAlpha(1);
                    mFragmentVerifyOtpBinding.btnVerify.setText("VERIFY AND PROCEED");
                } else {
                    mFragmentVerifyOtpBinding.btnVerify.setEnabled(false);
                    mFragmentVerifyOtpBinding.btnVerify.setAlpha(0.5f);
                    mFragmentVerifyOtpBinding.btnVerify.setText("ENTER OTP");
                }

            }
        });

        mFragmentVerifyOtpBinding.txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mFragmentVerifyOtpBinding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onResendOtpClicked(View view) {
        try {
            NetworkCallController controller = new NetworkCallController(this);
            controller.setListner(new NetworkResponseListner<SendOtpResponse>() {
                @Override
                public void onResponse(int requestCode, SendOtpResponse response) {
                    if (response.getSuccess()) {
                        otp = response.getData().getLoginotp();
                        Log.i("RESEND", "true");
                    } else {
                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(int requestCode) {
                }

            });

            controller.sendOtp(OTP_REQUEST, mobile, "true");


        } catch (Exception e) {
            Log.i("LoginError", e.getMessage());
        }
    }

    @Override
    public void onVerifyOtpClicked(View view) {
        if (mFragmentVerifyOtpBinding.otpView.length() == 6) {
            attemptLogin();
        }

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }


        String imei = "", device_info = "";

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        device_info = Build.MODEL;
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
        imei = telephonyManager.getDeviceId();
        PackageInfo pinfo = null;
        try {
            pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = pinfo.versionName;
        OneSIgnalHelper oneSIgnalHelper = new OneSIgnalHelper(getActivity());
        String mStrPlayerId = oneSIgnalHelper.getmStrUserID();

        try {
            if(mFragmentVerifyOtpBinding.otpView.getText().toString().equals(otp)) {

                String OTP = mFragmentVerifyOtpBinding.otpView.getText().toString();
                NetworkCallController controller = new NetworkCallController(this);
                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                    @Override
                    public void onResponse(int requestCode, LoginResponse response) {
                        // delete all previous record
                        getRealm().beginTransaction();
                        getRealm().deleteAll();
                        getRealm().commitTransaction();
                        profilePic = response.getUserProfilePic();
                        // add new record
                        getRealm().beginTransaction();
                        getRealm().copyToRealmOrUpdate(response);
                        getRealm().commitTransaction();
                        if (isVisible()) {
                            isGetInside = true;
                            new VerifyOtpFragment.UserLoginTask(isGetInside).execute((Void) null);
                        }
                    }

                    @Override
                    public void onFailure(int requestCode) {
                    }

                });


                controller.login(LOGIN_REQUEST, mobile, OTP, versionName, imei, device_info, mStrPlayerId);
            }else {
                Toast.makeText(getActivity(), "Invalid OTP!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.i("LoginError", e.getMessage());
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private Boolean isPass = false;
//        private UserLoginViewModel model = mFragmentLoginBinding.getModel();

        UserLoginTask(Boolean isGetInside) {
            isPass = isGetInside;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (isPass) {
                    return true;
                }
            } catch (Exception e) {
                Log.i("Background failed", e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                if (profilePic.trim().length() == 0) {
                    replaceFragment(FaceRecognizationFragment.newInstance(false, mobile), "VerifyOtpFragment-FaceRecognizationFragment");
                } else {
                    AppUtils.getHandShakeCall(mobile, getActivity());
                }
            } else {
                mFragmentVerifyOtpBinding.otpView.setError("Invalid OTP!");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    @Override
    public void onDestroy() {
        SMSListener.unbindListener();
        super.onDestroy();
    }
}
