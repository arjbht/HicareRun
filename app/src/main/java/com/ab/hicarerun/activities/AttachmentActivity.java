package com.ab.hicarerun.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivityAttachmentBinding;
import com.ab.hicarerun.fragments.AttachmentFragment;
import com.ab.hicarerun.handler.OnJobCardEventHandler;
import com.ab.hicarerun.handler.OnSaveEventHandler;
import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentList;
import com.ab.hicarerun.network.models.TaskModel.TaskChemicalList;
import com.ab.hicarerun.utils.SharedPreferencesUtility;

import java.util.HashMap;
import java.util.List;


public class AttachmentActivity extends BaseActivity implements OnJobCardEventHandler {
    public static final String ARGS_TASKS = "ARGS_TASKS";
    String taskId = "";
    private boolean isJobCardBoolean = false;
    List<GetAttachmentList> mAttachmentList = null;

    ActivityAttachmentBinding mActivityAttachmentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAttachmentBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_attachment);
        taskId = getIntent().getStringExtra(ARGS_TASKS);
        addFragment(AttachmentFragment.newInstance(taskId), "AttachmentActivity - AttachmentFragment");

        setSupportActionBar(mActivityAttachmentBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public void onBackPressed() {
        try {
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right);  //close animation
            super.onBackPressed();
            getAttchmentBack();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getAttchmentBack() {
        int fragment = getSupportFragmentManager().getBackStackEntryCount();
        Log.e("fragments", String.valueOf(fragment));
        if (fragment < 1) {
            if (mAttachmentList != null && mAttachmentList.size() > 0) {
                SharedPreferencesUtility.savePrefBoolean(this, SharedPreferencesUtility.PREF_ATTACHMENT, true);
            } else {
                SharedPreferencesUtility.savePrefBoolean(this, SharedPreferencesUtility.PREF_ATTACHMENT, false);
            }
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                getAttchmentBack();
                break;
        }

        return true;
    }


    @Override
    public void isJobCardEnable(Boolean b) {
        isJobCardBoolean = b;
    }

    @Override
    public void AttachmentList(List<GetAttachmentList> mList) {
        mAttachmentList = mList;
    }
}
