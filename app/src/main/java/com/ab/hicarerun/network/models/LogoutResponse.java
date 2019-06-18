package com.ab.hicarerun.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutResponse {
    @SerializedName("IsSuccess") @Expose
    private Boolean IsSuccess;

    @SerializedName("Data") @Expose
    private Boolean Data;

    @SerializedName("ErrorMessage") @Expose
    private String ErrorMessage;


    public Boolean getSuccess() {
        return IsSuccess;
    }

    public void setSuccess(Boolean success) {
        IsSuccess = success;
    }

    public Boolean getData() {
        return Data;
    }

    public void setData(Boolean data) {
        Data = data;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
