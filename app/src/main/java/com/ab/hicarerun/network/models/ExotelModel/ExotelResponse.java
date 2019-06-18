package com.ab.hicarerun.network.models.ExotelModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExotelResponse {
    @SerializedName("IsSuccess") @Expose
    private Boolean IsSuccess;

    @SerializedName("Data") @Expose
    private String Data;

    @SerializedName("ResponseMessage") @Expose
    private String ResponseMessage;


    public Boolean getSuccess() {
        return IsSuccess;
    }

    public void setSuccess(Boolean success) {
        IsSuccess = success;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        ResponseMessage = responseMessage;
    }
}
