package com.ab.hicarerun.network.models.AttachmentModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostAttachmentResponse {

    @SerializedName("IsSuccess") @Expose
    private Boolean IsSuccess;

    @SerializedName("Data") @Expose
    private String Data;

    @SerializedName("ErrorMessage") @Expose
    private String ErrorMessage;

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

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
