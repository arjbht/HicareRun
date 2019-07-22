package com.ab.hicarerun.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.TrainingActivity;
import com.ab.hicarerun.adapter.TechnicianGroomingAdapter;
import com.ab.hicarerun.adapter.VideoPlayerAdapter;
import com.ab.hicarerun.databinding.FragmentTrainingBinding;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.TrainingModel.Videos;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.VerticalSpacingItemDecorator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends BaseFragment {
FragmentTrainingBinding mFragmentTrainingBinding;
    private static final int VIDEO_REQUEST = 1000;
    private VideoPlayerAdapter mAdapter = null;
    private Integer pageNumber = 1;
    RecyclerView.LayoutManager layoutManager;



    public TrainingFragment() {
        // Required empty public constructor
    }

    public static TrainingFragment newInstance() {
        Bundle args = new Bundle();
        TrainingFragment fragment = new TrainingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentTrainingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);
        getActivity().setTitle("Training Videos");
        return mFragmentTrainingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentTrainingBinding.swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getTrainingVideos();
                    }
                });
        mFragmentTrainingBinding.recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mFragmentTrainingBinding.recycleView.setLayoutManager(layoutManager);
        mFragmentTrainingBinding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_dark, android.R.color.holo_blue_light,
                android.R.color.holo_red_dark, android.R.color.holo_red_light,
                android.R.color.holo_green_dark, android.R.color.holo_green_light,
                android.R.color.holo_red_dark, android.R.color.holo_red_light);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(2);
        mFragmentTrainingBinding.recycleView.addItemDecoration(itemDecorator);
        mAdapter = new VideoPlayerAdapter(initGlide(), getActivity());
        mFragmentTrainingBinding.recycleView.setAdapter(mAdapter);
        mFragmentTrainingBinding.recycleView.setAdapter(mAdapter);
        getTrainingVideos();
        mFragmentTrainingBinding.swipeRefreshLayout.setRefreshing(true);
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
            NetworkCallController controller = new NetworkCallController(this);
            controller.setListner(new NetworkResponseListner() {
                @Override
                public void onResponse(int requestCode, Object response) {
                    List<Videos> items = (List<Videos>) response;
                    mFragmentTrainingBinding.swipeRefreshLayout.setRefreshing(false);

                    if (items != null) {
                        if (pageNumber == 1 && items.size() > 0) {
                            mAdapter.setData(items);
                            mAdapter.notifyDataSetChanged();
                        } else if (items.size() > 0) {
                            mAdapter.addData(items);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            pageNumber--;
                        }
                    }
                }

                @Override
                public void onFailure(int requestCode) {
                }
            });
            controller.getTrainingVideos(VIDEO_REQUEST);
        }catch (Exception e){
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(getActivity(), e.toString(), getClass().getSimpleName(), "getTrainingVideos", lineNo);
        }

    }

}
