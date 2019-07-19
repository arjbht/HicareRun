package com.ab.hicarerun.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivityVerifyOtpBinding;
import com.ab.hicarerun.fragments.FaceRecognizationFragment;
import com.ab.hicarerun.fragments.LoginFragment;
import com.ab.hicarerun.fragments.VerifyOtpFragment;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.OtpModel.SendOtpResponse;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.notifications.OneSIgnalHelper;
import com.ab.hicarerun.viewmodel.UserLoginViewModel;

import static com.ab.hicarerun.BaseApplication.getRealm;

public class VerifyOtpActivity extends BaseActivity {
    ActivityVerifyOtpBinding mActivityVerifyOtpBinding;
    public static final String ARGS_MOBILE = "ARGS_MOBILE";
    public static final String ARGS_USER = "ARGS_USER";
    public static final String ARGS_OTP = "ARGS_OTP";
    String mobile = "", otp = "", user = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityVerifyOtpBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_verify_otp);

        setSupportActionBar(mActivityVerifyOtpBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mobile = getIntent().getStringExtra(ARGS_MOBILE);
        otp = getIntent().getStringExtra(ARGS_OTP);
        user = getIntent().getStringExtra(ARGS_USER);
        addFragment(VerifyOtpFragment.newInstance(mobile, otp, user), "verifyotpactivity - verifyotpFragment");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
