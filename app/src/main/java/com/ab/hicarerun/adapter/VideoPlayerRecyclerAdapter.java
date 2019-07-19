package com.ab.hicarerun.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.TrainingActivity;
import com.ab.hicarerun.network.models.TrainingModel.Videos;
import com.bumptech.glide.RequestManager;
import java.util.ArrayList;
import java.util.List;


public class VideoPlayerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Videos> videosList;
    private RequestManager requestManager;
    private Activity mActivity;

    public VideoPlayerRecyclerAdapter(List<Videos> mediaObjects, RequestManager requestManager, TrainingActivity trainingActivity) {
        this.videosList = mediaObjects;
        this.requestManager = requestManager;
        this.mActivity = trainingActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_video_list_item, viewGroup, false),mActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((VideoPlayerViewHolder)viewHolder).onBind(videosList.get(i), requestManager);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

}














