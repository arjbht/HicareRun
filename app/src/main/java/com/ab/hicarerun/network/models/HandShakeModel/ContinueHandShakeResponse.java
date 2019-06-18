package com.ab.hicarerun.network.models.HandShakeModel;

import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContinueHandShakeResponse {
    @SerializedName("IsSuccess")
    @Expose
    private Boolean isSuccess;
    @SerializedName("Data") @Expose
    private List<Tasks> data = null;
    @SerializedName("ErrorMessage")
    @Expose
    private String ErrorMessage;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public List<Tasks> getData() {
        return data;
    }

    public void setData(List<Tasks> data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
