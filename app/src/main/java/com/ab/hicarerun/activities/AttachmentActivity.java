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
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right);  //close animation
        super.onBackPressed();
        int fragment = getSupportFragmentManager().getBackStackEntryCount();
        Log.e("fragments", String.valueOf(fragment));
        if (fragment < 1) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("listSize", mAttachmentList.size());
            bundle.putBoolean("isJobCard", isJobCardBoolean);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
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
                finish();
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
