package com.ab.hicarerun.network.models.UpdateAppModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/9/2019.
 */
public class UpdateResponse {
    @SerializedName("IsSuccess")
    @Expose
    private Boolean isSuccess;
    @SerializedName("Data")
    @Expose
    private UpdateData Data;
    @SerializedName("ResponseMessage")
    @Expose
    private String ResponseMessage;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public UpdateData getData() {
        return Data;
    }

    public void setData(UpdateData data) {
        Data = data;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        ResponseMessage = responseMessage;
    }
}
