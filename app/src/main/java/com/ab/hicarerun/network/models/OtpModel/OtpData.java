package com.ab.hicarerun.network.models.OtpModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by Arjun Bhatt on 6/21/2019.
 */
public class OtpData extends RealmObject {

    @SerializedName("resource_id")
    @Expose
    private Integer resourceId;
    @SerializedName("resource_name")
    @Expose
    private String resourceName;
    @SerializedName("region_name")
    @Expose
    private String regionName;
    @SerializedName("service_center_name")
    @Expose
    private String serviceCenterName;
    @SerializedName("external_reference_resourceid")
    @Expose
    private String externalReferenceResourceid;
    @SerializedName("skills")
    @Expose
    private String skills;
    @SerializedName("technician_mobile")
    @Expose
    private String technicianMobile;
    @SerializedName("ServiceCenterId")
    @Expose
    private String serviceCenterId;
    @SerializedName("RegionId")
    @Expose
    private String regionId;
    @SerializedName("user_profile_image")
    @Expose
    private String userProfileImage;
    @SerializedName("loginotp")
    @Expose
    private String loginotp;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getServiceCenterName() {
        return serviceCenterName;
    }

    public void setServiceCenterName(String serviceCenterName) {
        this.serviceCenterName = serviceCenterName;
    }

    public String getExternalReferenceResourceid() {
        return externalReferenceResourceid;
    }

    public void setExternalReferenceResourceid(String externalReferenceResourceid) {
        this.externalReferenceResourceid = externalReferenceResourceid;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getTechnicianMobile() {
        return technicianMobile;
    }

    public void setTechnicianMobile(String technicianMobile) {
        this.technicianMobile = technicianMobile;
    }

    public String getServiceCenterId() {
        return serviceCenterId;
    }

    public void setServiceCenterId(String serviceCenterId) {
        this.serviceCenterId = serviceCenterId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getLoginotp() {
        return loginotp;
    }

    public void setLoginotp(String loginotp) {
        this.loginotp = loginotp;
    }
}
