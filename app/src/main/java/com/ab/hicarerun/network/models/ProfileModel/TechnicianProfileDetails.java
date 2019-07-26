package com.ab.hicarerun.network.models.ProfileModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/26/2019.
 */
public class TechnicianProfileDetails {
    @SerializedName("IsSuccess") @Expose
    private Boolean IsSuccess;

    @SerializedName("Data") @Expose
    private Profile Data;

    @SerializedName("ErrorMessage") @Expose
    private String ErrorMessage;

    public Boolean getSuccess() {
        return IsSuccess;
    }

    public void setSuccess(Boolean success) {
        IsSuccess = success;
    }

    public Profile getData() {
        return Data;
    }

    public void setData(Profile data) {
        Data = data;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
