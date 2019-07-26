package com.ab.hicarerun.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Window;
import android.view.WindowManager;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ActivityGrommingGalleryBinding;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

public class GrommingGalleryActivity extends BaseActivity {
    ActivityGrommingGalleryBinding mActivityGrommingGalleryBinding;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityGrommingGalleryBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_gromming_gallery);
        String image = getIntent().getStringExtra("Image");
        String title = getIntent().getStringExtra("Title");

        Glide.with(this).load(image).into(mActivityGrommingGalleryBinding.imgTech);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        setSupportActionBar(mActivityGrommingGalleryBinding.toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            mActivityGrommingGalleryBinding.imgTech.setScaleX(mScaleFactor);
            mActivityGrommingGalleryBinding.imgTech.setScaleY(mScaleFactor);
            return true;
        }
    }


}
