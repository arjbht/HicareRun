package com.ab.hicarerun.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.ExoplayerActivity;
import com.ab.hicarerun.activities.TrainingActivity;
import com.ab.hicarerun.databinding.LayoutVideoListItemBinding;
import com.ab.hicarerun.databinding.ReferralListAdapterBinding;
import com.ab.hicarerun.handler.OnDeleteListItemClickHandler;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TrainingModel.Videos;
import com.ab.hicarerun.viewmodel.ReferralListViewModel;
import com.ab.hicarerun.viewmodel.VideoPlayerViewModel;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Arjun Bhatt on 7/16/2019.
 */
public class VideoPlayerAdapter extends RecyclerView.Adapter<VideoPlayerAdapter.ViewHolder> {


    private final Context mContext;
    private List<VideoPlayerViewModel> items = null;
    private RequestManager requestManager;


    public VideoPlayerAdapter(RequestManager requestManager, Context context) {
        if (items == null) {
            items = new ArrayList<>();
        }
        this.requestManager = requestManager;
        this.mContext = context;
    }




    @Override
    public VideoPlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutVideoListItemBinding mLayoutVideoListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.layout_video_list_item, parent, false);
        return new VideoPlayerAdapter.ViewHolder(mLayoutVideoListItemBinding);
    }


    @Override
    public void onBindViewHolder(VideoPlayerAdapter.ViewHolder holder, final int position) {

        holder.mLayoutVideoListItemBinding.title.setText(items.get(position).getVideoTitle());
        holder.mLayoutVideoListItemBinding.description.setText(items.get(position).getVideoDescription());
        this.requestManager
                .load(items.get(position).getVideoThumbnail())
                .into(holder.mLayoutVideoListItemBinding.thumbnail);

        holder.mLayoutVideoListItemBinding.exoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.VIDEO_URI, items.get(position).getVideoUrl());
                mContext.startActivity(intent);
            }
        });

        try {
            holder.mLayoutVideoListItemBinding.duration.setText(getDuration(items.get(position).getVideoUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getDuration(String videoUrl) {
        MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(videoUrl));
        int duration = mp.getDuration();
        mp.release();
        /*convert millis to appropriate time*/
        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }


    @Override
    public int getItemCount() {
        return items.size();

    }

    public void setData(List<Videos> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            VideoPlayerViewModel videoPlayerViewModel = new VideoPlayerViewModel();
            videoPlayerViewModel.clone(data.get(index));
            items.add(videoPlayerViewModel);
        }
    }

    public void addData(List<Videos> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            VideoPlayerViewModel videoPlayerViewModel = new VideoPlayerViewModel();
            videoPlayerViewModel.clone(data.get(index));
            items.add(videoPlayerViewModel);
        }
    }
    public void removeAll() {
        items.removeAll(items);
        notifyDataSetChanged();
    }

    public VideoPlayerViewModel getItem(int position) {
        return items.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LayoutVideoListItemBinding mLayoutVideoListItemBinding;

        public ViewHolder(LayoutVideoListItemBinding mLayoutVideoListItemBinding) {
            super(mLayoutVideoListItemBinding.getRoot());
            this.mLayoutVideoListItemBinding = mLayoutVideoListItemBinding;
        }
    }
}

