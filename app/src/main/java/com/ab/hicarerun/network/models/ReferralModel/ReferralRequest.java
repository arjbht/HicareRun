package com.ab.hicarerun.network.models.ReferralModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReferralRequest {

    @SerializedName("TaskId") @Expose
    private String TaskId;
    @SerializedName("FirstName") @Expose
    private String FirstName;
    @SerializedName("LastName") @Expose
    private String LastName;
    @SerializedName("MobileNo") @Expose
    private String MobileNo;
    @SerializedName("AlternateMobileNo") @Expose
    private String AlternateMobileNo;
    @SerializedName("Email") @Expose
    private String Email;
    @SerializedName("InterestedService") @Expose
    private String InterestedService;

    public ReferralRequest() {
        TaskId = "NA";
        FirstName = "NA";
        LastName = "NA";
        MobileNo = "NA";
        AlternateMobileNo = "NA";
        Email = "NA";
        InterestedService = "NA";
    }



    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getAlternateMobileNo() {
        return AlternateMobileNo;
    }

    public void setAlternateMobileNo(String alternateMobileNo) {
        AlternateMobileNo = alternateMobileNo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getInterestedService() {
        return InterestedService;
    }

    public void setInterestedService(String interestedService) {
        InterestedService = interestedService;
    }
}
