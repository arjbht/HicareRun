package com.ab.hicarerun.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivityHelpBinding;
import com.ab.hicarerun.handler.UserHelpClickHandler;
import com.ab.hicarerun.utils.AppUtils;

public class HelpActivity extends AppCompatActivity implements UserHelpClickHandler {
    ActivityHelpBinding mActivityHelpBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHelpBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_help);
        mActivityHelpBinding.setHandler(this);
        setSupportActionBar(mActivityHelpBinding.toolbar);
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onContactNoClicked(View view) {
        try {
            String number = mActivityHelpBinding.txtNumber.getText().toString();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        }catch (Exception e){
            e.printStackTrace();
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(this, e.toString(), getClass().getSimpleName(), "onContactNoClicked", lineNo);
        }

    }

    @Override
    public void onEmailClicked(View view) {
        try {
            String mail = mActivityHelpBinding.txtMail.getText().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + mail));
//            intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
//            intent.putExtra(Intent.EXTRA_TEXT, "your_text");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(this, e.toString(), getClass().getSimpleName(), "onEmailClicked", lineNo);
        }
    }
}
