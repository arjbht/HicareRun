package com.ab.hicarerun.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivityLoginBinding;
import com.ab.hicarerun.fragments.LoginFragment;
import com.ab.hicarerun.handler.UserLoginClickHandler;
import com.ab.hicarerun.service.LocationManager;
import com.ab.hicarerun.service.listner.LocationManagerListner;
import com.ab.hicarerun.utils.SharedPreferencesUtility;
import com.ab.hicarerun.viewmodel.UserLoginViewModel;

public class LoginActivity extends BaseActivity  {
    ActivityLoginBinding mActivityLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityLoginBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_login);


        if (SharedPreferencesUtility.getPrefBoolean(this, SharedPreferencesUtility.IS_USER_LOGIN)) {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            finish();
        } else {
            addFragment(LoginFragment.newInstance(), "LoginTrealActivity-CreateRealFragment");
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

}
