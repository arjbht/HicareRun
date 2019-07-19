package com.ab.hicarerun.network.models.TechnicianGroomingModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/17/2019.
 */
public class TechGroom {
    @SerializedName("TechnicianId")
    @Expose
    private String TechnicianId;

    @SerializedName("TechnicianName")
    @Expose
    private String TechnicianName;

    @SerializedName("EmployeeCode")
    @Expose
    private String EmployeeCode;

    @SerializedName("MobileNo")
    @Expose
    private String MobileNo;

    @SerializedName("ImageUrl")
    @Expose
    private String ImageUrl;

    @SerializedName("Image")
    @Expose
    private String Image;


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
}
