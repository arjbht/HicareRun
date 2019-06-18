package com.ab.hicarerun.network.models.HandShakeModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HandShake implements Parcelable {
    @SerializedName("Text")
    @Expose
    private String Text;
    @SerializedName("Value")
    @Expose
    private String Value;
    @SerializedName("IsSelected")
    @Expose
    private String IsSelected;
    @SerializedName("UserId")
    @Expose
    private String UserId;
    @SerializedName("CreatedBy")
    @Expose
    private String CreatedBy;
    @SerializedName("PageType")
    @Expose
    private String PageType;


    protected HandShake(Parcel in) {
        Text = in.readString();
        Value = in.readString();
        IsSelected = in.readString();
        UserId = in.readString();
        CreatedBy = in.readString();
        PageType = in.readString();
    }

    public static final Creator<HandShake> CREATOR = new Creator<HandShake>() {
        @Override
        public HandShake createFromParcel(Parcel in) {
            return new HandShake(in);
        }

        @Override
        public HandShake[] newArray(int size) {
            return new HandShake[size];
        }
    };

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getIsSelected() {
        return IsSelected;
    }

    public void setIsSelected(String isSelected) {
        IsSelected = isSelected;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getPageType() {
        return PageType;
    }

    public void setPageType(String pageType) {
        PageType = pageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Text);
        dest.writeString(Value);
        dest.writeString(IsSelected);
        dest.writeString(UserId);
        dest.writeString(CreatedBy);
        dest.writeString(PageType);
    }
}
