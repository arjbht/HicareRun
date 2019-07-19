package com.ab.hicarerun.network.models.AttendanceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 6/17/2019.
 */
public class AttendanceRequest {
    @SerializedName("TechId") @Expose
    private String TechId;
    @SerializedName("InTime") @Expose private String InTime;
    @SerializedName("InTime_SC_Distance") @Expose private Double InTime_SC_Distance;
    @SerializedName("OutTime") @Expose private String OutTime;
    @SerializedName("OutTime_SC_Distance") @Expose private Double OutTime_SC_Distance;
    @SerializedName("Latitude") @Expose private String Latitude;
    @SerializedName("Longitude") @Expose private String Longitude;
    @SerializedName("UserId") @Expose private String UserId;
    @SerializedName("UserName") @Expose private String UserName;
    @SerializedName("DeviceIMEINumber") @Expose private String DeviceIMEINumber;
    @SerializedName("DeviceTime") @Expose private String DeviceTime;
    @SerializedName("BatteryStatistics") @Expose private String BatteryStatistics;
    @SerializedName("PhoneMake") @Expose private String PhoneMake;
    @SerializedName("DeviceName") @Expose private String DeviceName;
    @SerializedName("IsLoggedIn") @Expose private Boolean IsLoggedIn;
    @SerializedName("IsGPSConnected") @Expose private Boolean IsGPSConnected;
    @SerializedName("ResourceImage") @Expose private String ResourceImage;
    @SerializedName("SkipAttendance") @Expose private Boolean SkipAttendance;

    public AttendanceRequest() {
        TechId = "";
        InTime = "";
        InTime_SC_Distance = 0.0;
        OutTime = "";
        OutTime_SC_Distance = 0.0;
        Latitude = "";
        Longitude = "";
        UserId = "";
        UserName = "";
        DeviceIMEINumber = "";
        DeviceTime = "";
        BatteryStatistics = "";
        PhoneMake = "";
        DeviceName = "";
        IsLoggedIn = false;
        IsGPSConnected = false;
        ResourceImage = "";
        SkipAttendance = false;

    }

    public String getTechId() {
        return TechId;
    }

    public void setTechId(String techId) {
        TechId = techId;
    }

    public String getInTime() {
        return InTime;
    }

    public void setInTime(String inTime) {
        InTime = inTime;
    }

    public Double getInTime_SC_Distance() {
        return InTime_SC_Distance;
    }

    public void setInTime_SC_Distance(Double inTime_SC_Distance) {
        InTime_SC_Distance = inTime_SC_Distance;
    }

    public void setOutTime_SC_Distance(Double outTime_SC_Distance) {
        OutTime_SC_Distance = outTime_SC_Distance;
    }

    public Double getOutTime_SC_Distance() {
        return OutTime_SC_Distance;
    }

    public String getOutTime() {
        return OutTime;
    }

    public void setOutTime(String outTime) {
        OutTime = outTime;
    }


    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDeviceIMEINumber() {
        return DeviceIMEINumber;
    }

    public void setDeviceIMEINumber(String deviceIMEINumber) {
        DeviceIMEINumber = deviceIMEINumber;
    }

    public String getDeviceTime() {
        return DeviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        DeviceTime = deviceTime;
    }

    public String getBatteryStatistics() {
        return BatteryStatistics;
    }

    public void setBatteryStatistics(String batteryStatistics) {
        BatteryStatistics = batteryStatistics;
    }

    public String getPhoneMake() {
        return PhoneMake;
    }

    public void setPhoneMake(String phoneMake) {
        PhoneMake = phoneMake;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public Boolean getLoggedIn() {
        return IsLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        IsLoggedIn = loggedIn;
    }

    public Boolean getGPSConnected() {
        return IsGPSConnected;
    }

    public void setGPSConnected(Boolean GPSConnected) {
        IsGPSConnected = GPSConnected;
    }

    public String getResourceImage() {
        return ResourceImage;
    }

    public void setResourceImage(String resourceImage) {
        ResourceImage = resourceImage;
    }

    public Boolean getSkipAttendance() {
        return SkipAttendance;
    }

    public void setSkipAttendance(Boolean skipAttendance) {
        SkipAttendance = skipAttendance;
    }
}
