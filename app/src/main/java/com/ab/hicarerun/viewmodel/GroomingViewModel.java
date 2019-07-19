package com.ab.hicarerun.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroom;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/18/2019.
 */
public class GroomingViewModel implements Parcelable {
    private String TechnicianId;
    private String TechnicianName;
    private String EmployeeCode;
    private String MobileNo;
    private String ImageUrl;
    private String Image;

    public GroomingViewModel() {
        TechnicianId = "NA";
        TechnicianName = "NA";
        EmployeeCode = "NA";
        MobileNo = "NA";
        ImageUrl = "NA";
        Image = "NA";
    }

    protected GroomingViewModel(Parcel in) {
        TechnicianId = in.readString();
        TechnicianName = in.readString();
        EmployeeCode = in.readString();
        MobileNo = in.readString();
        ImageUrl = in.readString();
        Image = in.readString();
    }

    public static final Creator<GroomingViewModel> CREATOR = new Creator<GroomingViewModel>() {
        @Override
        public GroomingViewModel createFromParcel(Parcel in) {
            return new GroomingViewModel(in);
        }

        @Override
        public GroomingViewModel[] newArray(int size) {
            return new GroomingViewModel[size];
        }
    };

    public String getTechnicianId() {
        return TechnicianId;
    }

    public void setTechnicianId(String technicianId) {
        TechnicianId = technicianId;
    }

    public String getTechnicianName() {
        return TechnicianName;
    }

    public void setTechnicianName(String technicianName) {
        TechnicianName = technicianName;
    }

    public String getEmployeeCode() {
        return EmployeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        EmployeeCode = employeeCode;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TechnicianId);
        dest.writeString(TechnicianName);
        dest.writeString(EmployeeCode);
        dest.writeString(MobileNo);
        dest.writeString(ImageUrl);
        dest.writeString(Image);
    }


    public void clone(TechGroom groom) {
        this.TechnicianId = groom.getTechnicianId();
        this.TechnicianName = groom.getTechnicianName();
        this.EmployeeCode = groom.getEmployeeCode();
        this.MobileNo = groom.getMobileNo();
        this.ImageUrl = groom.getImageUrl();
        this.Image = groom.getImage();

    }
}
