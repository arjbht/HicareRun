package com.ab.hicarerun;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by arjun on 03/05/19.
 */

public abstract class BaseActivity extends AppCompatActivity {

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
}
