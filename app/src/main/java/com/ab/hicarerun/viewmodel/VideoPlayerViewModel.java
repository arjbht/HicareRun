package com.ab.hicarerun.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TrainingModel.Videos;

/**
 * Created by Arjun Bhatt on 7/16/2019.
 */
public class VideoPlayerViewModel implements Parcelable {
    private Integer id;
    private String VideoTitle;
    private String VideoDescription;
    private String VideoType;
    private String VideoThumbnail;
    private String VideoUrl;
    private String Created_On;

    public VideoPlayerViewModel() {
        id = 0;
        VideoTitle = "NA";
        VideoDescription = "NA";
        VideoType = "NA";
        VideoThumbnail = "NA";
        VideoUrl = "NA";
        Created_On = "NA";
    }



    public static final Creator<ReferralListViewModel> CREATOR = new Creator<ReferralListViewModel>() {
        @Override
        public ReferralListViewModel createFromParcel(Parcel in) {
            return new ReferralListViewModel(in);
        }

        @Override
        public ReferralListViewModel[] newArray(int size) {
            return new ReferralListViewModel[size];
        }
    };


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVideoTitle() {
        return VideoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        VideoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return VideoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        VideoDescription = videoDescription;
    }

    public String getVideoType() {
        return VideoType;
    }

    public void setVideoType(String videoType) {
        VideoType = videoType;
    }

    public String getVideoThumbnail() {
        return VideoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        VideoThumbnail = videoThumbnail;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }


    public String getCreated_On() {
        return Created_On;
    }

    public void setCreated_On(String created_On) {
        Created_On = created_On;
    }

    public static Creator<ReferralListViewModel> getCREATOR() {
        return CREATOR;
    }

    public void clone(Videos videos) {
        this.id = videos.getId();
        this.VideoTitle = videos.getVideoTitle();
        this.VideoDescription = videos.getVideoDescription();
        this.Created_On = videos.getCreatedOn();
        this.VideoThumbnail = videos.getVideoThumbnail();
        this.VideoType= videos.getVideoType();
        this.VideoUrl = videos.getVideoUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
