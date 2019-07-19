package com.ab.hicarerun;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by arjun on 03/05/19.
 */

public abstract class BaseActivity extends AppCompatActivity {
  private ProgressDialog mProgressDialog;
  @Override protected void attachBaseContext(Context context) {
    try {
      super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void addFragment(BaseFragment fragment, String tag) {
    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, tag).commit();
  }

  public void showProgressDialog() {
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      return;
    }
    Log.i("ProgressDialog", "showProgressDialog");
    try {
      mProgressDialog = new ProgressDialog(this, R.style.TransparentProgressDialog);
      mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
      mProgressDialog.setCancelable(false);
      mProgressDialog.setIndeterminate(true);
      mProgressDialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void dismissProgressDialog() {
    if (mProgressDialog != null) {
      if (!mProgressDialog.isShowing()) {
        return;
      }
      Log.i("ProgressDialog", "dismissProgressDialog");
      try {
        mProgressDialog.dismiss();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
