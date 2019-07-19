package com.ab.hicarerun.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivityTechnicianSeniorBinding;
import com.ab.hicarerun.fragments.AttachmentFragment;
import com.ab.hicarerun.fragments.TechnicianSeniorFragment;
import com.ab.hicarerun.utils.SharedPreferencesUtility;

public class TechnicianSeniorActivity extends BaseActivity {
    ActivityTechnicianSeniorBinding mTechnicianSeniorBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTechnicianSeniorBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_technician_senior);
        addFragment(TechnicianSeniorFragment.newInstance(), "TechnicianSeniorActivity - TechnicianSeniorFragment");

        setSupportActionBar(mTechnicianSeniorBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        try {
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right);  //close animation
            getGroomingBack();
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getGroomingBack() {
        int fragment = getSupportFragmentManager().getBackStackEntryCount();
        Log.e("fragments", String.valueOf(fragment));
        if (fragment < 1) {
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
                getGroomingBack();
                break;
        }

        return true;
    }
}
