package com.ab.hicarerun.activities;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.R;
import com.ab.hicarerun.adapter.ReferralListAdapter;
import com.ab.hicarerun.adapter.VideoPlayerAdapter;
import com.ab.hicarerun.adapter.VideoPlayerRecyclerAdapter;
import com.ab.hicarerun.databinding.ActivityTrainingBinding;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.HandShakeModel.HandShake;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TrainingModel.Videos;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.MyDividerItemDecoration;
import com.ab.hicarerun.utils.VerticalSpacingItemDecorator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class TrainingActivity extends BaseActivity {
    ActivityTrainingBinding mActivityTrainingBinding;
    private static final int VIDEO_REQUEST = 1000;
    private VideoPlayerAdapter mAdapter = null;
    private Integer pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTrainingBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_training);
        setSupportActionBar(mActivityTrainingBinding.toolbar);
        getSupportActionBar().setTitle("Training Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initRecyclerView();
        showProgressDialog();
        getTrainingVideos();
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mActivityTrainingBinding.recyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(2);
        mActivityTrainingBinding.recyclerView.addItemDecoration(itemDecorator);
        mAdapter = new VideoPlayerAdapter(initGlide(), TrainingActivity.this);
        mActivityTrainingBinding.recyclerView.setAdapter(mAdapter);

    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }


    private void getTrainingVideos() {
        try {
            NetworkCallController controller = new NetworkCallController();
            controller.setListner(new NetworkResponseListner() {
                @Override
                public void onResponse(int requestCode, Object response) {
                    List<Videos> items = (List<Videos>) response;
                    if (items != null) {
                        if (pageNumber == 1 && items.size() > 0) {
                            mAdapter.setData(items);
                            mAdapter.notifyDataSetChanged();
                            dismissProgressDialog();
                        } else if (items.size() > 0) {
                            mAdapter.addData(items);
                            mAdapter.notifyDataSetChanged();
                            dismissProgressDialog();
                        } else {
                            pageNumber--;
                        }
                    }
                }

                @Override
                public void onFailure(int requestCode) {
                    dismissProgressDialog();
                }
            });
            controller.getTrainingVideos(VIDEO_REQUEST);
        }catch (Exception e){
            dismissProgressDialog();
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(this, e.toString(), getClass().getSimpleName(), "getTrainingVideos", lineNo);
        }

    }




}
